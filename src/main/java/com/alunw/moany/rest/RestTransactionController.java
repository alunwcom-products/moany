package com.alunw.moany.rest;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alunw.moany.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alunw.moany.model.Transaction;
import com.alunw.moany.model.TransactionType;
import com.alunw.moany.repository.TransactionRepository;
import com.alunw.moany.services.AccountService;
import com.alunw.moany.services.BudgetItemService;
import com.alunw.moany.services.Utilities;

@RestController
@RequestMapping("/rest/transactions/v2/")
public class RestTransactionController {
	
	@Autowired
	private TransactionRepository transactionRepo;
	
	@Autowired
	private BudgetItemService budgetService;
	
	@Autowired
	private AccountService accountService;
	
	private static Logger logger = LoggerFactory.getLogger(RestTransactionController.class);
	
	/**
	 * TODO Returns 'real' transactions (as stored in database).
	 * 
	 * @param accountNumbers
	 * @param startDateStr
	 * @param endDateStr
	 * @return
	 */
	@RequestMapping(value={"/"}, method = RequestMethod.GET)
	@CrossOrigin("*")
	public List<Transaction> getTransactions(
			@RequestParam(name="acc", required=false) String accountNumbers,
			@RequestParam(value="startDate", required = false) String startDateStr,
			@RequestParam(value="endDate", required = false) String endDateStr) {
		
		logger.info("getTransactions({}, {}, {})", accountNumbers, startDateStr, endDateStr);
		
		LocalDate startDate = Utilities.getDateFromString(startDateStr);
		if (startDate == null) {
			startDate = Utilities.getEarliestDate();
		}
		
		LocalDate endDate = Utilities.getDateFromString(endDateStr);
		if (endDate == null) {
			endDate = Utilities.getLatestDate();
		}
		
		return transactionRepo.findTransactionsByAccount(accountService.findByIdIn(accountNumbers), startDate, endDate);
	}
	
	/**
	 * TODO Returns 'virtual' budgeting transactions (not stored in database).
	 * 
	 * @param accountNumbers
	 * @param startDateStr
	 * @param endDateStr
	 * @return
	 */
	@RequestMapping(value={"/budget"}, method = RequestMethod.GET)
	@CrossOrigin("*")
	public List<Transaction> getBudgetTransactions(
			@RequestParam(name="acc", required=false) String accountNumbers,
			@RequestParam(value="startDate", required = false) String startDateStr,
			@RequestParam(value="endDate", required = false) String endDateStr) {
		
		logger.info("getBudgetTransactions({}, {}, {})", accountNumbers, startDateStr, endDateStr);
		
		LocalDate startDate = Utilities.getDateFromString(startDateStr);
		if (startDate == null) {
			startDate = Utilities.getEarliestDate();
		}
		
		LocalDate endDate = Utilities.getDateFromString(endDateStr);
		if (endDate == null) {
			endDate = Utilities.getLatestDate();
		}
		
		return budgetService.generateBudgetingTransactionsByAccount(accountService.findByIdIn(accountNumbers), startDate, endDate);
	}
	
	/**
	 * TODO Returns merged and sorted real and virtual transactions.
	 * 
	 * @param monthStr
	 * @param accountNumbersStr
	 * @param excludeBudgetItemsStr
	 * @return
	 */
	@RequestMapping(value={"/month/{month}"}, method = RequestMethod.GET)
	@CrossOrigin("*")
	public ResponseEntity<Map<String, Object>> getTransactionsByMonth(
			@PathVariable(name="month", required=true) String monthStr,
			@RequestParam(name="acc", required = false) String accountNumbersStr,
			@RequestParam(name="excludeBudgetItems", required = false) String excludeBudgetItemsStr) {
		
		logger.info("getTransactionsByMonth({}, {}, {})", monthStr, accountNumbersStr, excludeBudgetItemsStr);
		
		boolean excludeBudgetItems = Boolean.parseBoolean(excludeBudgetItemsStr);
		
		YearMonth month = null;
		try {
			month = YearMonth.parse(monthStr);
		} catch (DateTimeParseException e) {
			logger.error("Unable to parse month ({}), using current month.", monthStr);
			month = YearMonth.now();
		}
		
		// get real transactions
		// TODO add service methods to access results directly with YearMonth
		List<Transaction> results = transactionRepo.findTransactionsByAccount(accountService.findByIdIn(accountNumbersStr),
				LocalDate.of(month.getYear(), month.getMonth(), 1), 
				LocalDate.of(month.getYear(), month.getMonth(), month.getMonth().length(month.isLeapYear())));
		
		// merge virtual transactions
		if (!excludeBudgetItems) {
			results.addAll(budgetService.generateBudgetingTransactionsByAccount(
				accountService.findByIdIn(accountNumbersStr), 
				LocalDate.of(month.getYear(), month.getMonth(), 1),
				LocalDate.of(month.getYear(), month.getMonth(), month.getMonth().length(month.isLeapYear()))));
		}
		
		Collections.sort(results, new Comparator<Transaction>() {
			@Override
			public int compare(Transaction t1, Transaction t2) {
				
				if (t1.getTransactionDate().isBefore(t2.getTransactionDate())) {
					return -1;
				}
				
				if (t1.getTransactionDate().isAfter(t2.getTransactionDate())) {
					return 1;
				}
				
				// transaction date is the same - use secondary sort key
				
				if (t1.getSourceRow() < t2.getSourceRow()) {
					return -1;
				}
				
				if (t1.getSourceRow() > t2.getSourceRow()) {
					return 1;
				}
				
				// last ditch comparison - description
				return t1.getDescription().compareToIgnoreCase(t2.getDescription());
			}
		});
		
		Map<String, Object> map = new HashMap<>();
		map.put("transactions", results);
		map.put("startingBalance", accountService.findBroughtForwardBalance(accountNumbersStr, LocalDate.of(month.getYear(), month.getMonth(), 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
		
		return ResponseEntity.ok(map);
	}
	
	/**
	 * PUT an transaction record in the repository, returning the transaction. If the id is blank, then a new 
	 * transaction record is created, and the id is included in the returned transaction. If the id is specified, then 
	 * this transaction will be updated and the transaction returned. If the specified id does not exist, then the 
	 * record is not stored, and an error response is returned.
	 * 
	 * If there is a validation error, then the record is not stored, and an error response returned.
	 * 
	 * @param transaction A transaction
	 * @param result 
	 * @param model
	 * @return A transaction.
	 */
	@RequestMapping(value={"/"}, method = RequestMethod.PUT)
	public Transaction putTransactionById(@Validated @RequestBody Transaction transaction, BindingResult result, Model model) {
		logger.info("putTransactionById()");
		// TODO testing
		if (result != null) {
			logger.info("errors = " + result.getErrorCount());
			logger.info("errors = " + result.getAllErrors());
		}
		
		// TODO Validate data
		if (transaction != null) {
			logger.debug("account = " + transaction);
		}
		// Store data
		return transactionRepo.save(transaction);
	}
	
	@RequestMapping(value={"/id/{transactionId}"}, method = RequestMethod.DELETE)
	public void deleteTransaction(@PathVariable String transactionId) {
		
		logger.info("deleting transaction id = {}", transactionId);
		transactionRepo.deleteById(transactionId);
	}
	
	@RequestMapping(value={"/types"}, method = RequestMethod.GET)
	public List<TransactionType> getTransactionTypes() {
		
		logger.info("getTransactionTypes()");
		
		return Arrays.asList(TransactionType.values());
	}
	

}

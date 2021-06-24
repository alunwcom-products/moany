package com.alunw.moany.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.alunw.moany.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alunw.moany.model.Account;
import com.alunw.moany.model.Transaction;
import com.alunw.moany.repository.AccountRepository;

@Component
public class AccountService {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private TransactionRepository transactionRepo;
	
	@Autowired
	private BudgetItemService budgetItemService;
	
	@PersistenceContext
	private EntityManager em;
	
	@PostConstruct
	public void init() {}
	
	/**
	 * Wrapper to AccountRepository.findByIdIn() method, converting delimited string to list of ids.
	 * 
	 * If blank string supplied, all accounts will be returned.
	 * 
	 * @param commaDelimitedIds
	 * @return
	 */
	public List<Account> findByIdIn(String commaDelimitedIds) {
		
		logger.debug("findByIdIn({})", commaDelimitedIds);
		
		if (commaDelimitedIds == null || commaDelimitedIds.isEmpty()) {
			return accountRepo.findAll();
		}
		String[] ids = commaDelimitedIds.split(",");
		return accountRepo.findByIdIn(Arrays.asList(ids));
	}
	
	/**
	 * TODO
	 * 
	 * @param accountIds
	 * @param date
	 * @return
	 */
	public BigDecimal findBroughtForwardBalance(String accountIds, String date) {
		
		logger.info("findBroughtForwardBalance({}, {})", accountIds, date);
		
		List<Account> accounts = findByIdIn(accountIds);
		
		
		Optional<LocalDate> dateResult = Optional.empty();
		if (date != null) {
			dateResult = Utilities.parseDate(date, "yyyy-MM-dd");
		}
		if (!dateResult.isPresent()) {
			// set default
			dateResult = Optional.of(Utilities.getLatestDate());
		}
		
		return findBroughtForwardBalance(accounts, dateResult.get());
	}
	
	/**
	 * TODO 
	 * Calculate an account balance for any given date (i.e. brought forward balance at 00:00).
	 * Needs to include virtual budgeting transactions.
	 * 
	 * NEXT - if we include a start date and balance, this could be an incremental calculation.
	 */
	@Transactional
	public BigDecimal findBroughtForwardBalance(List<Account> accounts, LocalDate date) {
		
		logger.info("findBroughtForwardBalance({}, {})", accounts, date);
		
		if (accounts == null || date == null) {
			throw new NullPointerException("Account and date parameters cannot be null.");
		}
		
		// TODO drop account start date!!! can have user start date - but should be common start date (if used)
		LocalDate accountStartDate = Utilities.getEarliestDate();
//		if (account.getStartDate() != null) {
//			accountStartDate = account.getStartDate();
//		}
		
		// As we want the brought forward balance - we will only retrieve transactions *up to* the previous day
		LocalDate endDate = date.minus(1, ChronoUnit.DAYS);
		
		// TODO if endDate before account startDate return zero
//		if (endDate.isBefore(accountStartDate)) {
//			return new BigDecimal(0);
//		}
		
		// calculate balance from account start date
		BigDecimal balance = new BigDecimal(0);
		for (Account account : accounts) {
			balance = balance.add(account.getStartBalance());
			logger.debug("balance [acc = {}, total = {}]", account.getAccNum(), balance);
		}
		List<Transaction> transactions = transactionRepo.findTransactionsByAccount(accounts, accountStartDate, endDate);
		for (Transaction t : transactions) {
			
			// amount may be null
			if (t.getNetAmount() != null) {
				balance = balance.add(t.getNetAmount());
			}
			
			logger.debug("transaction [date = {}, desc = {}, net amount = {}, running balance = {}]", t.getTransactionDate(), t.getDescription(), t.getNetAmount(), balance);
		}
		
		// calculate adjustment for virtual budgeting transactions
		List<Transaction> budgetTransactions = budgetItemService.generateBudgetingTransactionsByAccount(accounts, accountStartDate, endDate);
		for (Transaction t : budgetTransactions) {
			
			// amount should not be null - but check anyway
			if (t.getNetAmount() != null) {
				balance = balance.add(t.getNetAmount());
			}
			
			logger.debug("transaction [date = {}, desc = {}, net amount = {}, running balance = {}]", t.getTransactionDate(), t.getDescription(), t.getNetAmount(), balance);
		}
		
		return balance;
	}
}

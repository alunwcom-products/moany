package com.alunw.moany.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.TypedQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alunw.moany.model.Account;
import com.alunw.moany.model.BudgetItem;
import com.alunw.moany.model.Transaction;
import com.alunw.moany.model.TransactionType;
import com.alunw.moany.repository.BudgetItemRepository;

/**
 * Budget item service - to return 'virtual' budgeting transactions.
 */
@Component
public class BudgetItemService {

	// TODO should this be configurable in application.properties?
	public final static int MAXIMUM_MONTH_RANGE = 120;

	private static final Logger logger = LoggerFactory.getLogger(BudgetItemService.class);

	@Autowired
	private BudgetItemRepository budgetItemRepo;

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void updateBudgetItemStartDate(String budgetItemId, LocalDate afterDate) {

		logger.info("updateBudgetItemStartDate({}, {})", budgetItemId, afterDate);

		// check parameters specified
		if (budgetItemId == null || afterDate == null) {
			logger.warn("One or more parameters missing - no action.");
			return;
		}

		Optional<BudgetItem> result = budgetItemRepo.findById(budgetItemId);

		if (!result.isPresent()) {
			logger.warn("No matching budget item found ({}) - no action.", budgetItemId);
			return;
		}

		BudgetItem budgetItem = result.get();
		// Increment date by 1 day
		LocalDate newStartDate = afterDate.plus(1, ChronoUnit.DAYS);
		logger.debug("Setting new budget item start date of {}", newStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		budgetItem.setStartDate(newStartDate);

		budgetItemRepo.save(budgetItem);
	}

	@Transactional(readOnly = true)
	public List<Transaction> generateBudgetingTransactionsByAccount(List<Account> accounts, LocalDate startDate, LocalDate endDate) {

		logger.info("generateBudgetingTransactionsByAccount({}, {}, {})", accounts, startDate, endDate);

		List<Transaction> results = new ArrayList<>();

		// check parameters specified
		if (startDate == null || endDate == null || accounts == null || accounts.isEmpty()) {
			logger.info("One or more parameters missing - returning empty list.");
			return results;
		}

		// find budget items in date range
		TypedQuery<BudgetItem> typedQuery = em.createQuery("from BudgetItem "
				+ "where account in :acc "
				+ "and startDate <= :endDate "
				+ "and (endDate >= :startDate or endDate is null) "
				+ "order by startDate asc", BudgetItem.class);
		typedQuery.setParameter("acc", accounts);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		List<BudgetItem> budgets = typedQuery.getResultList();

		// generate virtual transactions
		List<YearMonth> monthRange = getMonthRange(startDate, endDate);
		for (BudgetItem item : budgets) {
			if (item.getPeriodType() == ChronoUnit.WEEKS) {
				processWeeklyBudgetItem(item, monthRange, results);
			} else if (item.getPeriodType() == ChronoUnit.MONTHS) {
				processMonthlyBudgetItem(item, monthRange, results);
			} else {
				logger.error("Unexpected budget item period value [{}]. Skipping.", item.getPeriodType());
			}
		}

		logger.debug("Generated {} budget transaction(s)", results.size());
		return results;
	}

	private void processMonthlyBudgetItem(BudgetItem item, List<YearMonth> monthRange, List<Transaction> results) {
		for (YearMonth period : monthRange) {

			// TODO is in budget item range?

			LocalDate transactionDate = null;
			// Get budget item day of month for this month range
			int dayOfMonth = item.getDayOfPeriod();
			if (dayOfMonth < 1) {
				// use first day of month
				transactionDate = LocalDate.of(period.getYear(), period.getMonth(), 1);
			} else if (dayOfMonth < period.getMonth().length(period.isLeapYear())) {
				// use budget item day of month
				transactionDate = LocalDate.of(period.getYear(), period.getMonth(), dayOfMonth);
			} else {
				// else use last day of month
				transactionDate = LocalDate.of(period.getYear(), period.getMonth(), period.getMonth().length(period.isLeapYear()));
			}

			// check date is within budget item range
			if (!transactionDate.isBefore(item.getStartDate())) {
				if (item.getEndDate() == null || !transactionDate.isAfter(item.getEndDate())) {
					// create budget transaction
					Transaction t = new Transaction();
					t.setAccount(item.getAccount());
					t.setCategory(item.getCategory());
					t.setDescription(item.getDescription());
					t.setNetAmount(item.getAmount());
					t.setSourceType(TransactionType.BUDGETING);
					t.setTransactionDate(transactionDate);
					t.setBudgetItem(item);
					results.add(t);
				}
			}
		}
	}

	private void processWeeklyBudgetItem(BudgetItem item, List<YearMonth> monthRange, List<Transaction> results) {
		for (YearMonth month : monthRange) {

			logger.debug("processWeeklyBudgetItem(month = {}, item start = {})", month, item.getStartDate());

			// start and end of month being processed
			LocalDate firstDayOfMonth = LocalDate.of(month.getYear(), month.getMonth(), 1);
			LocalDate lastDayOfMonth = LocalDate.of(month.getYear(), month.getMonth(), month.getMonth().length(month.isLeapYear()));

			// adjusted day of week for budget item
			int dayOfWeek = item.getDayOfPeriod();
			if (dayOfWeek < 1) {
				// use first day of week (Monday)
				dayOfWeek = 1;
			} else if (dayOfWeek > 7 ) {
				// use last day of week (Sunday)
				dayOfWeek = 7;
			}
			logger.debug("day of week = {}", dayOfWeek);

			// Calculate first transaction

			LocalDate transactionDate = firstDayOfMonth;
			if (item.getStartDate().isAfter(transactionDate)) {
				transactionDate = item.getStartDate();
			}
			logger.debug("start = {} [{}]", transactionDate, transactionDate.getDayOfWeek().getValue());

			if (dayOfWeek > transactionDate.getDayOfWeek().getValue()) {
				transactionDate = transactionDate.plusDays(dayOfWeek - transactionDate.getDayOfWeek().getValue());
				logger.debug("adjusted start #1 = {} [{}]", transactionDate, transactionDate.getDayOfWeek().getValue());
			} else if (dayOfWeek < transactionDate.getDayOfWeek().getValue()) {
				transactionDate = transactionDate.minusDays(transactionDate.getDayOfWeek().getValue() - dayOfWeek).plusWeeks(1);
				logger.debug("adjusted start #2 = {} [{}]", transactionDate, transactionDate.getDayOfWeek().getValue());
			}

			while (!transactionDate.isAfter(lastDayOfMonth)) {
				logger.debug("new = {}", transactionDate);
				// create budget transaction
				Transaction t = new Transaction();
				t.setAccount(item.getAccount());
				t.setCategory(item.getCategory());
				t.setDescription(item.getDescription());
				t.setNetAmount(item.getAmount());
				t.setSourceType(TransactionType.BUDGETING);
				t.setTransactionDate(transactionDate);
				t.setBudgetItem(item);
				results.add(t);

				// increment transaction date
				transactionDate = transactionDate.plusWeeks(1);
				logger.debug("next = {}", transactionDate);
			}
		}
	}

	private List<YearMonth> getMonthRange(LocalDate startDate, LocalDate endDate) {

		// Create list of months in date range
		List<YearMonth> monthRange = new ArrayList<>();
		YearMonth yearMonth = YearMonth.from(startDate);
		monthRange.add(yearMonth);
		while (yearMonth.isBefore(YearMonth.from(endDate))) {
			// TODO no limit - need all virtual transactions to calculate balances
//			if (monthRange.size() >= MAXIMUM_MONTH_RANGE) {
//				// Already have maximum number of months in range
//				logger.warn("budget transaction month range exceeds maximum ({}), only first {} month(s) included in summary.", MAXIMUM_MONTH_RANGE, MAXIMUM_MONTH_RANGE);
//				break;
//			}
			yearMonth = yearMonth.plus(1, ChronoUnit.MONTHS);
			monthRange.add(yearMonth);
		}
		return monthRange;
	}
}

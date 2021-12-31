package com.alunw.moany.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alunw.moany.model.Account;
import com.alunw.moany.model.Category;
import com.alunw.moany.repository.CategoryRepository;

/**
 * Category service methods.
 */
@Component
public class CategoryService {
	
	private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
	
	public static final String PLANNED_AMOUNT_KEY = "plannedAmount";
	public static final String PLANNED_BALANCE_KEY = "plannedBalance";
	public static final String ACTUAL_AMOUNT_KEY = "actualAmount";
	public static final String ACTUAL_BALANCE_KEY = "actualBalance";
	public static final String SURPLUS_AMOUNT_KEY = "surplusAmount";
	public static final String SURPLUS_BALANCE_KEY = "surplusBalance";
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * TODO
	 * 
	 * return a list of category summary map objects, for one or more accounts - currently for 12 months from the specified start month
	 * where each category map contains:
	 * - category info: id, name, fullName
	 * - plannedAmount: map of planned (budgeted) amounts, with month as key
	 * - plannedBalance: map of actual balance (total of planned amount month on month), with month as key
	 * - actualAmount: map of actual amounts (sum of transaction), with month as key
	 * - actualBalance: map of actual balance (total of actual amount month on month), with month as key
	 * - surplusAmount: map of surplus amounts (actual amount - planned amount), with month as key
	 * - surplusBalance: map of actual balance (total of surplus amount month on month), with month as key
	 * 
	 * In addition, 2 'pseudo' categories will be returned:
	 * - 'uncategorized': any transactions not assigned to a category. (planned amounts will always be null)
	 * - 'totals': combined totals of all planned and actual amounts. (including transactions without a category)
	 * 
	 * @param accounts
	 * @param startMonth
	 * @return
	 */
	public List<Map<String, Object>> getCategorySummary(List<Account> accounts, YearMonth startMonth) {
		
		logger.info("getCategorySummary({}, {})", accounts, startMonth);
		
		List<Map<String, Object>> categorySummaries = new ArrayList<>();
		
		if (startMonth == null || accounts == null || accounts.isEmpty()) {
			return categorySummaries;
		}
		
		List<YearMonth> months = getMonths(startMonth, 12);
		
		// Get transactions for each month for each category 
		List<Category> categories = categoryRepo.findAll();
		Collections.sort(categories);
		for (Category category : categories) {
			categorySummaries.add(processCategory(category, accounts, months));
		}
		
		// TODO Include uncategorized transactions
		categorySummaries.add(processCategory(Category.NULL_CATEGORY, accounts, months));
		
		// TODO process totals
		addTotals(categorySummaries, months);
		//categorySummaries.add(processTotals(categorySummaries, months));
		
		return categorySummaries;
	}
	
	/**
	 * Returns a list of YearMonth objects, starting with the supplied start month, and with the specified number of 
	 * months.
	 * 
	 * @param startMonth
	 * @param numberOfMonths
	 * @return List<YearMonth>
	 */
	private List<YearMonth> getMonths(YearMonth startMonth, int numberOfMonths) {
		List<YearMonth> months = new ArrayList<>();
		YearMonth yearMonth = startMonth;
		for (int i = 0; i < numberOfMonths; i++) {
			months.add(yearMonth);
			yearMonth = yearMonth.plusMonths(1);
		}
		return months;
	}
	
	/**
	 * TODO
	 * Returns a category summary map.
	 * 
	 * 
	 * @param category
	 * @param accounts
	 * @param months
	 * @return
	 */
	private Map<String, Object> processCategory(Category category, List<Account> accounts, List<YearMonth> months) {
		
		Map<String, Object> categorySummary = new TreeMap<>();
		
		// Add basic category content
		categorySummary.put("id", category.getId());
		categorySummary.put("name", category.getName());
		categorySummary.put("fullName", category.getFullName());
		
		addPlannedAmountForMonths(category, months, categorySummary);
		addPlannedBalanceForMonths(category, months, categorySummary);
		addActualAmountForMonths(category, months, accounts, categorySummary);
		addActualBalanceForMonths(category, months, categorySummary);
		addSurplusAmountForMonths(category, months, categorySummary);
		addSurplusBalanceForMonths(category, months, categorySummary);
		
		return categorySummary;
	}
	
	/**
	 * Adds the planned (budget) amount map for the category to the category summary map.
	 * 
	 * @param category
	 * @param months
	 * @param categorySummary
	 */
	private void addPlannedAmountForMonths(Category category, List<YearMonth> months, Map<String, Object> categorySummary) {
		Map<YearMonth, BigDecimal> resultMap = new TreeMap<>();
		BigDecimal amount = null;
		for (YearMonth month : months) {
			if (category != Category.NULL_CATEGORY) {
				amount = category.getCategoryBudget(month);
				if (amount != null) {
					resultMap.put(month, amount);
				}
			}
		}
		categorySummary.put(PLANNED_AMOUNT_KEY, resultMap);
	}
	
	/**
	 * Adds the planned (budget) balance map for the category to the category summary map.
	 * 
	 * NOTE #1: This method uses the planned amount map, and assumes this map has already been added to the category summary.
	 * NOTE #2: This method assumes the list of months are in ascending chronological order.
	 * 
	 * @param category
	 * @param months
	 * @param categorySummary
	 */
	private void addPlannedBalanceForMonths(Category category, List<YearMonth> months, Map<String, Object> categorySummary) {
		Map<YearMonth, Object> resultMap = new TreeMap<>();
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal amount = null;
		for (YearMonth month : months) {
			amount = ((Map<YearMonth, BigDecimal>) categorySummary.get(PLANNED_AMOUNT_KEY)).get(month);
			if (amount == null) {
				amount = BigDecimal.ZERO;
			}
			total = total.add(amount);
			resultMap.put(month, total);
		}
		categorySummary.put(PLANNED_BALANCE_KEY, resultMap);
	}
	
	private void addActualAmountForMonths(Category category, List<YearMonth> months, List<Account> accounts, Map<String, Object> categorySummary) {
		Map<YearMonth, Object> resultMap = new TreeMap<>();
		for (YearMonth month : months) {
			BigDecimal amount = getActualAmount(category, accounts, month.atDay(1), month.atEndOfMonth());
			if (amount != null) {
				resultMap.put(month, amount);
			}
		}
		categorySummary.put(ACTUAL_AMOUNT_KEY, resultMap);
	}
	
	/**
	 * TODO combine with other totals?
	 * 
	 * Adds the actual balance map for the category to the category summary map.
	 * 
	 * NOTE #1: This method uses the actual amount map, and assumes this map has already been added to the category summary.
	 * NOTE #2: This method assumes the list of months are in ascending chronological order.
	 * 
	 * @param category
	 * @param months
	 * @param categorySummary
	 */
	private void addActualBalanceForMonths(Category category, List<YearMonth> months, Map<String, Object> categorySummary) {
		Map<YearMonth, Object> resultMap = new TreeMap<>();
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal amount = null;
		for (YearMonth month : months) {
			amount = ((Map<YearMonth, BigDecimal>) categorySummary.get(ACTUAL_AMOUNT_KEY)).get(month);
			if (amount == null) {
				amount = BigDecimal.ZERO;
			}
			total = total.add(amount);
			resultMap.put(month, total);
		}
		categorySummary.put(ACTUAL_BALANCE_KEY, resultMap);
	}
	
	private void addSurplusAmountForMonths(Category category, List<YearMonth> months, Map<String, Object> categorySummary) {
		Map<YearMonth, Object> resultMap = new TreeMap<>();
		for (YearMonth month : months) {
			
			BigDecimal planned = ((Map<YearMonth, BigDecimal>) categorySummary.get(PLANNED_AMOUNT_KEY)).get(month);
			BigDecimal actual = ((Map<YearMonth, BigDecimal>) categorySummary.get(ACTUAL_AMOUNT_KEY)).get(month);
			
			if (planned == null && actual == null) {
				continue;
			}
			
			// surplus amount (actual - planned) 
			if (planned == null) {
				planned = BigDecimal.ZERO;
			}
			if (actual == null) {
				actual = BigDecimal.ZERO;
			}
			resultMap.put(month, actual.subtract(planned));
		}
		categorySummary.put(SURPLUS_AMOUNT_KEY, resultMap);
	}
	
	/**
	 * TODO combine with other totals?
	 * 
	 * Adds the surplus balance map for the category to the category summary map.
	 * 
	 * NOTE #1: This method uses the surplus amount map, and assumes this map has already been added to the category summary.
	 * NOTE #2: This method assumes the list of months are in ascending chronological order.
	 * 
	 * @param category
	 * @param months
	 * @param categorySummary
	 */
	private void addSurplusBalanceForMonths(Category category, List<YearMonth> months, Map<String, Object> categorySummary) {
		Map<YearMonth, Object> resultMap = new TreeMap<>();
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal amount = null;
		for (YearMonth month : months) {
			amount = ((Map<YearMonth, BigDecimal>) categorySummary.get(SURPLUS_AMOUNT_KEY)).get(month);
			if (amount == null) {
				amount = BigDecimal.ZERO;
			}
			total = total.add(amount);
			resultMap.put(month, total);
		}
		categorySummary.put(SURPLUS_BALANCE_KEY, resultMap);
	}
	
	@Transactional(readOnly = true)
	public BigDecimal getActualAmount(Category category, List<Account> accounts, LocalDate startDate, LocalDate endDate) {
		
		logger.info("getActualAmount({}, {}, {}, {})", category, accounts, startDate, endDate);
		
		Query query = null;
		
		if (category == Category.NULL_CATEGORY) {
			query = em.createQuery("SELECT SUM(netAmount) FROM Transaction "
					+ "WHERE account IN :acc "
					+ "AND transactionDate BETWEEN :startDate AND :endDate "
					+ "AND category IS null ");
		} else {
			query = em.createQuery("SELECT SUM(netAmount) FROM Transaction "
					+ "WHERE account IN :acc "
					+ "AND transactionDate BETWEEN :startDate AND :endDate "
					+ "AND category IN :cat ");
			query.setParameter("cat", category);
		}
		
		query.setParameter("acc", accounts);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		
		Object result = query.getSingleResult();
		if (result == null) {
			logger.debug("Null actual amount");
			return null;
		} else {
			logger.debug("Actual amount = {} [{}]", result.toString(), result.getClass().getName());
			return (BigDecimal) result;
		}
	}
	
	/**
	 * TODO tidy method!
	 * 
	 * @param categorySummaries
	 * @param months
	 */
	private void addTotals(List<Map<String, Object>> categorySummaries, List<YearMonth> months) {
		
		// Faux category for totals
		Map<String, Object> categorySummary = new TreeMap<>();
		
		categorySummary.put("id", null);
		categorySummary.put("name", "Totals");
		categorySummary.put("fullName", "Totals");
		
		
		TreeMap<YearMonth, BigDecimal> totalPlannedAmount = new TreeMap<>();
		TreeMap<YearMonth, BigDecimal> totalActualAmount = new TreeMap<>();
		TreeMap<YearMonth, BigDecimal> totalPlannedBalance = new TreeMap<>();
		TreeMap<YearMonth, BigDecimal> totalActualBalance = new TreeMap<>();
		TreeMap<YearMonth, BigDecimal> totalSurplusAmount = new TreeMap<>();
		TreeMap<YearMonth, BigDecimal> totalSurplusBalance = new TreeMap<>();
		
		
		// calculate remaining values
		boolean firstMonth = true;
		for (YearMonth month : months) {
			
			// TODO factor out
			for (Map<String, Object> category : categorySummaries) {
				
				BigDecimal plannedAmount = (BigDecimal) ((Map<YearMonth, Object>) category.get("plannedAmount")).get(month); // TODO Ugh!
				if (plannedAmount != null) {
					if (totalPlannedAmount.get(month) == null) {
						totalPlannedAmount.put(month, plannedAmount);
					} else {
						BigDecimal newTotal = totalPlannedAmount.get(month).add(plannedAmount);
						totalPlannedAmount.put(month, newTotal);
					}
				}
				
				BigDecimal actualAmount = (BigDecimal) ((Map<YearMonth, Object>) category.get("actualAmount")).get(month); // TODO Ugh!
				if (actualAmount != null) {
					if (totalActualAmount.get(month) == null) {
						totalActualAmount.put(month, actualAmount);
					} else {
						BigDecimal newTotal = totalActualAmount.get(month).add(actualAmount);
						totalActualAmount.put(month, newTotal);
					}
				}
			}
			
			// TODO planned brought forward value?
			BigDecimal planned = totalPlannedAmount.get(month);
			if (planned == null) {
				planned = BigDecimal.ZERO;
			}
			// TODO actual brought forward value?
			BigDecimal actual = totalActualAmount.get(month);
			if (actual == null) {
				actual = BigDecimal.ZERO;
			}
			// surplus amount (actual - planned)
			BigDecimal surplus = actual.subtract(planned);
			totalSurplusAmount.put(month, surplus);
			
			if (firstMonth) {
				totalPlannedBalance.put(month, planned);
				totalActualBalance.put(month, actual);
				totalSurplusBalance.put(month, surplus);
				
				firstMonth = false;
			} else {
				totalPlannedBalance.put(month, totalPlannedBalance.get(month.minusMonths(1)).add(planned));
				totalActualBalance.put(month, totalActualBalance.get(month.minusMonths(1)).add(actual));
				totalSurplusBalance.put(month, totalSurplusBalance.get(month.minusMonths(1)).add(surplus));
			}
		}
		
		categorySummary.put(PLANNED_AMOUNT_KEY, totalPlannedAmount);
		categorySummary.put(PLANNED_BALANCE_KEY, totalPlannedBalance);
		categorySummary.put(ACTUAL_AMOUNT_KEY, totalActualAmount);
		categorySummary.put(ACTUAL_BALANCE_KEY, totalActualBalance);
		categorySummary.put(SURPLUS_AMOUNT_KEY, totalSurplusAmount);
		categorySummary.put(SURPLUS_BALANCE_KEY, totalSurplusBalance);
		
		categorySummaries.add(categorySummary);
	}
	
	/**
	 * Add category and its children to a list. This is called recursively.
	 * 
	 * @param results
	 * @param category
	 */
	private void getCategoryChildren(List<Category> results, Category category) {
		
		results.add(category);
		
		TypedQuery<Category> typedQuery = em.createQuery("from Category where parent = :cat ", Category.class);
		typedQuery.setParameter("cat", category);
		List<Category> children = typedQuery.getResultList();
		
		for (Category c : children) {
			getCategoryChildren(results, c);
		}
	}
}

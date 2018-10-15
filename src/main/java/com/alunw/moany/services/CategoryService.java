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
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alunw.moany.model.Account;
import com.alunw.moany.model.Category;
import com.alunw.moany.model.Transaction;
import com.alunw.moany.repository.CategoryRepository;

/**
 * Category service methods.
 */
@Component
public class CategoryService {
	
	private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	public List<Map<String, Object>> getCategorySummary(List<Account> accounts, YearMonth startMonth) {
		
		logger.info("getCategorySummary({}, {})", accounts, startMonth);
		
		List<Map<String, Object>> results = new ArrayList<>();
		
		if (startMonth == null || accounts == null || accounts.isEmpty()) {
			return results;
		}
		
		// Create list of months for specified year
		List<YearMonth> months = new ArrayList<>();
		YearMonth yearMonth = startMonth;
		for (int i = 0; i < 12; i++) {
			months.add(yearMonth);
			yearMonth = yearMonth.plusMonths(1);
		}
		
		// Get transactions for each month for each category 
		List<Category> categories = categoryRepo.findAll();
		for (Category category : categories) {
			results.add(processCategory(category, accounts, months));
		}
		
		// TODO Include uncategorized transactions
		results.add(processCategory(Category.NULL_CATEGORY, accounts, months));
		
		// TODO process totals
		results.add(processTotals(results, months));
		
		
		
		
		// Initialize total amounts
//		TreeMap<String, BigDecimal> totalPlannedAmount = new TreeMap<>();
//		TreeMap<String, BigDecimal> totalActualAmount = new TreeMap<>();
//		TreeMap<String, BigDecimal> totalPlannedBalance = new TreeMap<>();
//		TreeMap<String, BigDecimal> totalActualBalance = new TreeMap<>();
//		TreeMap<String, BigDecimal> totalSurplusAmount = new TreeMap<>();
//		TreeMap<String, BigDecimal> totalSurplusBalance = new TreeMap<>();
		
		
		
		
		return results;
	}
	
	private Map<String, Object> processCategory(Category category, List<Account> accounts, List<YearMonth> months) {
		
		Map<String, Object> categoryMap = new TreeMap<>();
		
		categoryMap.put("id", category.getId());
		categoryMap.put("name", category.getName());
		categoryMap.put("fullName", category.getFullName());
		
		TreeMap<String, Object> plannedAmount = new TreeMap<>();
		TreeMap<String, Object> plannedBalance = new TreeMap<>();
		TreeMap<String, Object> actualAmount = new TreeMap<>();
		TreeMap<String, Object> actualBalance = new TreeMap<>();
		TreeMap<String, Object> surplusAmount = new TreeMap<>();
		TreeMap<String, Object> surplusBalance = new TreeMap<>();
		
		// TODO initialize totals to zero - bought forward in future???
		BigDecimal plannedTotal = BigDecimal.ZERO; 
		BigDecimal actualTotal = BigDecimal.ZERO; 
		
		for (YearMonth month : months) {
			
			// planned amount + balance
			BigDecimal budgetAmount = null;
			if (category != Category.NULL_CATEGORY) {
				budgetAmount = category.getCategoryBudget(month);
				if (budgetAmount != null) {
					plannedAmount.put(month.toString(), budgetAmount);
					plannedTotal = plannedTotal.add(budgetAmount);
					
	//				if (totalPlannedAmount.get(month.toString()) == null) {
	//					totalPlannedAmount.put(month.toString(), budgetAmount);
	//				} else {
	//					BigDecimal newTotal = totalPlannedAmount.get(month.toString()).add(budgetAmount);
	//					totalPlannedAmount.put(month.toString(), newTotal);
	//				}
				}
				plannedBalance.put(month.toString(), plannedTotal);
			}
			
			// actual amount + balance
			BigDecimal transactionsTotal = BigDecimal.ZERO;
			List<Transaction> transactions = Collections.emptyList();
			transactions = findByCategory(category, accounts, month.atDay(1), month.atEndOfMonth(), false); // TODO exclude child categories for now
			if (!transactions.isEmpty()) {
				for (Transaction transaction : transactions) {
					if (transaction.getNetAmount() != null) {
						transactionsTotal = transactionsTotal.add(transaction.getNetAmount());
					}
				}
				actualAmount.put(month.toString(), transactionsTotal);
				actualTotal = actualTotal.add(transactionsTotal);
				
//				if (totalActualAmount.get(month.toString()) == null) {
//					totalActualAmount.put(month.toString(), transactionsTotal);
//				} else {
//					BigDecimal newTotal = totalActualAmount.get(month.toString()).add(transactionsTotal);
//					totalActualAmount.put(month.toString(), newTotal);
//				}
			}
			actualBalance.put(month.toString(), actualTotal);
			
			// surplus amount (actual - planned) 
			// TODO set null values as zero for surplus
			BigDecimal actual = BigDecimal.ZERO;
			BigDecimal planned = BigDecimal.ZERO;
			if (budgetAmount != null) {
				planned = budgetAmount;
			}
			if (transactionsTotal != null) {
				actual = transactionsTotal;
			}
			surplusAmount.put(month.toString(), actual.subtract(planned));
			
			// surplus balance (actual - planned)
			surplusBalance.put(month.toString(), actualTotal.subtract(plannedTotal));
		}
		
		categoryMap.put("plannedAmount", plannedAmount);
		categoryMap.put("plannedBalance", plannedBalance);
		categoryMap.put("actualAmount", actualAmount);
		categoryMap.put("actualBalance", actualBalance);
		categoryMap.put("surplusAmount", surplusAmount);
		categoryMap.put("surplusBalance", surplusBalance);
		
//		results.add(categoryMap);
		
		
		
		return categoryMap;
	}
	
	private Map<String, Object> processTotals(List<Map<String, Object>> categoryData, List<YearMonth> months) {
		
		// Faux category for totals
		Map<String, Object> categoryMap = new TreeMap<>();
		
		TreeMap<String, BigDecimal> totalPlannedAmount = new TreeMap<>();
		TreeMap<String, BigDecimal> totalActualAmount = new TreeMap<>();
		TreeMap<String, BigDecimal> totalPlannedBalance = new TreeMap<>();
		TreeMap<String, BigDecimal> totalActualBalance = new TreeMap<>();
		TreeMap<String, BigDecimal> totalSurplusAmount = new TreeMap<>();
		TreeMap<String, BigDecimal> totalSurplusBalance = new TreeMap<>();
		
//		categoryMap.put("id", category.getId());
		categoryMap.put("name", "Totals");
		categoryMap.put("fullName", "Totals");
		
		// calculate remaining values
		boolean firstMonth = true;
		for (YearMonth month : months) {
			
			// TODO factor out
			for (Map<String, Object> category : categoryData) {
				
				BigDecimal plannedAmount = (BigDecimal) ((Map<String, Object>) category.get("plannedAmount")).get(month.toString()); // TODO Ugh!
				if (plannedAmount != null) {
					if (totalPlannedAmount.get(month.toString()) == null) {
						totalPlannedAmount.put(month.toString(), plannedAmount);
					} else {
						BigDecimal newTotal = totalPlannedAmount.get(month.toString()).add(plannedAmount);
						totalPlannedAmount.put(month.toString(), newTotal);
					}
				}
				
				BigDecimal actualAmount = (BigDecimal) ((Map<String, Object>) category.get("actualAmount")).get(month.toString()); // TODO Ugh!
				if (actualAmount != null) {
					if (totalActualAmount.get(month.toString()) == null) {
						totalActualAmount.put(month.toString(), actualAmount);
					} else {
						BigDecimal newTotal = totalActualAmount.get(month.toString()).add(actualAmount);
						totalActualAmount.put(month.toString(), newTotal);
					}
				}
			}
			
			// TODO planned brought forward value?
			BigDecimal planned = totalPlannedAmount.get(month.toString());
			if (planned == null) {
				planned = BigDecimal.ZERO;
			}
			// TODO actual brought forward value?
			BigDecimal actual = totalActualAmount.get(month.toString());
			if (actual == null) {
				actual = BigDecimal.ZERO;
			}
			// surplus amount (actual - planned)
			BigDecimal surplus = actual.subtract(planned);
			totalSurplusAmount.put(month.toString(), surplus);
			
			if (firstMonth) {
				totalPlannedBalance.put(month.toString(), planned);
				totalActualBalance.put(month.toString(), actual);
				totalSurplusBalance.put(month.toString(), surplus);
				
				firstMonth = false;
			} else {
				totalPlannedBalance.put(month.toString(), totalPlannedBalance.get(month.minusMonths(1).toString()).add(planned));
				totalActualBalance.put(month.toString(), totalActualBalance.get(month.minusMonths(1).toString()).add(actual));
				totalSurplusBalance.put(month.toString(), totalSurplusBalance.get(month.minusMonths(1).toString()).add(surplus));
			}
		}
		
		categoryMap.put("plannedAmount", totalPlannedAmount);
		categoryMap.put("plannedBalance", totalPlannedBalance);
		categoryMap.put("actualAmount", totalActualAmount);
		categoryMap.put("actualBalance", totalActualBalance);
		categoryMap.put("surplusAmount", totalSurplusAmount);
		categoryMap.put("surplusBalance", totalSurplusBalance);
		
//		results.add(categoryMap);
		
		return categoryMap;
	}
	
	/**
	 * TODO re-factor method processing - to make clearer/simplify
	 * 
	 * @param accounts
	 * @param startMonth
	 * @return
	 */
	public List<Map<String, Object>> getCategorySummary_1(List<Account> accounts, YearMonth startMonth) {
		
		logger.info("getCategorySummary({}, {})", accounts, startMonth);
		
		List<Map<String, Object>> results = new ArrayList<>();
		
		if (startMonth == null || accounts == null || accounts.isEmpty()) {
			return results;
		}
		
		// Create list of months for specified year
		List<YearMonth> months = new ArrayList<>();
		YearMonth yearMonth = startMonth;
		for (int i = 0; i < 12; i++) {
			months.add(yearMonth);
			yearMonth = yearMonth.plusMonths(1);
		}
		
		// Initialize monthly totals
		TreeMap<String, BigDecimal> totalPlannedAmount = new TreeMap<>();
		TreeMap<String, BigDecimal> totalPlannedBalance = new TreeMap<>();
		TreeMap<String, BigDecimal> totalActualAmount = new TreeMap<>();
		TreeMap<String, BigDecimal> totalActualBalance = new TreeMap<>();
		TreeMap<String, BigDecimal> totalSurplusAmount = new TreeMap<>();
		TreeMap<String, BigDecimal> totalSurplusBalance = new TreeMap<>();
		
		// Get transactions for each month for each category 
		// TODO must get uncategorized transactions too!!
		List<Category> categories = categoryRepo.findAll();
		for (Category category : categories) {
			
			Map<String, Object> categoryMap = new TreeMap<>();
			
			categoryMap.put("id", category.getId());
			categoryMap.put("name", category.getName());
			categoryMap.put("fullName", category.getFullName());
			
			TreeMap<String, Object> plannedAmount = new TreeMap<>();
			TreeMap<String, Object> plannedBalance = new TreeMap<>();
			TreeMap<String, Object> actualAmount = new TreeMap<>();
			TreeMap<String, Object> actualBalance = new TreeMap<>();
			TreeMap<String, Object> surplusAmount = new TreeMap<>();
			TreeMap<String, Object> surplusBalance = new TreeMap<>();
			
			// TODO initialize totals to zero - bought forward in future???
			BigDecimal plannedTotal = BigDecimal.ZERO; 
			BigDecimal actualTotal = BigDecimal.ZERO; 
			
			for (YearMonth month : months) {
				
				// planned amount + balance
				BigDecimal budgetAmount = category.getCategoryBudget(month);
				if (budgetAmount != null) {
					plannedAmount.put(month.toString(), budgetAmount);
					plannedTotal = plannedTotal.add(budgetAmount);
					
					if (totalPlannedAmount.get(month.toString()) == null) {
						totalPlannedAmount.put(month.toString(), budgetAmount);
					} else {
						BigDecimal newTotal = totalPlannedAmount.get(month.toString()).add(budgetAmount);
						totalPlannedAmount.put(month.toString(), newTotal);
					}
				}
				plannedBalance.put(month.toString(), plannedTotal);
				
				// actual amount + balance
				BigDecimal transactionsTotal = BigDecimal.ZERO;
				List<Transaction> transactions = Collections.emptyList();
				transactions = findByCategory(category, accounts, month.atDay(1), month.atEndOfMonth(), false); // TODO exclude child categories for now
				if (!transactions.isEmpty()) {
					for (Transaction transaction : transactions) {
						if (transaction.getNetAmount() != null) {
							transactionsTotal = transactionsTotal.add(transaction.getNetAmount());
						}
					}
					actualAmount.put(month.toString(), transactionsTotal);
					actualTotal = actualTotal.add(transactionsTotal);
					
					if (totalActualAmount.get(month.toString()) == null) {
						totalActualAmount.put(month.toString(), transactionsTotal);
					} else {
						BigDecimal newTotal = totalActualAmount.get(month.toString()).add(transactionsTotal);
						totalActualAmount.put(month.toString(), newTotal);
					}
				}
				actualBalance.put(month.toString(), actualTotal);
				
				// surplus amount (actual - planned) 
				// TODO set null values as zero for surplus
				BigDecimal actual = BigDecimal.ZERO;
				BigDecimal planned = BigDecimal.ZERO;
				if (budgetAmount != null) {
					planned = budgetAmount;
				}
				if (transactionsTotal != null) {
					actual = transactionsTotal;
				}
				surplusAmount.put(month.toString(), actual.subtract(planned));
				
				// surplus balance (actual - planned)
				surplusBalance.put(month.toString(), actualTotal.subtract(plannedTotal));
			}
			
			categoryMap.put("plannedAmount", plannedAmount);
			categoryMap.put("plannedBalance", plannedBalance);
			categoryMap.put("actualAmount", actualAmount);
			categoryMap.put("actualBalance", actualBalance);
			categoryMap.put("surplusAmount", surplusAmount);
			categoryMap.put("surplusBalance", surplusBalance);
			
			results.add(categoryMap);
		}
		
		// Faux category for totals
		Map<String, Object> categoryMap = new TreeMap<>();
		
//		categoryMap.put("id", category.getId());
		categoryMap.put("name", "Totals");
		categoryMap.put("fullName", "Totals");
		
		// calculate remaining values
		boolean firstMonth = true;
		for (YearMonth month : months) {
			
			// TODO planned brought forward value?
			BigDecimal planned = totalPlannedAmount.get(month.toString());
			if (planned == null) {
				planned = BigDecimal.ZERO;
			}
			// TODO actual brought forward value?
			BigDecimal actual = totalActualAmount.get(month.toString());
			if (actual == null) {
				actual = BigDecimal.ZERO;
			}
			// surplus amount (actual - planned)
			BigDecimal surplus = actual.subtract(planned);
			totalSurplusAmount.put(month.toString(), surplus);
			
			if (firstMonth) {
				totalPlannedBalance.put(month.toString(), planned);
				totalActualBalance.put(month.toString(), actual);
				totalSurplusBalance.put(month.toString(), surplus);
				
				firstMonth = false;
			} else {
				totalPlannedBalance.put(month.toString(), totalPlannedBalance.get(month.minusMonths(1).toString()).add(planned));
				totalActualBalance.put(month.toString(), totalActualBalance.get(month.minusMonths(1).toString()).add(actual));
				totalSurplusBalance.put(month.toString(), totalSurplusBalance.get(month.minusMonths(1).toString()).add(surplus));
			}
		}
		
		categoryMap.put("plannedAmount", totalPlannedAmount);
		categoryMap.put("plannedBalance", totalPlannedBalance);
		categoryMap.put("actualAmount", totalActualAmount);
		categoryMap.put("actualBalance", totalActualBalance);
		categoryMap.put("surplusAmount", totalSurplusAmount);
		categoryMap.put("surplusBalance", totalSurplusBalance);
		
		results.add(categoryMap);
		
		return results;
	}
	
	/**
	 * Returns a list of transactions for the specified category, accounts, and start and end dates (inclusive).
	 * 
	 * Additionally, the results can include child (sub) categories.
	 * 
	 * @param category
	 * @param accounts
	 * @param startDate
	 * @param endDate
	 * @param includeChildCategories
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Transaction> findByCategory(Category category, List<Account> accounts, LocalDate startDate, LocalDate endDate, boolean includeChildCategories) {
		
		logger.info("findByCategory({}, {}, {}, {}, {})", category, accounts, startDate, endDate, includeChildCategories);
		
		TypedQuery<Transaction> typedQuery = null;
		
		if (category == Category.NULL_CATEGORY) {
			
			typedQuery = em.createQuery("from Transaction "
					+ "where account in :acc "
					+ "and transactionDate >= :startDate "
					+ "and transactionDate <= :endDate "
					+ "and category is null "
					+ "order by transactionDate, sourceRow asc", Transaction.class);
			
		} else {
			List<Category> categories = new ArrayList<>();
			if (includeChildCategories) {
				getCategoryChildren(categories, category);
			} else {
				categories.add(category);
			}
			logger.debug("categories = {}", categories);
			
			typedQuery = em.createQuery("from Transaction "
					+ "where account in :acc "
					+ "and transactionDate >= :startDate "
					+ "and transactionDate <= :endDate "
					+ "and category in :cat "
					+ "order by transactionDate, sourceRow asc", Transaction.class);
			
			typedQuery.setParameter("cat", categories);
		}
		
		typedQuery.setParameter("acc", accounts);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		
		List<Transaction> results = typedQuery.getResultList();
		
		logger.debug("Retrieved {} transaction(s)", results.size());
		
		return results;
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

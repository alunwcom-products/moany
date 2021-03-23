package com.alunw.moany;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alunw.moany.model.Category;
import com.alunw.moany.model.CategoryBudget;
import com.alunw.moany.repository.CategoryRepository;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//@TestPropertySource("classpath:application-test.yml")
public class CategoryBudgetTest {
	
	private static Logger logger = LoggerFactory.getLogger(CategoryBudgetTest.class);
	
//	@Autowired
//	private CategoryRepository categoryRepo;
	
	@Before
	public void before() {
	}
	
	@After
	public void after() {
	}

	@Test
	public void dummy() {
		assertTrue(true);
	}

	/**
	 * Basic Category POJO methods
	 */
//	@Test
//	public void test_CategoryBudget_1() {
//
//		Category cat = new Category("Test 1", null);
//		Category catStored = categoryRepo.save(cat);
//
//		assertEquals(cat.getName(), catStored.getName());
//
//		CategoryBudget budget = new CategoryBudget();
//		budget.setMonthlyBudget(new BigDecimal("1.00"));
//		budget.setStartDate(YearMonth.parse("2018-06"));
//		budget.setEndDate(YearMonth.parse("2018-08"));
//		catStored.addCategoryBudget(budget);
//
//		budget = new CategoryBudget();
//		budget.setMonthlyBudget(new BigDecimal("2.00"));
//		budget.setStartDate(YearMonth.parse("2018-05"));
//		catStored.addCategoryBudget(budget);
//
//		budget = new CategoryBudget();
//		budget.setMonthlyBudget(new BigDecimal("3.00"));
//		budget.setStartDate(YearMonth.parse("2018-09"));
//		budget.setEndDate(YearMonth.parse("2018-09"));
//		catStored.addCategoryBudget(budget);
//
//		budget = new CategoryBudget();
//		budget.setMonthlyBudget(new BigDecimal("4.00"));
//		budget.setStartDate(YearMonth.parse("2018-07"));
//		budget.setEndDate(YearMonth.parse("2018-07"));
//		catStored.addCategoryBudget(budget);
//
//		budget = new CategoryBudget();
//		budget.setMonthlyBudget(new BigDecimal("4.00"));
//		budget.setStartDate(YearMonth.parse("2018-07"));
//		budget.setEndDate(YearMonth.parse("2018-07"));
//		catStored.addCategoryBudget(budget);
//
//		catStored = categoryRepo.save(catStored);
//
//		Set<CategoryBudget> budgets = catStored.getCategoryBudgets();
//		for (CategoryBudget cb : budgets) {
//			logger.debug("retrieved budget: {}", cb);
//		}
//
//		assertEquals(5, catStored.getCategoryBudgets().size());
//
//		assertEquals(null, catStored.getCategoryBudget(YearMonth.parse("2018-04")));
//		assertEquals(new BigDecimal("2.00"), catStored.getCategoryBudget(YearMonth.parse("2018-05")));
//		assertEquals(new BigDecimal("1.00"), catStored.getCategoryBudget(YearMonth.parse("2018-06")));
//		assertEquals(new BigDecimal("4.00"), catStored.getCategoryBudget(YearMonth.parse("2018-07")));
//		assertEquals(new BigDecimal("1.00"), catStored.getCategoryBudget(YearMonth.parse("2018-08")));
//		assertEquals(new BigDecimal("3.00"), catStored.getCategoryBudget(YearMonth.parse("2018-09")));
//		assertEquals(new BigDecimal("2.00"), catStored.getCategoryBudget(YearMonth.parse("2018-10")));
//
//	}
	
}

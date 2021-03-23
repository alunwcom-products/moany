package com.alunw.moany;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@RunWith(SpringRunner.class)
@SpringBootTest
//@AutoConfigureMockMvc
//@TestPropertySource("classpath:application-test.yml")
public class CategoryBudgetTest {
	
//	private static Logger logger = LoggerFactory.getLogger(CategoryBudgetTest.class);
	
//	@Autowired
//	private CategoryRepository categoryRepo;
	
//	@Before
//	public void before() {
//	}
	
//	@After
//	public void after() {
//	}

	@Test
	public void dummy() {
		Assertions.assertTrue(true);
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

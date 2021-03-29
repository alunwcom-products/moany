package com.alunw.moany;

import com.alunw.moany.model.Category;
import com.alunw.moany.model.CategoryBudget;
import com.alunw.moany.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
public class CategoryBudgetTest {

    private static Logger logger = LoggerFactory.getLogger(CategoryBudgetTest.class);

    @Autowired
    private CategoryRepository categoryRepo;

    @BeforeEach
    public void before() {
    }

    @AfterEach
    public void after() {
    }

    /**
     * Basic Category POJO methods
     */
    @Test
    public void test_CategoryBudget_1() {

        Category cat = new Category("Test 1", null);
        Category catStored = categoryRepo.save(cat);

        Assertions.assertEquals(cat.getName(), catStored.getName());

        CategoryBudget budget = new CategoryBudget();
        budget.setMonthlyBudget(new BigDecimal("1.00"));
        budget.setStartDate(YearMonth.parse("2018-06"));
        budget.setEndDate(YearMonth.parse("2018-08"));
        catStored.addCategoryBudget(budget);

        budget = new CategoryBudget();
        budget.setMonthlyBudget(new BigDecimal("2.00"));
        budget.setStartDate(YearMonth.parse("2018-05"));
        catStored.addCategoryBudget(budget);

        budget = new CategoryBudget();
        budget.setMonthlyBudget(new BigDecimal("3.00"));
        budget.setStartDate(YearMonth.parse("2018-09"));
        budget.setEndDate(YearMonth.parse("2018-09"));
        catStored.addCategoryBudget(budget);

        budget = new CategoryBudget();
        budget.setMonthlyBudget(new BigDecimal("4.00"));
        budget.setStartDate(YearMonth.parse("2018-07"));
        budget.setEndDate(YearMonth.parse("2018-07"));
        catStored.addCategoryBudget(budget);

        budget = new CategoryBudget();
        budget.setMonthlyBudget(new BigDecimal("4.00"));
        budget.setStartDate(YearMonth.parse("2018-07"));
        budget.setEndDate(YearMonth.parse("2018-07"));
        catStored.addCategoryBudget(budget);

        catStored = categoryRepo.save(catStored);

        Set<CategoryBudget> budgets = catStored.getCategoryBudgets();
        for (CategoryBudget cb : budgets) {
            logger.debug("retrieved budget: {}", cb);
        }

        Assertions.assertEquals(4, catStored.getCategoryBudgets().size());

        Assertions.assertEquals(null, catStored.getCategoryBudget(YearMonth.parse("2018-04")));
        Assertions.assertEquals(new BigDecimal("2.00"), catStored.getCategoryBudget(YearMonth.parse("2018-05")));
        Assertions.assertEquals(new BigDecimal("1.00"), catStored.getCategoryBudget(YearMonth.parse("2018-06")));
        Assertions.assertEquals(new BigDecimal("4.00"), catStored.getCategoryBudget(YearMonth.parse("2018-07")));
        Assertions.assertEquals(new BigDecimal("1.00"), catStored.getCategoryBudget(YearMonth.parse("2018-08")));
        Assertions.assertEquals(new BigDecimal("3.00"), catStored.getCategoryBudget(YearMonth.parse("2018-09")));
        Assertions.assertEquals(new BigDecimal("2.00"), catStored.getCategoryBudget(YearMonth.parse("2018-10")));

    }
}

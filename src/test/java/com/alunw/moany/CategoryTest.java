package com.alunw.moany;

import com.alunw.moany.model.Account;
import com.alunw.moany.model.AccountType;
import com.alunw.moany.model.Category;
import com.alunw.moany.model.CategoryBudget;
import com.alunw.moany.model.Transaction;
import com.alunw.moany.model.TransactionType;
import com.alunw.moany.repository.AccountRepository;
import com.alunw.moany.repository.BudgetItemRepository;
import com.alunw.moany.repository.CategoryRepository;
import com.alunw.moany.repository.TransactionRepository;
import com.alunw.moany.services.BudgetItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
public class CategoryTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private BudgetItemRepository budgetRepo;

    @Autowired
    private BudgetItemService budgetService;

    private static Account account;

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
    public void test_Category_1() {

        String cat1Name = "Level 1 Category";
        String cat2Name = "Level 2 Category";
        CategoryBudget budget = new CategoryBudget(YearMonth.parse("2000-01"), null, new BigDecimal(100));

        Category cat1 = new Category();
        cat1.setName(cat1Name);

        Category cat2 = new Category(cat2Name, cat1);
        cat2.addCategoryBudget(budget);

        Assertions.assertEquals(null, cat1.getId());
        Assertions.assertEquals(null, cat2.getId());

        Assertions.assertEquals(cat1Name, cat1.getName());
        Assertions.assertEquals(cat2Name, cat2.getName());

        Assertions.assertEquals(cat1Name, cat1.getFullName());
        Assertions.assertEquals(cat1Name + Category.FULL_NAME_SEPARATOR + cat2Name, cat2.getFullName());

        Assertions.assertEquals(0, cat1.getCategoryBudgets().size());
        Assertions.assertEquals(1, cat2.getCategoryBudgets().size());
        Assertions.assertEquals(budget.getMonthlyBudget(), cat2.getCategoryBudget(YearMonth.parse("2018-01")));

        Assertions.assertEquals(null, cat1.getParent());
        Assertions.assertEquals(cat1, cat2.getParent());
    }

    /**
     * Basic repository save and find
     */
    @Test
    public void test_CategoryRepository_1() {

        String cat1Name = "Level 1 Category";
        String cat2Name = "Level 2 Category";
        CategoryBudget budget = new CategoryBudget(YearMonth.parse("2000-01"), null, new BigDecimal(100));

        Category cat1 = new Category();
        cat1.setName(cat1Name);
        cat1 = categoryRepo.save(cat1);

        Category cat2 = new Category(cat2Name, cat1);
        cat2.addCategoryBudget(budget);
        cat2 = categoryRepo.save(cat2);

        Optional<Category> opt1 = categoryRepo.findById(cat1.getId());

        Assertions.assertTrue(opt1.isPresent());
        Assertions.assertEquals(cat1.getId(), opt1.get().getId());
        Assertions.assertEquals(cat1Name, opt1.get().getName());
        Assertions.assertEquals(cat1Name, opt1.get().getFullName());
        Assertions.assertEquals(0, opt1.get().getCategoryBudgets().size());
        Assertions.assertEquals(null, opt1.get().getParent());

        Optional<Category> opt2 = categoryRepo.findById(cat2.getId());

        Assertions.assertTrue(opt2.isPresent());
        Assertions.assertEquals(cat2.getId(), opt2.get().getId());
        Assertions.assertEquals(cat2Name, opt2.get().getName());
        Assertions.assertEquals(cat1Name + Category.FULL_NAME_SEPARATOR + cat2Name, opt2.get().getFullName());
        Assertions.assertEquals(0, budget.getMonthlyBudget().compareTo(opt2.get().getCategoryBudget(YearMonth.parse("2020-01"))));
        Assertions.assertEquals(cat1.getId(), opt2.get().getParent().getId()); // compare category id
    }

    /**
     * Repository test for bug where category with children couldn't be updated (foreign key error).
     */
    @Test
    public void test_CategoryRepository_2() {

        String cat1Name = "Level 1 Category";
        String cat2Name1 = "Level 2a Category";
        String cat2Name2 = "Level 2b Category";
        String cat3Name = "Level 3 Category";
        CategoryBudget budget1 = new CategoryBudget(YearMonth.parse("2000-01"), null, new BigDecimal(200));
        CategoryBudget budget2 = new CategoryBudget(YearMonth.parse("2010-01"), null, new BigDecimal(201));
        CategoryBudget budget3 = new CategoryBudget(YearMonth.parse("2000-01"), null, new BigDecimal(100));

        Category cat1 = new Category();
        cat1.setName(cat1Name);
        cat1 = categoryRepo.save(cat1);

        Category cat2 = new Category(cat2Name1, cat1);
        cat2.addCategoryBudget(budget1);
        cat2 = categoryRepo.save(cat2);

        Category cat3 = new Category(cat3Name, cat2);
        cat3.addCategoryBudget(budget3);
        cat3 = categoryRepo.save(cat3);

        Account acc = new Account();
        acc.setAccNum("12345");
        acc.setName("name");
        acc.setType(AccountType.DEBIT);
        acc = accountRepo.save(acc);

        Transaction tr1 = new Transaction();
        tr1.setDescription("test #1");
        tr1.setAccount(acc);
        tr1.setCategory(cat2);
        tr1.setSourceType(TransactionType.MANUAL);
        tr1 = transactionRepo.save(tr1);

        Transaction tr2 = new Transaction();
        tr2.setDescription("test #2");
        tr2.setAccount(acc);
        tr2.setCategory(cat3);
        tr2.setSourceType(TransactionType.MANUAL);
        tr2 = transactionRepo.save(tr2);

        Optional<Category> opt1 = categoryRepo.findById(cat1.getId());
        Assertions.assertTrue(opt1.isPresent());

        Optional<Category> opt2 = categoryRepo.findById(cat2.getId());
        Assertions.assertTrue(opt2.isPresent());

        Optional<Category> opt3 = categoryRepo.findById(cat3.getId());
        Assertions.assertTrue(opt3.isPresent());

        opt2.get().setName(cat2Name2);
        opt2.get().addCategoryBudget(budget2);
        categoryRepo.save(opt2.get());

        opt2 = categoryRepo.findById(cat2.getId());
        Assertions.assertTrue(opt2.isPresent());
        Assertions.assertEquals(cat2.getId(), opt2.get().getId());
        Assertions.assertEquals(cat2Name2, opt2.get().getName());
        Assertions.assertEquals(cat1Name + Category.FULL_NAME_SEPARATOR + cat2Name2, opt2.get().getFullName());
        Assertions.assertEquals(0, budget2.getMonthlyBudget().compareTo(opt2.get().getCategoryBudget(YearMonth.parse("2018-06"))));
        Assertions.assertEquals(cat1.getId(), opt2.get().getParent().getId()); // compare category id

    }

    /**
     * Integration with CategoryBudget
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

        catStored = categoryRepo.save(catStored);

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

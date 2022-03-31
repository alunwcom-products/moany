package com.alunw.moany;

import com.alunw.moany.model.Account;
import com.alunw.moany.model.BudgetItem;
import com.alunw.moany.model.Transaction;
import com.alunw.moany.repository.AccountRepository;
import com.alunw.moany.repository.BudgetItemRepository;
import com.alunw.moany.services.BudgetItemService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
public class BudgetItemTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private BudgetItemRepository budgetRepo;

    @Autowired
    private BudgetItemService budgetService;

    private static Account account;

    @BeforeEach
    public void before() {
        if (account == null) {
            account = new Account();
            account.setAccNum("12345678");
            account.setName("test account");
            account = accRepo.save(account);
        }
    }

    @AfterEach
    public void after() {
        budgetRepo.deleteAll();
    }

    @Test
    public void testBudgetItem_1() throws Exception {

        BudgetItem item = new BudgetItem();
        item.setAccount(account);
        item.setAmount(new BigDecimal("-50.00"));
        item.setDescription("test item");
        item.setStartDate(LocalDate.parse("2018-04-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        item = budgetRepo.save(item);

        List<BudgetItem> items = budgetRepo.findAll();
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item.getId(), items.get(0).getId());

    }

    @Test
    public void testBudgetItemService_1() throws Exception {

        BudgetItem item = new BudgetItem();
        item.setAccount(account);
        item.setAmount(new BigDecimal("-50.00"));
        item.setDescription("test item");
        item.setStartDate(LocalDate.parse("2018-04-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        item = budgetRepo.save(item);

        List<Account> accounts = new ArrayList<>();
        accounts.add(account);

        List<Transaction> results = budgetService.generateBudgetingTransactionsByAccount(accounts, LocalDate.parse("2018-05-01"), LocalDate.parse("2018-07-31"));

        System.out.println(results);

        Assertions.assertEquals(3, results.size());

    }

    @Test
    public void testBudgetItemService_2() throws Exception {

        BudgetItem item = new BudgetItem();
        item.setAccount(account);
        item.setAmount(new BigDecimal("-50.00"));
        item.setDescription("test item");
        item.setStartDate(LocalDate.parse("2018-04-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        item.setDayOfPeriod(99); // end of month
        item = budgetRepo.save(item);

        List<Account> accounts = new ArrayList<>();
        accounts.add(account);

        List<Transaction> results = budgetService.generateBudgetingTransactionsByAccount(accounts, LocalDate.parse("2018-01-01"), LocalDate.parse("2018-07-31"));

        System.out.println(results);

        Assertions.assertEquals(4, results.size());

    }

    @Test
    public void getTransactions_OK() throws Exception {
        mvc.perform(get("/rest/transactions/v2/")
                .with(httpBasic("test", "password"))
                .accept(MediaType.APPLICATION_JSON))
//              .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("[]")));
    }

    @Test
    public void getTransactions_401() throws Exception {
        mvc.perform(get("/rest/transactions/v2/")
                .accept(MediaType.APPLICATION_JSON))
//              .andExpect(unauthenticated())
                .andExpect(status().is4xxClientError());
    }
}

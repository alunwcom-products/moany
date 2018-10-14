package com.alunw.moany;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alunw.moany.model.Account;
import com.alunw.moany.model.BudgetItem;
import com.alunw.moany.model.Transaction;
import com.alunw.moany.repository.AccountRepository;
import com.alunw.moany.repository.BudgetItemRepository;
import com.alunw.moany.services.BudgetItemService;

@RunWith(SpringRunner.class)
@SpringBootTest
//@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
public class BudgetItemTest {
	
//	@Autowired
//	private MockMvc mvc;
	
	@Autowired
	private AccountRepository accRepo;
	
	@Autowired
	private BudgetItemRepository budgetRepo;
	
	@Autowired
	private BudgetItemService budgetService;
	
	private static Account account;
	
	@Before
	public void before() {
		if (account == null) {
			account = new Account();
			account.setAccNum("12345678");
			account.setName("test account");
			account = accRepo.save(account);
		}
	}
	
	@After
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
		assertEquals(1, items.size());
		assertEquals(item.getId(), items.get(0).getId());
		
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
		
		assertEquals(3, results.size());
		
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
		
		assertEquals(4, results.size());
		
	}

//	@Test
//	public void getTransactions_OK() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.get("/rest/transactions/v2/")
//			.with(httpBasic("test", "password"))
//			.accept(MediaType.APPLICATION_JSON))
//			.andExpect(authenticated())
//			.andExpect(status().isOk())
//			.andExpect(content().string(Matchers.containsString("[]")));
//	}
	
//	@Test
//	public void getTransactions_401() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.get("/rest/transactions/v2/")
//			.accept(MediaType.APPLICATION_JSON))
//			.andExpect(unauthenticated())
//			.andExpect(status().is4xxClientError());
//	}
	
//	@Test
//	public void getEmail() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.get("/subscription/?requesterName=Springboard&societyCode=RECS&eMailAddr=joe3@example.com").accept(MediaType.APPLICATION_JSON))
//			.andExpect(status().isOk())
//			.andExpect(content().string(Matchers.containsString("\"code\":\"EIS_0000\",\"message\":\"Success\"")))
//			.andExpect(content().string(Matchers.containsString("\"subscriptionID\":\"3333333\"")));
//	}
	
//	@Test
//	public void getReporting() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.get("/subscription/?requesterName=Springboard&societyCode=RECS&contractStart=20181231&cancellationCode=N&mediaType=C&paymentRate=M").accept(MediaType.APPLICATION_JSON))
//			.andExpect(status().isOk())
//			.andExpect(content().string(Matchers.containsString("\"code\":\"EIS_0000\",\"message\":\"Success\"")))
//			.andExpect(MockMvcResultMatchers.jsonPath("$.subscriptionResponse.subscriptionDetails", Matchers.hasSize(1)));
//	}
	
}

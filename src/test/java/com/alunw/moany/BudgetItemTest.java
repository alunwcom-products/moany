package com.alunw.moany;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@RunWith(SpringRunner.class)
@SpringBootTest
//@AutoConfigureMockMvc
//@TestPropertySource("classpath:application-test.yml")
public class BudgetItemTest {
	
//	@Autowired
//	private MockMvc mvc;
	
//	@Autowired
//	private AccountRepository accRepo;
	
//	@Autowired
//	private BudgetItemRepository budgetRepo;
	
//	@Autowired
//	private BudgetItemService budgetService;
	
//	private static Account account;
	
//	@Before
//	public void before() {
//		if (account == null) {
//			account = new Account();
//			account.setAccNum("12345678");
//			account.setName("test account");
//			account = accRepo.save(account);
//		}
//	}
	
//	@After
//	public void after() {
//		budgetRepo.deleteAll();
//	}

	@Test
	public void dummy() {
		Assertions.assertTrue(true);
	}
	
//	@Test
//	public void testBudgetItem_1() throws Exception {
//
//		BudgetItem item = new BudgetItem();
//		item.setAccount(account);
//		item.setAmount(new BigDecimal("-50.00"));
//		item.setDescription("test item");
//		item.setStartDate(LocalDate.parse("2018-04-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//		item = budgetRepo.save(item);
//
//		List<BudgetItem> items = budgetRepo.findAll();
//		assertEquals(1, items.size());
//		assertEquals(item.getId(), items.get(0).getId());
//
//	}

//	@Test
//	public void testBudgetItemService_1() throws Exception {
//
//		BudgetItem item = new BudgetItem();
//		item.setAccount(account);
//		item.setAmount(new BigDecimal("-50.00"));
//		item.setDescription("test item");
//		item.setStartDate(LocalDate.parse("2018-04-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//		item = budgetRepo.save(item);
//
//		List<Account> accounts = new ArrayList<>();
//		accounts.add(account);
//
//		List<Transaction> results = budgetService.generateBudgetingTransactionsByAccount(accounts, LocalDate.parse("2018-05-01"), LocalDate.parse("2018-07-31"));
//
//		System.out.println(results);
//
//		assertEquals(3, results.size());
//
//	}

//	@Test
//	public void testBudgetItemService_2() throws Exception {
//
//		BudgetItem item = new BudgetItem();
//		item.setAccount(account);
//		item.setAmount(new BigDecimal("-50.00"));
//		item.setDescription("test item");
//		item.setStartDate(LocalDate.parse("2018-04-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//		item.setDayOfPeriod(99); // end of month
//		item = budgetRepo.save(item);
//
//		List<Account> accounts = new ArrayList<>();
//		accounts.add(account);
//
//		List<Transaction> results = budgetService.generateBudgetingTransactionsByAccount(accounts, LocalDate.parse("2018-01-01"), LocalDate.parse("2018-07-31"));
//
//		System.out.println(results);
//
//		assertEquals(4, results.size());
//
//	}

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

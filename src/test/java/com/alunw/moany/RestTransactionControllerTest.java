package com.alunw.moany;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@RunWith(SpringRunner.class)
@SpringBootTest
//@AutoConfigureMockMvc
//@TestPropertySource("classpath:application-test.yml")
public class RestTransactionControllerTest {
	
//	@Autowired
//	private MockMvc mvc;

	@Test
	public void dummy() {
		Assertions.assertTrue(true);
	}

//	@Test
//	public void getTransactions_OK() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.get("/rest/transactions/v2/")
//			.with(httpBasic("test", "password"))
//			.accept(MediaType.APPLICATION_JSON))
//			//.andExpect(authenticated())
//			.andExpect(status().isOk())
//			.andExpect(content().string(Matchers.containsString("[]")));
//	}
	
//	@Test
//	public void getTransactions_401() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.get("/rest/transactions/v2/")
//			.accept(MediaType.APPLICATION_JSON))
//			//.andExpect(unauthenticated())
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

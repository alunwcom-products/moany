package com.alunw.moany;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
public class MvcTest {

    @Autowired
    MockMvc mvc;

    @Test
    public void test1() throws Exception {
        Assertions.assertTrue(true);
    }

    @Test
	public void testStatusOk() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/status").accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("{\"status\":\"OK\"}")));
	}

	@Test
	public void getTransactions_OK() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/rest/transactions/v2/")
			.with(SecurityMockMvcRequestPostProcessors.httpBasic("test", "password"))
			.accept(MediaType.APPLICATION_JSON))
//			.andExpect(SecurityMockMvcResultMatchers.authenticated())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("[]")));
	}

}

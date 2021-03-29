package com.alunw.moany;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
public class RestTransactionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getTransactions_OK() throws Exception {
        mvc.perform(get("/rest/transactions/v2/")
                .with(httpBasic("test", "password"))
                .accept(MediaType.APPLICATION_JSON))
                //.andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("[]")));
    }

    @Test
    public void getTransactions_401() throws Exception {
        mvc.perform(get("/rest/transactions/v2/")
                .accept(MediaType.APPLICATION_JSON))
                //.andExpect(unauthenticated())
                .andExpect(status().is4xxClientError());
    }
}

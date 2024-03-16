package com.jar.jit.controller;

import com.jar.jit.IntegrationTest;
import com.jar.jit.entity.Account;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Currency;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerIT extends IntegrationTest {


    @Test
    @Tag("basic")
    void getAccounts_withSuccess() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(HttpMethod.GET, "/accounts")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    @Tag("basic")
    void getSingleAccount_withSuccess() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(HttpMethod.GET, "/accounts/PL-0001")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is("PL-0001")))
                .andExpect(jsonPath("$.currency", Matchers.is("PLN")))
                .andExpect(jsonPath("$.owner", Matchers.is("Homer Simpson")))
                .andExpect(jsonPath("$.amount", Matchers.is(2312.2)));
    }

    @Test
    @Tag("full")
    void getSingleAccount_withFailure() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(HttpMethod.GET, "/accounts/non-existent")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message", Matchers.is("Account does not exist")));
    }

    @Test
    @Tag("basic")
    void updateAccountCurrency_withSuccess() throws Exception {
        Account acc = new Account();
        acc.setCurrency(Currency.getInstance("USD"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(HttpMethod.PUT, "/accounts/PL-0001")
                .content(objectMapper.writeValueAsString(acc))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.currency", Matchers.is("USD")));
    }

    @Test
    @Tag("full")
    void updateAccountCurrency_withFailure() throws Exception {
        Account acc = new Account();
        acc.setCurrency(Currency.getInstance("GBP"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(HttpMethod.PUT, "/accounts/PL-0001")
                .content(objectMapper.writeValueAsString(acc))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", Matchers.is("Not accepted currency")));
    }
}

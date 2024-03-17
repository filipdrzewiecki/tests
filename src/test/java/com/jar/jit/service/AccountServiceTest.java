package com.jar.jit.service;

import com.jar.jit.UnitTest;
import com.jar.jit.client.NbpWebClient;
import com.jar.jit.client.RateResponse;
import com.jar.jit.entity.Account;
import com.jar.jit.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;


public class AccountServiceTest extends UnitTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NbpWebClient nbpWebClient;

    @Test
    void updateAccountCurrency_withFailure() throws Exception {
        //GIVEN
        Account acc = new Account();
        acc.setOwner("Blabla");
        acc.setAmount(BigDecimal.ONE);
        acc.setCurrency(Currency.getInstance("GBP"));

        Account update = new Account();
        update.setCurrency(Currency.getInstance("PLN"));

        Mockito.when(accountRepository.findById("PL-0001")).thenReturn(Optional.of(acc));
        Mockito.when(accountRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);
        Mockito.when(nbpWebClient.getRate(any())).thenReturn(testRateResponse());

        //WHEN
        Account result = accountService.updateAccount("PL-0001", update);

        //THEN
        assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("PLN");
    }

    private RateResponse testRateResponse() {
        var rate = new RateResponse.Rate();
        rate.setMid(BigDecimal.ONE);

        var res = new RateResponse();
        res.setRates(List.of(rate));

        return res;
    }
}

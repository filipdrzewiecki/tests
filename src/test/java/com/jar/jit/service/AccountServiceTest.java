package com.jar.jit.service;

import com.jar.jit.UnitTest;
import com.jar.jit.entity.Account;
import com.jar.jit.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class AccountServiceTest extends UnitTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    void updateAccountCurrency_withFailure() throws Exception {
        Account acc = new Account();
        acc.setOwner("Blabla");
        acc.setCurrency(Currency.getInstance("GBP"));

        Account update = new Account();
        update.setCurrency(Currency.getInstance("PLN"));

        Mockito.when(accountRepository.findById("PL-0001")).thenReturn(Optional.of(acc));
        Mockito.when(accountRepository.save(Mockito.any())).thenAnswer(a -> a.getArguments()[0]);



        Account result = accountService.updateAccount("PL-0001", update);


        assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("PLN");
    }
}

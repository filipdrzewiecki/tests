package com.jar.jit.service;

import com.jar.jit.UnitTest;
import com.jar.jit.client.NbpWebClient;
import com.jar.jit.client.RateResponse;
import com.jar.jit.entity.Account;
import com.jar.jit.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


public class AccountServiceTest extends UnitTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NbpWebClient nbpWebClient;

    private Account acc;

    @BeforeEach
    void setup() {
        acc = new Account();
        acc.setOwner("Owner");
        acc.setAmount(BigDecimal.ONE);
        acc.setCurrency(Currency.getInstance("PLN"));

        Mockito.lenient().when(accountRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);
    }

    @Test
    void updateAccount_withSuccess() throws Exception {
        //GIVEN
        Account update = new Account();
        update.setCurrency(Currency.getInstance("USD"));

        Mockito.when(accountRepository.findById("PL-0001")).thenReturn(Optional.of(acc));
        Mockito.when(nbpWebClient.getRate(any())).thenReturn(testRateResponse());

        //WHEN
        Account result = accountService.updateAccount("PL-0001", update);

        //THEN
        assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("USD");
    }

    @ParameterizedTest
    @CsvSource({"PLN,USD", "PLN,EUR", "USD,PLN", "EUR,PLN"})
    void updateAccountCurrency_withSuccess(String from, String to) throws Exception {
        //GIVEN
        Account update = new Account();
        update.setCurrency(Currency.getInstance(to));

        acc.setCurrency(Currency.getInstance(from));
        Mockito.when(accountRepository.findById("PL-0001")).thenReturn(Optional.of(acc));
        Mockito.when(nbpWebClient.getRate(any())).thenReturn(testRateResponse());

        //WHEN
        Account result = accountService.updateAccount("PL-0001", update);

        //THEN
        assertThat(result.getCurrency().getCurrencyCode()).isEqualTo(to);
    }

    @ParameterizedTest
    @CsvSource({"PLN,PLN", "USD,EUR", "USD,USD", "EUR,USD"})
    void updateAccountCurrency_withIllegalCurrencyCombination_withFailure(String from, String to) throws Exception {
        //GIVEN
        Account update = new Account();
        update.setCurrency(Currency.getInstance(to));

        acc.setCurrency(Currency.getInstance(from));
        Mockito.when(accountRepository.findById("PL-0001")).thenReturn(Optional.of(acc));

        //WHEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                accountService.updateAccount("PL-0001", update));

        //THEN
        assertThat(exception.getMessage()).isEqualTo("Can only update from PLN or to PLN");
    }

    @ParameterizedTest
    @MethodSource("provideCurrencies")
    void updateAccountCurrency_withNotAcceptedCurrency_withFailure(String to) throws Exception {
        //GIVEN
        Account update = new Account();
        update.setCurrency(Currency.getInstance(to));

        acc.setCurrency(Currency.getInstance("PLN"));
        Mockito.when(accountRepository.findById("PL-0001")).thenReturn(Optional.of(acc));
        //Mockito.when(nbpWebClient.getRate(any())).thenReturn(testRateResponse());

        //WHEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                accountService.updateAccount("PL-0001", update));

        //THEN
        assertThat(exception.getMessage()).isEqualTo("Not accepted currency");
    }

    private static Stream<Arguments> provideCurrencies() {
        Set<String> acceptedCurrencies = Set.of("PLN", "USD", "EUR");
        return Currency.getAvailableCurrencies().stream()
                .map(Currency::getCurrencyCode)
                .filter(currency -> !acceptedCurrencies.contains(currency))
                .map(Arguments::of);
    }

    private RateResponse testRateResponse() {
        var rate = new RateResponse.Rate();
        rate.setMid(BigDecimal.ONE);

        var res = new RateResponse();
        res.setRates(List.of(rate));

        return res;
    }
}

package com.jar.jit.service;

import com.jar.jit.client.NbpWebClient;
import com.jar.jit.client.RateResponse;
import com.jar.jit.entity.Account;
import com.jar.jit.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final NbpWebClient nbpWebClient;

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccount(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .orElseThrow(() ->  new EntityNotFoundException("Account does not exist"));
    }

    public Account updateAccount(String accountNumber, Account update) {
        Account acc = getAccount(accountNumber);
        updateAccountCurrency(acc, update);
        updateAccountOwner(acc, update);
        return accountRepository.save(acc);
    }

    private void updateAccountCurrency(Account acc, Account update) {
        Currency currency = update.getCurrency();
        if (currency == null) {
            throw new IllegalArgumentException("Currency is required");
        }
        if (!Set.of(acc.getCurrency().getCurrencyCode(), update.getCurrency().getCurrencyCode()).contains("PLN")) {
            throw new IllegalArgumentException("Only PLN account can switch or from PLN account");
        }
        if (!Set.of("PLN", "USD", "EUR").contains(currency.getCurrencyCode())) {
            throw new IllegalArgumentException("Not accepted currency");
        }
        RateResponse rateResponse = nbpWebClient.getRate(update.getCurrency());
        BigDecimal rate = rateResponse.getRates().get(0).getMid();

        acc.setCurrency(update.getCurrency());
        acc.setAmount(recalculateAmount(acc.getCurrency(), acc.getAmount(), rate));
    }

    private BigDecimal recalculateAmount(Currency from, BigDecimal amount, BigDecimal rate) {
        if ("PLN".equals(from.getCurrencyCode())) {
            return amount.multiply(rate);
        }
        return amount.divide(rate, RoundingMode.HALF_UP);
    }

    private void updateAccountOwner(Account acc, Account update) {
        if (update.getOwner() == null || acc.getOwner().equals(update.getOwner())) {
            return;
        }
        if (update.getOwner().isEmpty()) {
            throw new IllegalArgumentException("Owner of the account is required");
        }
        acc.setOwner(update.getOwner());
    }
}

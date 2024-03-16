package com.jar.jit.service;

import com.jar.jit.entity.Account;
import com.jar.jit.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

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
        if (!Set.of("PLN", "USD", "EUR").contains(currency.getCurrencyCode())) {
            throw new IllegalArgumentException("Not accepted currency");
        }
        acc.setCurrency(update.getCurrency());
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

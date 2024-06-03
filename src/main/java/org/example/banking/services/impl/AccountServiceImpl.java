package org.example.banking.services.impl;

import org.example.banking.domain.dto.AccountDto;
import org.example.banking.domain.entities.AccountEntity;
import org.example.banking.repositories.AccountRepository;
import org.example.banking.services.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountEntity createAccount(AccountEntity accountEntity) {
        return accountRepository.save(accountEntity);
    }

    @Override
    public List<AccountEntity> findAll() {
        return StreamSupport.stream(accountRepository
                                .findAll()
                                .spliterator(),
                        false)
                .toList();
    }

    @Override
    public Page<AccountEntity> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public Optional<AccountEntity> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public boolean isExist(Long id) {
        return accountRepository.existsById(id);
    }

    @Override
    public AccountEntity partialUpdate(Long id, AccountEntity accountEntity) {
        accountEntity.setId(id);

        return accountRepository.findById(id).map(existingAccount -> {
            Optional.ofNullable(accountEntity.getAccountNumber())
                    .ifPresent(existingAccount::setAccountNumber);
            Optional.ofNullable(accountEntity.getBalance())
                    .ifPresent(existingAccount::setBalance);

            return accountRepository.save(existingAccount);
        }).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public AccountDto deposit(Long id, BigDecimal amount) {
        return accountRepository.findById(id).map(accountEntity -> {
            accountEntity.setBalance(accountEntity.getBalance().add(amount));
            return accountRepository.save(accountEntity);
        }).map(accountEntity -> new AccountDto(accountEntity.getId(), accountEntity.getAccountNumber(), accountEntity.getBalance()))
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public AccountDto withdraw(Long id, BigDecimal amount) {
        return accountRepository.findById(id).map(accountEntity -> {
            accountEntity.setBalance(accountEntity.getBalance().subtract(amount));
            return accountRepository.save(accountEntity);
        }).map(accountEntity -> new AccountDto(accountEntity.getId(), accountEntity.getAccountNumber(), accountEntity.getBalance()))
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public AccountDto transfer(Long fromId, Long toId, BigDecimal amount) {
        AccountEntity fromAccount = accountRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        AccountEntity toAccount = accountRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return accountRepository.findById(fromId).map(accountEntity -> new AccountDto(accountEntity.getId(), accountEntity.getAccountNumber(), accountEntity.getBalance()))
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}

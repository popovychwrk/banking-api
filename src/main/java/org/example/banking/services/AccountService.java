package org.example.banking.services;

import org.example.banking.domain.dto.AccountDto;
import org.example.banking.domain.entities.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    AccountEntity createAccount(AccountEntity accountEntity);

    List<AccountEntity> findAll();

    Page<AccountEntity> findAll(Pageable pageable);

    Optional<AccountEntity> findById(Long id);

    boolean isExist(Long id);

    AccountEntity partialUpdate(Long id, AccountEntity accountEntity);

    void deleteAccount(Long id);

    AccountDto deposit(Long id, BigDecimal amount);

    AccountDto withdraw(Long id, BigDecimal amount);

    AccountDto transfer(Long fromId, Long toId, BigDecimal amount);
}

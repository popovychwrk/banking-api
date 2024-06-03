package org.example.banking.controllers;

import org.example.banking.domain.dto.AccountDto;
import org.example.banking.domain.entities.AccountEntity;
import org.example.banking.mappers.Mapper;
import org.example.banking.services.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AccountController {

    private final AccountService accountService;
    private final Mapper<AccountEntity, AccountDto> accountMapper;

    public AccountController(AccountService accountService,
                             Mapper<AccountEntity, AccountDto> accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @PostMapping(path = "/accounts")
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        AccountEntity accountEntity = accountMapper.mapFrom(accountDto);
        AccountEntity savedAccountEntity = accountService.createAccount(accountEntity);
        return new ResponseEntity<>(accountMapper.mapTo(savedAccountEntity), HttpStatus.CREATED);
    }

//    @GetMapping(path = "/accounts")
//    public List<AccountDto> getAllAccounts() {
//        List<AccountEntity> accounts = accountService.findAll();
//        return accounts.stream().map(accountMapper::mapTo).toList();
//    }

    @GetMapping(path = "/accounts")
    public Page<AccountDto> getAllAccounts(Pageable pageable) {
        Page<AccountEntity> accounts = accountService.findAll(pageable);
        return accounts.map(accountMapper::mapTo);
    }

    @GetMapping(path = "/accounts/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        Optional<AccountEntity> accountEntity = accountService.findById(id);
        return accountEntity.map(account -> new ResponseEntity<>(accountMapper.mapTo(account), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(path = "/accounts/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id,
                                                    @RequestBody AccountDto accountDto) {
        if (!accountService.isExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        accountDto.setId(id);
        AccountEntity accountEntity = accountMapper.mapFrom(accountDto);
        AccountEntity savedAccountEntity = accountService.createAccount(accountEntity);
        return new ResponseEntity<>(accountMapper.mapTo(savedAccountEntity), HttpStatus.OK);
    }

    @PatchMapping(path = "/accounts/{id}")
    public ResponseEntity<AccountDto> partialAccount(@PathVariable Long id,
                                                     @RequestBody AccountDto accountDto) {
        if (!accountService.isExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        AccountEntity accountEntity = accountMapper.mapFrom(accountDto);
        AccountEntity updatedEntity = accountService.partialUpdate(id, accountEntity);
        return new ResponseEntity<>(accountMapper.mapTo(updatedEntity), HttpStatus.OK);
    }

    @DeleteMapping(path = "/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        if (!accountService.isExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        accountService.deleteAccount(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

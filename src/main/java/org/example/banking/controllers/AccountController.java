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

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final Mapper<AccountEntity, AccountDto> accountMapper;

    public AccountController(AccountService accountService,
                             Mapper<AccountEntity, AccountDto> accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @PostMapping()
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        AccountEntity accountEntity = accountMapper.mapFrom(accountDto);
        AccountEntity savedAccountEntity = accountService.createAccount(accountEntity);
        return new ResponseEntity<>(accountMapper.mapTo(savedAccountEntity), HttpStatus.CREATED);
    }

    @GetMapping()
    public Page<AccountDto> getAllAccounts(Pageable pageable) {
        Page<AccountEntity> accounts = accountService.findAll(pageable);
        return accounts.map(accountMapper::mapTo);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        Optional<AccountEntity> accountEntity = accountService.findById(id);
        return accountEntity.map(account -> new ResponseEntity<>(accountMapper.mapTo(account), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(path = "/{id}")
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

    @PatchMapping(path = "/{id}")
    public ResponseEntity<AccountDto> partialAccount(@PathVariable Long id,
                                                     @RequestBody AccountDto accountDto) {
        if (!accountService.isExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        AccountEntity accountEntity = accountMapper.mapFrom(accountDto);
        AccountEntity updatedEntity = accountService.partialUpdate(id, accountEntity);
        return new ResponseEntity<>(accountMapper.mapTo(updatedEntity), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        if (!accountService.isExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        accountService.deleteAccount(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "/deposit/{id}")
    public ResponseEntity<AccountDto> deposit(@PathVariable Long id,
                                              @RequestParam BigDecimal amount) {
        AccountDto accountDto = accountService.deposit(id, amount);
        return new ResponseEntity<>(accountDto, HttpStatus.OK);
    }

    @PostMapping(path = "/withdraw/{id}")
    public ResponseEntity<AccountDto> withdraw(@PathVariable Long id,
                                               @RequestParam BigDecimal amount) {
        AccountDto accountDto = accountService.withdraw(id, amount);
        return new ResponseEntity<>(accountDto, HttpStatus.OK);
    }

    @PostMapping(path = "/transfer/")
    public ResponseEntity<AccountDto> transfer(@RequestParam Long fromAccountId,
                                               @RequestParam Long toAccountId,
                                               @RequestParam BigDecimal amount) {
        AccountDto accountDto = accountService.transfer(fromAccountId, toAccountId, amount);
        return new ResponseEntity<>(accountDto, HttpStatus.OK);
    }
}

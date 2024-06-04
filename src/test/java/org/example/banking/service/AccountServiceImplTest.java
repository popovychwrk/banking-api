package org.example.banking.service;

import org.example.banking.domain.dto.AccountDto;
import org.example.banking.domain.entities.AccountEntity;
import org.example.banking.repositories.AccountRepository;
import org.example.banking.services.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AccountEntity account1;
    private AccountEntity account2;

    @BeforeEach
    void setUp() {
        account1 = createAccount(1L, "123456789", new BigDecimal("1000"));
        account2 = createAccount(2L, "987654321", new BigDecimal("500"));
    }

    private AccountEntity createAccount(Long id, String accountNumber, BigDecimal balance) {
        AccountEntity account = new AccountEntity();
        account.setId(id);
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        return account;
    }

    @Test
    void shouldTransferFundsCorrectly() {
        BigDecimal transferAmount = new BigDecimal("200");
        mockAccountFindById(account1, account2);

        accountService.transfer(account1.getId(), account2.getId(), transferAmount);

        assertAll(
                "Validating both account balances after transfer",
                () -> assertEquals(0, account1.getBalance().compareTo(new BigDecimal("800")),
                        "Should decrease from account"),
                () -> assertEquals(0, account2.getBalance().compareTo(new BigDecimal("700")),
                        "Should increase to account")
        );

        verify(accountRepository, times(1)).save(account1);
        verify(accountRepository, times(1)).save(account2);
    }

    private void mockAccountFindById(AccountEntity... accounts) {
        for (AccountEntity account : accounts) {
            when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        }
    }

    @Test
    void shouldThrowExceptionForInsufficientFunds() {
        BigDecimal transferAmount = new BigDecimal("1200");

        mockAccountFindById(account1, account2);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.transfer(account1.getId(), account2.getId(), transferAmount),
                "Expected transfer to throw due to insufficient funds");

        assertEquals("Insufficient balance in the sender's account", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForTransferZeroAmount() {
        BigDecimal transferAmount = new BigDecimal("0");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(account1.getId(), account2.getId(), transferAmount),
                "Expected transfer of zero amount to throw Illegal Argument Exception");

        assertEquals("Transfer amount must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldNotifyWhenSenderAccountNotFound() {
        BigDecimal transferAmount = new BigDecimal("100");
        when(accountRepository.findById(account1.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.transfer(account1.getId(), account2.getId(), transferAmount),
                "Expected transfer to throw due to missing account");

        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void shouldWithdrawFundsCorrectly() {
        BigDecimal withdrawalAmount = new BigDecimal("200");
        when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));

        accountService.withdraw(account1.getId(), withdrawalAmount);

        assertEquals(new BigDecimal("800"), account1.getBalance(),
                "Should decrease the balance by withdrawal amount");

        verify(accountRepository, times(1)).save(account1);
    }

    @Test
    void shouldThrowExceptionForInsufficientWithdrawalFunds() {
        BigDecimal withdrawalAmount = new BigDecimal("1200");
        when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.withdraw(account1.getId(), withdrawalAmount),
                "Expected withdrawal to throw due to insufficient funds");

        assertEquals("Insufficient funds in account ID: 1", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForWithdrawZeroOrNegativeAmount() {
        BigDecimal withdrawalAmount = new BigDecimal("0");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(account1.getId(), withdrawalAmount),
                "Expected withdrawal of zero amount to throw Illegal Argument Exception");

        assertEquals("Withdrawal amount must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldNotifyWhenAccountNotFoundForWithdrawal() {
        BigDecimal withdrawalAmount = new BigDecimal("100");
        when(accountRepository.findById(account1.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.withdraw(account1.getId(), withdrawalAmount),
                "Expected withdrawal to throw due to missing account");

        assertEquals("Account not found with ID: 1", exception.getMessage());
    }

    @Test
    void shouldDepositCorrectly() {
        BigDecimal depositAmount = new BigDecimal("500");
        when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));

        doAnswer(invocation -> {
            AccountEntity account = invocation.getArgument(0);
            if (account.getId().equals(account1.getId())) {
                account.setBalance(account.getBalance().add(depositAmount));
            }
            return account;
        }).when(accountRepository).save(any(AccountEntity.class));

        AccountDto result = accountService.deposit(account1.getId(), depositAmount);

        assertEquals(new BigDecimal("2000"), account1.getBalance(),
                "Balance should be increased by deposit amount");

        assertEquals(account1.getId(), result.getId());
        assertEquals(account1.getAccountNumber(), result.getAccountNumber());
        assertEquals(account1.getBalance(), result.getBalance());

        verify(accountRepository, times(1)).save(account1);
    }

    @Test
    void shouldThrowExceptionForDepositZeroAmount() {
        BigDecimal depositAmount = new BigDecimal("0");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.deposit(account1.getId(), depositAmount),
                "Expected deposit of zero amount to throw Illegal Argument Exception");

        assertEquals("Deposit amount must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeDepositAmount() {
        BigDecimal depositAmount = new BigDecimal("-100");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.deposit(account1.getId(), depositAmount),
                "Expected deposit of negative amount to throw Illegal Argument Exception");

        assertEquals("Deposit amount must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldNotifyWhenAccountNotFoundOnDeposit() {
        BigDecimal depositAmount = new BigDecimal("100");
        when(accountRepository.findById(account1.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.deposit(account1.getId(), depositAmount),
                "Expected deposit to throw due to missing account");

        assertEquals("Account not found", exception.getMessage());
    }
}

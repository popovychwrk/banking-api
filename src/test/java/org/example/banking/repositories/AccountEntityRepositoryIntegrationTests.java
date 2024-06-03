package org.example.banking.repositories;

import org.example.banking.TestDataUtil;
import org.example.banking.domain.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountEntityRepositoryIntegrationTests {

    private final AccountRepository underTest;

    @Autowired
    public AccountEntityRepositoryIntegrationTests(AccountRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatAccountCanBeCreatedAndRecalled() {
        Account account = TestDataUtil.createTestAccountA();
        underTest.save(account);
        Optional<Account> result = underTest.findById(account.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(account);
    }

    @Test
    public void testThatMultipleAccountsCanBeCreatedAndRecalled() {
        Account accountA = TestDataUtil.createTestAccountA();
        Account accountB = TestDataUtil.createTestAccountB();
        Account accountC = TestDataUtil.createTestAccountC();
        underTest.save(accountA);
        underTest.save(accountB);
        underTest.save(accountC);

        Iterable<Account> result = underTest.findAll();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(accountA, accountB, accountC);
    }

    @Test
    public void testThatAccountCanBeUpdated() {
        Account account = TestDataUtil.createTestAccountA();
        underTest.save(account);
        account.setBalance(new BigDecimal("1000.00"));
        underTest.save(account);
        Optional<Account> result = underTest.findById(account.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(account);
    }

    @Test
    public void testThatAccountCanBeDeleted() {
        Account account = TestDataUtil.createTestAccountA();
        underTest.save(account);
        underTest.deleteById(account.getId());
        Optional<Account> result = underTest.findById(account.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatGetAccountWithBalanceLessThan() {
        Account accountA = TestDataUtil.createTestAccountA();
        Account accountB = TestDataUtil.createTestAccountB();
        Account accountC = TestDataUtil.createTestAccountC();
        underTest.save(accountA);
        underTest.save(accountB);
        underTest.save(accountC);

        Iterable<Account> result = underTest.findByBalanceLessThan(new BigDecimal("300.00"));
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(accountA, accountB);
    }

    @Test
    public void testThatGetAccountWithGreaterThenZero() {
        Account accountA = TestDataUtil.createTestAccountA();
        Account accountB = TestDataUtil.createTestAccountB();
        Account accountC = TestDataUtil.createTestAccountC();
        underTest.save(accountA);
        underTest.save(accountB);
        underTest.save(accountC);

        Iterable<Account> result = underTest.findByBalanceGreaterThanZero();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(accountA, accountB, accountC);
    }
}

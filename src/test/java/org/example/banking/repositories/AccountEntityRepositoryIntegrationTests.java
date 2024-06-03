package org.example.banking.repositories;

import org.example.banking.TestDataUtil;
import org.example.banking.domain.entities.AccountEntity;
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
        AccountEntity accountEntity = TestDataUtil.createTestAccountEntityA();
        underTest.save(accountEntity);
        Optional<AccountEntity> result = underTest.findById(accountEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(accountEntity);
    }

    @Test
    public void testThatMultipleAccountsCanBeCreatedAndRecalled() {
        AccountEntity accountEntityA = TestDataUtil.createTestAccountEntityA();
        AccountEntity accountEntityB = TestDataUtil.createTestAccountEntityB();
        AccountEntity accountEntityC = TestDataUtil.createTestAccountEntityC();
        underTest.save(accountEntityA);
        underTest.save(accountEntityB);
        underTest.save(accountEntityC);

        Iterable<AccountEntity> result = underTest.findAll();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(accountEntityA, accountEntityB, accountEntityC);
    }

    @Test
    public void testThatAccountCanBeUpdated() {
        AccountEntity accountEntity = TestDataUtil.createTestAccountEntityA();
        underTest.save(accountEntity);
        accountEntity.setBalance(new BigDecimal("1000.00"));
        underTest.save(accountEntity);
        Optional<AccountEntity> result = underTest.findById(accountEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(accountEntity);
    }

    @Test
    public void testThatAccountCanBeDeleted() {
        AccountEntity accountEntity = TestDataUtil.createTestAccountEntityA();
        underTest.save(accountEntity);
        underTest.deleteById(accountEntity.getId());
        Optional<AccountEntity> result = underTest.findById(accountEntity.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatGetAccountWithBalanceLessThan() {
        AccountEntity accountEntityA = TestDataUtil.createTestAccountEntityA();
        AccountEntity accountEntityB = TestDataUtil.createTestAccountEntityB();
        AccountEntity accountEntityC = TestDataUtil.createTestAccountEntityC();
        underTest.save(accountEntityA);
        underTest.save(accountEntityB);
        underTest.save(accountEntityC);

        Iterable<AccountEntity> result = underTest.findByBalanceLessThan(new BigDecimal("300.00"));
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(accountEntityA, accountEntityB);
    }

    @Test
    public void testThatGetAccountWithGreaterThenZero() {
        AccountEntity accountEntityA = TestDataUtil.createTestAccountEntityA();
        AccountEntity accountEntityB = TestDataUtil.createTestAccountEntityB();
        AccountEntity accountEntityC = TestDataUtil.createTestAccountEntityC();
        underTest.save(accountEntityA);
        underTest.save(accountEntityB);
        underTest.save(accountEntityC);

        Iterable<AccountEntity> result = underTest.findByBalanceGreaterThanZero();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(accountEntityA, accountEntityB, accountEntityC);
    }
}

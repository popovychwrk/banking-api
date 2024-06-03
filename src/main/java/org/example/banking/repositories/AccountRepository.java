package org.example.banking.repositories;

import org.example.banking.domain.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Iterable<Account> findByBalanceLessThan(BigDecimal bigDecimal);

    @Query("SELECT a FROM Account a WHERE a.balance > 0")
    Iterable<Account> findByBalanceGreaterThanZero();
}

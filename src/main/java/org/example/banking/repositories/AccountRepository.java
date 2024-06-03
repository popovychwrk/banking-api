package org.example.banking.repositories;

import org.example.banking.domain.entities.AccountEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends CrudRepository<AccountEntity, Long>,
        PagingAndSortingRepository<AccountEntity, Long> {

    Iterable<AccountEntity> findByBalanceLessThan(BigDecimal bigDecimal);

    @Query("SELECT a FROM AccountEntity a WHERE a.balance > 0")
    Iterable<AccountEntity> findByBalanceGreaterThanZero();
}

package org.example.banking;

import org.example.banking.domain.Account;

import java.math.BigDecimal;

public final class TestDataUtil {
    private TestDataUtil() {
    }

    public static Account createTestAccountA() {
        return Account.builder()
                .accountNumber("1234567890")
                .balance(new BigDecimal("100.00"))
                .build();
    }

    public static Account createTestAccountB() {
        return Account.builder()
                .accountNumber("0987654321")
                .balance(new BigDecimal("200.00"))
                .build();
    }

    public static Account createTestAccountC() {
        return Account.builder()
                .accountNumber("1357924680")
                .balance(new BigDecimal("300.00"))
                .build();
    }
}

package org.example.banking;

import org.example.banking.domain.dto.AccountDto;
import org.example.banking.domain.entities.AccountEntity;

import java.math.BigDecimal;

public final class TestDataUtil {
    private TestDataUtil() {
    }

    public static AccountEntity createTestAccountEntityA() {
        return AccountEntity.builder()
                .id(1L)
                .accountNumber("1234567890")
                .balance(new BigDecimal("100.00"))
                .build();
    }

    public static AccountEntity createTestAccountEntityB() {
        return AccountEntity.builder()
                .accountNumber("0987654321")
                .balance(new BigDecimal("200.00"))
                .build();
    }

    public static AccountEntity createTestAccountEntityC() {
        return AccountEntity.builder()
                .accountNumber("1357924680")
                .balance(new BigDecimal("300.00"))
                .build();
    }

    public static AccountDto createTestAccountDtoA() {
        return AccountDto.builder()
                .id(1L)
                .accountNumber("1234567890")
                .balance(new BigDecimal("100.00"))
                .build();
    }

    public static AccountDto createTestAccountDtoB() {
        return AccountDto.builder()
                .accountNumber("0987654321")
                .balance(new BigDecimal("200.00"))
                .build();
    }

    public static AccountDto createTestAccountDtoC() {
        return AccountDto.builder()
                .accountNumber("1357924680")
                .balance(new BigDecimal("300.00"))
                .build();
    }


}

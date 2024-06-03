package org.example.banking.mappers.impl;

import org.example.banking.domain.dto.AccountDto;
import org.example.banking.domain.entities.AccountEntity;
import org.example.banking.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AccountMapperImpl implements Mapper<AccountEntity, AccountDto> {

    private final ModelMapper modelMapper;

    public AccountMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public AccountDto mapTo(AccountEntity accountEntity) {
        return modelMapper.map(accountEntity, AccountDto.class);
    }

    @Override
    public AccountEntity mapFrom(AccountDto accountDto) {
        return modelMapper.map(accountDto, AccountEntity.class);
    }
}

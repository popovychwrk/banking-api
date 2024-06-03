package org.example.banking.mappers;

public interface Mapper<T, U> {
    U mapTo(T t);

    T mapFrom(U u);
}

package com.bigos.common.applciaiton.saga;


public interface SagaStep<T> {

    void process(T data);

    void rollback(T data);
}

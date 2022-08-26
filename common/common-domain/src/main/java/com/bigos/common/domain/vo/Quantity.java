package com.bigos.common.domain.vo;

public record Quantity(Integer numberOfElements) {

    public Quantity(Integer numberOfElements) {
        if (numberOfElements < 0) {
            throw new IllegalArgumentException("Number of elements cannot be lower than 0");
        }
        this.numberOfElements = numberOfElements;
    }
}

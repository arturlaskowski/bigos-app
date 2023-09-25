package com.bigos.order.domain.core;

import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.ProductId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Product {

    private ProductId id;

    private Money price;
}

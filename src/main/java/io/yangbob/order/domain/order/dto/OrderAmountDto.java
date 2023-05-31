package io.yangbob.order.domain.order.dto;

import lombok.experimental.Accessors;

public record OrderAmountDto(
        int shippingAmount,
        long productsAmount,
        @Accessors(fluent = true)
        boolean hasDiscount,
        long totalAmount
) {

}

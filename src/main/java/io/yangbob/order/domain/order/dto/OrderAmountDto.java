package io.yangbob.order.domain.order.dto;

import lombok.*;
import lombok.experimental.Accessors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class OrderAmountDto {
    private int shippingAmount;
    private long productsAmount;

    @Accessors(fluent = true)
    private boolean hasDiscount;
    private long totalAmount;
}

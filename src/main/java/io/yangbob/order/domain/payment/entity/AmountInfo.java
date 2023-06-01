package io.yangbob.order.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class AmountInfo {
    @Column(name = "amount_shipping", columnDefinition = "integer", nullable = false)
    private int shipping;

    @Column(name = "has_discount", columnDefinition = "boolean", nullable = false)
    @Accessors(fluent = true)
    private boolean hasDiscount;

    @Column(name = "amount_products", columnDefinition = "bigint", nullable = false)
    private long products;

    @Column(name = "amount_total", columnDefinition = "bigint", nullable = false)
    private long total;
}

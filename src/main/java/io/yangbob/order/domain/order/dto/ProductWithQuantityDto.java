package io.yangbob.order.domain.order.dto;

import io.yangbob.order.domain.product.entity.Product;
import lombok.*;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ProductWithQuantityDto {
    private Product product;
    private int quantity;
}

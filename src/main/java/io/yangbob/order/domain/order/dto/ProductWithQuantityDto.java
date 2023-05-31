package io.yangbob.order.domain.order.dto;

import io.yangbob.order.domain.product.entity.Product;


public record ProductWithQuantityDto(Product product, int quantity) {
}

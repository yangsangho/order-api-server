package io.yangbob.order.app.api.order.reqres.order;

import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;

public record ProductWithQuantityResponse(
        String productId,
        String name,
        int price,
        int quantity
) {
    public static ProductWithQuantityResponse from(OrderProduct orderProduct) {
        return new ProductWithQuantityResponse(
                orderProduct.getProduct().getId().toString(),
                orderProduct.getProduct().getName(),
                orderProduct.getProduct().getPrice(),
                orderProduct.getQuantity()
        );
    }
}

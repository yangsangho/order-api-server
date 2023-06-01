package io.yangbob.order.app.api.order.reqres.order;

import io.yangbob.order.app.api.order.reqres.takeorder.ShippingInfoReqRes;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import io.yangbob.order.domain.order.entity.order.OrderStatus;
import io.yangbob.order.domain.payment.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String orderId,
        OrderStatus status,
        List<ProductWithQuantityResponse> productWithQuantities,
        AmountInfoResponse amountInfo,
        LocalDateTime orderTime,
        OrdererInfoResponse ordererInfo,
        ShippingInfoReqRes shippingInfo
) {
    public static OrderResponse of(Order order, Payment payment) {
        return new OrderResponse(
                order.getId().toString(),
                order.getStatus(),
                order.getOrderProducts().stream().map(ProductWithQuantityResponse::from).toList(),
                AmountInfoResponse.of(order, payment),
                order.getCreatedAt(),
                OrdererInfoResponse.from(order.getOrderer()),
                ShippingInfoReqRes.from(order)
        );
    }
}

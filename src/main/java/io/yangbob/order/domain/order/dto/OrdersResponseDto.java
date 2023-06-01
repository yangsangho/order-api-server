package io.yangbob.order.domain.order.dto;

import io.yangbob.order.domain.order.entity.order.OrderStatus;

import java.time.LocalDateTime;

public record OrdersResponseDto(
        String representativeProductName,
        OrderStatus status,
        String address,
        long totalAmount,
        LocalDateTime orderTime
) {
}

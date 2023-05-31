package io.yangbob.order.domain.event;

import io.yangbob.order.domain.payment.entity.Payment;

public record PayEvent(Payment payment) {
}

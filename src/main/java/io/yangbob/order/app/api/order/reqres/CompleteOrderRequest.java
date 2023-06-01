package io.yangbob.order.app.api.order.reqres;

import io.yangbob.order.domain.payment.entity.PaymentMethod;

public record CompleteOrderRequest(PaymentMethod paymentMethod) {
}

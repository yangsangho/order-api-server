package io.yangbob.order.app.api.order.reqres.order;

import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.payment.entity.AmountInfo;
import io.yangbob.order.domain.payment.entity.Payment;

public record AmountInfoResponse(
        int shipping,
        boolean hasDiscount,
        long products,
        long total
) {
    public static AmountInfoResponse of(Order order, Payment payment) {
        AmountInfo amountInfo;
        if (payment == null) {
            amountInfo = order.getAmounts();
        } else {
            amountInfo = payment.getAmountInfo();
        }

        return new AmountInfoResponse(
                amountInfo.getShipping(),
                amountInfo.hasDiscount(),
                amountInfo.getProducts(),
                amountInfo.getTotal()
        );
    }
}

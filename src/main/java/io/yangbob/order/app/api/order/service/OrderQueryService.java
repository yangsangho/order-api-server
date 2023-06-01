package io.yangbob.order.app.api.order.service;

import io.yangbob.order.app.api.order.reqres.order.OrderResponse;
import io.yangbob.order.app.common.exception.NoResourceException;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import io.yangbob.order.domain.order.entity.order.OrderStatus;
import io.yangbob.order.domain.order.repository.OrderQueryRepository;
import io.yangbob.order.domain.payment.entity.Payment;
import io.yangbob.order.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderQueryRepository orderQueryRepository;
    private final PaymentRepository paymentRepository;

    public OrderResponse findOrder(String orderId) {
        Order order = orderQueryRepository.find(new OrderId(orderId)).orElseThrow(() -> new NoResourceException("order"));
        Payment payment = null;
        if (order.getStatus() == OrderStatus.COMPLETED) {
            payment = paymentRepository.findByOrder(order).orElseThrow(() -> new NoResourceException("payment"));
        }

        return OrderResponse.of(order, payment);
    }
}

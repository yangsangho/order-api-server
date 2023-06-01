package io.yangbob.order.domain.payment.repository;

import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.payment.entity.Payment;
import io.yangbob.order.domain.payment.entity.PaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, PaymentId> {
    Optional<Payment> findByOrder(Order order);
}

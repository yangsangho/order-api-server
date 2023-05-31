package io.yangbob.order.domain.order.repository;

import io.yangbob.order.domain.order.entity.Order;
import io.yangbob.order.domain.order.entity.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, OrderId> {
}

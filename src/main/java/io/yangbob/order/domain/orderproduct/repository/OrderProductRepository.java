package io.yangbob.order.domain.orderproduct.repository;

import io.yangbob.order.domain.orderproduct.entity.OrderProduct;
import io.yangbob.order.domain.orderproduct.entity.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductId> {
}

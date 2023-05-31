package io.yangbob.order.domain.order.entity.order;

import io.yangbob.order.EntityFactory;
import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;
import io.yangbob.order.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {
    @Test
    @DisplayName("OrderProducts Immutable 테스트")
    void orderProductsImmutableTest() {
        Order order = EntityFactory.createOrder();

        Product product = EntityFactory.createProduct("이어폰", 55000);
        OrderProduct orderProduct = new OrderProduct(order, product, 1);
        assertThatThrownBy(() -> order.getOrderProducts().add(orderProduct)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> order.getOrderProducts().set(0, orderProduct)).isInstanceOf(UnsupportedOperationException.class);
    }
}

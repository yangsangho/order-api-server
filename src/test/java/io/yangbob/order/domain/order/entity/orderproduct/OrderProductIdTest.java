package io.yangbob.order.domain.order.entity.orderproduct;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderProductIdTest {
    @Test
    void stringConstructorTest() {
        UUID uuid = UUID.randomUUID();
        OrderProductId id1 = new OrderProductId(uuid.toString());
        OrderProductId id2 = new OrderProductId(uuid.toString());

        assertThat(id1).isEqualTo(id2);
    }
}

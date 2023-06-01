package io.yangbob.order.domain.payment.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentIdTest {
    @Test
    void stringConstructorTest() {
        UUID uuid = UUID.randomUUID();
        PaymentId id1 = new PaymentId(uuid.toString());
        PaymentId id2 = new PaymentId(uuid.toString());

        assertThat(id1).isEqualTo(id2);
    }
}

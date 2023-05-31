package io.yangbob.order.domain.order.entity.order;

import io.yangbob.order.domain.order.entity.order.Receiver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReceiverTest {

    @Test
    @DisplayName("Value Object 테스트")
    void equalTest() {
        Receiver r1 = new Receiver("n1", "p1");
        Receiver r2 = new Receiver("n2", "p2");
        Receiver r3 = new Receiver("n1", "p1");

        assertThat(r1).isNotEqualTo(r2);
        assertThat(r3).isNotEqualTo(r2);
        assertThat(r1).isEqualTo(r3);
    }
}

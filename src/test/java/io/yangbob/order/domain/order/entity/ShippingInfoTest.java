package io.yangbob.order.domain.order.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShippingInfoTest {
    @Test
    @DisplayName("Value Object 테스트")
    void equalTest() {
        Receiver r1 = new Receiver("n1", "p1");

        ShippingInfo s1 = new ShippingInfo(r1, "address", "message");
        ShippingInfo s2 = new ShippingInfo(r1, "address", null);
        ShippingInfo s3 = new ShippingInfo(r1, "address", "message");

        assertThat(s1).isNotEqualTo(s2);
        assertThat(s3).isNotEqualTo(s2);
        assertThat(s1).isEqualTo(s3);
    }
}

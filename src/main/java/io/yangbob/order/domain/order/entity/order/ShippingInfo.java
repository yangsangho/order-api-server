package io.yangbob.order.domain.order.entity.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ShippingInfo {
    @Embedded
    private Receiver receiver;
    @Column(columnDefinition = "varchar", nullable = false)
    private String address;
    @Column(columnDefinition = "varchar")
    private String message;

    int getAmount() {
        return address.startsWith("서울") ? 0 : 3000;
    }
}

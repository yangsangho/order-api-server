package io.yangbob.order.domain.order.entity.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Receiver {
    @Column(name = "receiver_name", columnDefinition = "varchar", nullable = false)
    private String name;
    @Column(name = "receiver_phone_number", columnDefinition = "char(11)", nullable = false, length = 11)
    private String phoneNumber;
}

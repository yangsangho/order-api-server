package io.yangbob.order.domain.order.entity.orderproduct;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class OrderProductId implements Serializable {
    @Column(name = "orders_product_id", columnDefinition = "uuid", nullable = false)
    private final UUID id = UlidCreator.getMonotonicUlid().toUuid();
}

package io.yangbob.order.domain.order.entity;

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
public class OrderId implements Serializable {
    @Column(name = "orders_id", columnDefinition = "uuid", nullable = false)
    private UUID id = UlidCreator.getMonotonicUlid().toUuid();
}
package io.yangbob.order.domain.product.entity;

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
public class ProductId implements Serializable {
    @Column(name = "product_id", columnDefinition = "uuid", nullable = false)
    private UUID id = UlidCreator.getMonotonicUlid().toUuid();
}
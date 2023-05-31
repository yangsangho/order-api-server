package io.yangbob.order.domain.product.entity;

import io.yangbob.order.domain.common.entity.PrimaryKeyEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter(AccessLevel.PROTECTED)
@Getter
public class Product extends PrimaryKeyEntity<ProductId> {
    protected Product() {
        super(new ProductId());
    }

    public Product(String name, int price) {
        super(new ProductId());
        this.name = name;
        this.price = price;
    }

    @Column(columnDefinition = "varchar", nullable = false)
    private String name;

    @Column(columnDefinition = "integer", nullable = false)
    private int price;
}

package io.yangbob.order.domain.product.entity;

import io.yangbob.order.domain.common.entity.PrimaryKey;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@AttributeOverride(
        name = "id",
        column = @Column(name = "product_id")
)
public class ProductId extends PrimaryKey {
    public ProductId() {
        super();
    }

    public ProductId(String uuid) {
        super(uuid);
    }
}

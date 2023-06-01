package io.yangbob.order.domain.order.entity.orderproduct;

import io.yangbob.order.domain.common.entity.PrimaryKey;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@AttributeOverride(
        name = "id",
        column = @Column(name = "orders_product_id")
)
public class OrderProductId extends PrimaryKey {
    public OrderProductId() {
        super();
    }

    public OrderProductId(String uuid) {
        super(uuid);
    }
}

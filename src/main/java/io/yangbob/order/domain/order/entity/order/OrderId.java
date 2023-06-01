package io.yangbob.order.domain.order.entity.order;

import io.yangbob.order.domain.common.entity.PrimaryKey;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@AttributeOverride(
        name = "id",
        column = @Column(name = "orders_id")
)
public class OrderId extends PrimaryKey {
    public OrderId() {
        super();
    }

    public OrderId(String uuid) {
        super(uuid);
    }
}

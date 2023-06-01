package io.yangbob.order.domain.payment.entity;

import io.yangbob.order.domain.common.entity.PrimaryKey;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@AttributeOverride(
        name = "id",
        column = @Column(name = "payment_id")
)
public class PaymentId extends PrimaryKey {
    public PaymentId() {
        super();
    }

    public PaymentId(String uuid) {
        super(uuid);
    }
}

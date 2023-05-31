package io.yangbob.order.domain.payment.entity;

import io.yangbob.order.domain.common.entity.PrimaryKeyEntity;
import io.yangbob.order.domain.order.entity.order.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter(AccessLevel.PROTECTED)
@Getter
public class Payment extends PrimaryKeyEntity<PaymentId> {
    public Payment() {
        super(new PaymentId());
    }

    public Payment(Order order, PaymentMethod method) {
        super(new PaymentId());
        this.order = order;
        this.method = method;
        this.methodData = "{}";
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", columnDefinition = "uuid", nullable = false)
    private Order order;

    @Column(columnDefinition = "varchar", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(columnDefinition = "varchar", nullable = false)
    private String methodData;
}

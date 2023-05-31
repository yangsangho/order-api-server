package io.yangbob.order.domain.order.entity;

import io.yangbob.order.domain.common.entity.PrimaryKeyEntity;
import io.yangbob.order.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Setter(AccessLevel.PROTECTED)
@Getter
public class Order extends PrimaryKeyEntity<OrderId> {

    protected Order() {
        super(new OrderId());
    }

    public Order(Member orderer, ShippingInfo shippingInfo) {
        this();
        this.orderer = orderer;
        this.status = OrderStatus.RECEIPTED;
        this.shippingInfo = shippingInfo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "uuid", nullable = false)
    private Member orderer;

    @Column(columnDefinition = "varchar", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    private ShippingInfo shippingInfo;
}

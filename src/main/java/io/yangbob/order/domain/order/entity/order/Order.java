package io.yangbob.order.domain.order.entity.order;

import io.yangbob.order.domain.common.entity.PrimaryKeyEntity;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
@Setter(AccessLevel.PROTECTED)
@Getter
public class Order extends PrimaryKeyEntity<OrderId> {

    protected Order() {
        super(new OrderId());
    }

    public Order(Member orderer, ShippingInfo shippingInfo, List<ProductWithQuantityDto> productWithQuantityList) {
        this();
        this.orderer = orderer;
        this.status = OrderStatus.RECEIPTED;
        this.shippingInfo = shippingInfo;
        orderProducts.addAll(
                productWithQuantityList.stream()
                        .map(dto -> new OrderProduct(this, dto.getProduct(), dto.getQuantity()))
                        .toList()
        );
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "uuid", nullable = false)
    private Member orderer;

    @Column(columnDefinition = "varchar", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    private ShippingInfo shippingInfo;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    public List<OrderProduct> getOrderProducts() {
        return Collections.unmodifiableList(orderProducts);
    }
}

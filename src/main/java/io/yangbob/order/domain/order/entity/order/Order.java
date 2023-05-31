package io.yangbob.order.domain.order.entity.order;

import io.yangbob.order.domain.common.entity.PrimaryKeyEntity;
import io.yangbob.order.domain.event.PayEvent;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.order.dto.OrderAmountDto;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;
import io.yangbob.order.domain.payment.entity.Payment;
import io.yangbob.order.domain.payment.entity.PaymentMethod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;

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
        super(new OrderId());
        this.orderer = orderer;
        this.status = OrderStatus.RECEIPTED;
        this.shippingInfo = shippingInfo;
        orderProducts.addAll(
                productWithQuantityList.stream()
                        .map(dto -> new OrderProduct(this, dto.product(), dto.quantity()))
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

    public OrderAmountDto getAmounts() {
        final long productsAmount = orderProducts.stream().mapToLong(OrderProduct::getAmount).sum();
        final int shippingAmount = shippingInfo.getAmount();
        final boolean hasDiscount = hasDiscount();

        return new OrderAmountDto(
                shippingAmount,
                productsAmount,
                hasDiscount,
                (hasDiscount ? (long) (productsAmount * 0.9) : productsAmount) + shippingAmount
        );
    }

    private boolean hasDiscount() {
        return orderProducts.stream().mapToInt(OrderProduct::getQuantity).sum() >= 5;
    }

    public void pay(ApplicationEventPublisher publisher, PaymentMethod method) {
        Payment payment = new Payment(this, method);
        publisher.publishEvent(new PayEvent(payment));

        status = OrderStatus.COMPLETED;
    }
}

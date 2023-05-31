package io.yangbob.order.domain.order.entity.orderproduct;


import io.yangbob.order.domain.common.entity.PrimaryKeyEntity;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders_product")
@Setter(AccessLevel.PROTECTED)
@Getter
public class OrderProduct extends PrimaryKeyEntity<OrderProductId> {

    protected OrderProduct() {
        super(new OrderProductId());
    }

    public OrderProduct(Order order, Product product, int quantity) {
        this();
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", columnDefinition = "uuid", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", columnDefinition = "uuid", nullable = false)
    private Product product;

    @Column(columnDefinition = "integer", nullable = false)
    private int quantity;
}

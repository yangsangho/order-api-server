package io.yangbob.order.domain.order.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.yangbob.order.domain.order.data.OrderFilter;
import io.yangbob.order.domain.order.data.OrderSort;
import io.yangbob.order.domain.order.dto.OrdersResponseDto;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import io.yangbob.order.domain.order.entity.order.OrderStatus;
import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;
import io.yangbob.order.domain.payment.entity.Payment;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.yangbob.order.domain.member.entity.QMember.member;
import static io.yangbob.order.domain.order.entity.order.QOrder.order;
import static io.yangbob.order.domain.order.entity.orderproduct.QOrderProduct.orderProduct;
import static io.yangbob.order.domain.payment.entity.QPayment.payment;
import static io.yangbob.order.domain.product.entity.QProduct.product;

@Repository
public class OrderQueryRepository {
    public OrderQueryRepository(EntityManager em) {
        query = new JPAQueryFactory(em);
    }

    private final JPAQueryFactory query;

    public Optional<Order> find(OrderId id) {
        return Optional.ofNullable(
                query.selectFrom(order)
                        .join(order.orderer, member).fetchJoin()
                        .join(order.orderProducts, orderProduct).fetchJoin()
                        .join(orderProduct.product, product).fetchJoin()
                        .where(order.id.eq(id))
                        .fetchOne()
        );
    }

    public Page<OrdersResponseDto> findAll(Pageable pageable, OrderFilter filter) {
        int orderCount0 = query.selectFrom(order).fetch().size();
        Long orderCount = query.select(order.count()).from(order).where(getWhere(filter)).fetchOne();

        List<Order> orders = query.selectFrom(order)
                .orderBy(getOrderBy(pageable).toArray(new OrderSpecifier[]{}))
                .where(getWhere(filter))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Map<OrderId, List<OrderProduct>> orderProductMap = query.selectFrom(orderProduct)
                .join(orderProduct.product, product).fetchJoin()
                .where(orderProduct.order.in(orders))
                .fetch().stream().collect(Collectors.groupingBy(op -> op.getOrder().getId()));

        Map<OrderId, List<Payment>> paymentMap = query.selectFrom(payment)
                .where(payment.order.in(orders))
                .fetch().stream().collect(Collectors.groupingBy(p -> p.getOrder().getId()));

        List<OrdersResponseDto> result = new ArrayList<>();
        for (Order order : orders) {
            List<OrderProduct> orderProducts = orderProductMap.get(order.getId());
            order.updateOrderProducts(orderProducts);

            String representativeProductName = orderProducts.stream().findFirst().orElseThrow().getProduct().getName();
            if (orderProducts.size() > 1) {
                representativeProductName += " 외 " + (orderProducts.size() - 1) + "건";
            }

            long totalAmount;
            if (order.getStatus() == OrderStatus.COMPLETED) {
                totalAmount = paymentMap.get(order.getId()).stream().findFirst().orElseThrow().getAmountInfo().getTotal();
            } else {
                totalAmount = order.getAmounts().getTotal();
            }

            result.add(
                    new OrdersResponseDto(
                            representativeProductName,
                            order.getStatus(),
                            order.getShippingInfo().getAddress(),
                            totalAmount,
                            order.getCreatedAt()
                    )
            );
        }

        return new PageImpl<>(result, pageable, orderCount);
    }

    private static BooleanExpression getWhere(OrderFilter filter) {
        BooleanExpression result = null;

        if (filter == OrderFilter.STATUS_RECEIPTED) {
            result = order.status.eq(OrderStatus.RECEIPTED);
        } else if (filter == OrderFilter.STATUS_COMPLETED) {
            result = order.status.eq(OrderStatus.COMPLETED);
        }

        return result;
    }

    private static List<OrderSpecifier<?>> getOrderBy(Pageable pageable) {
        List<OrderSpecifier<?>> result = new ArrayList<>();

        Sort.Order orderTimeOrder = pageable.getSort().getOrderFor(OrderSort.ORDER_TIME.name());
        if (orderTimeOrder != null) {
            if (orderTimeOrder.isAscending()) {
                result.add(order.id.id.asc());
            } else if (orderTimeOrder.isDescending()) {
                result.add(order.id.id.desc());
            }
        }

        Sort.Order addressOrder = pageable.getSort().getOrderFor(OrderSort.ADDRESS.name());
        if (addressOrder != null) {
            if (addressOrder.isAscending()) {
                result.add(order.shippingInfo.address.asc());
            } else if (addressOrder.isDescending()) {
                result.add(order.shippingInfo.address.desc());
            }
        }

        return result;
    }

}

package io.yangbob.order.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static io.yangbob.order.domain.member.entity.QMember.member;
import static io.yangbob.order.domain.order.entity.order.QOrder.order;
import static io.yangbob.order.domain.order.entity.orderproduct.QOrderProduct.orderProduct;
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
}

package io.yangbob.order.domain.order.repository;

import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import io.yangbob.order.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import util.EntityFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderQueryRepositoryTest {

    @Autowired
    public OrderQueryRepositoryTest(MemberRepository memberRepository, ProductRepository productRepository, OrderRepository orderRepository, EntityManager em) {
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.em = em;
        this.orderQueryRepository = new OrderQueryRepository(em);
    }

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final EntityManager em;
    private final OrderQueryRepository orderQueryRepository;

    @Test
    void findOrderTest() {
        Member member = EntityFactory.createMember();
        List<ProductWithQuantityDto> productWithQuantities = EntityFactory.createProductWithQuantities();
        Order order = EntityFactory.createOorder(member, productWithQuantities);

        memberRepository.save(member);
        productWithQuantities.forEach(productWithQuantityDto -> productRepository.save(productWithQuantityDto.product()));
        OrderId orderId = order.getId();
        assertThat(orderRepository.findById(orderId).isPresent()).isFalse();

        orderRepository.save(order);
        em.flush();
        em.clear();

        System.out.println("--------------------------------------------------------------------------");

        Order findOrder = orderQueryRepository.find(order.getId()).get();
        System.out.println("orderer name = " + findOrder.getOrderer().getName());
        findOrder.getOrderProducts().forEach(orderProduct -> System.out.println("product name = " + orderProduct.getProduct().getName()));
    }
}

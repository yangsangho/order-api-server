package io.yangbob.order.domain.order.repository;

import io.yangbob.order.EntityFactory;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("기본적인 저장 및 조회")
    void saveAndFindAndDeleteTest() {
        Member member = EntityFactory.createMember();
        memberRepository.save(member);

        Receiver receiver = new Receiver(member.getName(), member.getPhoneNumber());
        ShippingInfo shippingInfo = new ShippingInfo(receiver, "서울특별시", "문 앞에 두세요");
        Order order = new Order(member, shippingInfo);
        OrderId orderId = order.getId();
        assertThat(orderRepository.findById(orderId).isPresent()).isFalse();

        orderRepository.save(order);
        em.flush();
        em.clear();

        Order findOrder = orderRepository.findById(orderId).get();
        assertThat(findOrder).isNotNull();
        assertThat(findOrder).isEqualTo(order);
        assertThat(findOrder.getOrderer()).isEqualTo(member);
        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.RECEIPTED);
        assertThat(findOrder.getShippingInfo()).isEqualTo(shippingInfo);

        orderRepository.delete(findOrder);
        em.flush();
        em.clear();

        assertThat(orderRepository.findById(orderId).isPresent()).isFalse();
    }
}

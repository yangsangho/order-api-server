package io.yangbob.order.domain.common.entity;

import util.EntityFactory;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PrimaryKeyEntityTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EntityManager em;


    @Test
    @DisplayName("Proxy Equals 등 테스트")
    void equalsTest() {
        Member member = EntityFactory.createMember();
        assertThat(member).isEqualTo(member);
        assertThat(member).isNotEqualTo(null);

        Order order = EntityFactory.createOorder(member, List.of());
        assertThat(member).isNotEqualTo(order);

        memberRepository.save(member);
        orderRepository.save(order);
        em.flush();
        em.clear();

        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(member).isEqualTo(findOrder.getOrderer());
    }

}

package io.yangbob.order.domain.payment.repository;

import io.yangbob.order.EntityFactory;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.repository.OrderRepository;
import io.yangbob.order.domain.payment.entity.Payment;
import io.yangbob.order.domain.payment.entity.PaymentId;
import io.yangbob.order.domain.payment.entity.PaymentMethod;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("기본적인 저장 및 조회")
    void saveAndFindAndDeleteTest() {
        Member member = EntityFactory.createMember();
        memberRepository.save(member);

        Order order = EntityFactory.createOrder(member, new ArrayList<>());
        orderRepository.save(order);

        PaymentMethod method = PaymentMethod.CARD;
        Payment payment = new Payment(order, method);
        PaymentId paymentId = payment.getId();
        assertThat(paymentRepository.findById(paymentId).isPresent()).isFalse();

        paymentRepository.save(payment);
        em.flush();
        em.clear();

        Payment findPayment = paymentRepository.findById(paymentId).get();
        assertThat(findPayment).isNotNull();
        assertThat(findPayment).isEqualTo(payment);
        assertThat(findPayment.getOrder()).isEqualTo(order);
        assertThat(findPayment.getMethod()).isEqualTo(method);
        assertThat(findPayment.getMethodData()).isEqualTo("{}");

        paymentRepository.delete(findPayment);
        em.flush();
        em.clear();

        assertThat(paymentRepository.findById(paymentId).isPresent()).isFalse();
    }
}

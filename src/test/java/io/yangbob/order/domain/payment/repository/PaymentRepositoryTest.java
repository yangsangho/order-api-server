package io.yangbob.order.domain.payment.repository;

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
import util.EntityFactory;

import java.util.List;
import java.util.Optional;

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
        Member member = new Member("yangbob", "01012341234");
        memberRepository.save(member);
        Order order = new Order(member, EntityFactory.createShippingInfo(member), List.of());
        orderRepository.save(order);

        PaymentMethod method = PaymentMethod.CARD;
        Payment payment = new Payment(order, method, order.getAmounts());
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

    @Test
    @DisplayName("Order로 찾기 테스트")
    void findByOrderTest() {
        Member member = new Member("yangbob", "01012341234");
        memberRepository.save(member);
        Order order = new Order(member, EntityFactory.createShippingInfo(member), List.of());
        orderRepository.save(order);

        assertThat(paymentRepository.findByOrder(order).isPresent()).isFalse();

        Payment payment = new Payment(order, PaymentMethod.CARD, order.getAmounts());
        paymentRepository.save(payment);
        em.flush();
        em.clear();

        Optional<Payment> findPayment = paymentRepository.findByOrder(order);
        assertThat(findPayment.isPresent()).isTrue();
        assertThat(findPayment.get()).isEqualTo(payment);
    }
}

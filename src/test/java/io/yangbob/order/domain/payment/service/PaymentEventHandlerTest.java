package io.yangbob.order.domain.payment.service;

import util.EntityFactory;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderStatus;
import io.yangbob.order.domain.order.repository.OrderRepository;
import io.yangbob.order.domain.payment.entity.Payment;
import io.yangbob.order.domain.payment.entity.PaymentMethod;
import io.yangbob.order.domain.payment.repository.PaymentRepository;
import io.yangbob.order.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PaymentEventHandlerTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("주문 완료처리 및 이벤트를 통해 Payment Entity 저장 테스트")
    void payAndSavePaymentTest() {
        Member member = EntityFactory.createMember();
        memberRepository.save(member);

        List<ProductWithQuantityDto> productWithQuantities = EntityFactory.createProductWithQuantities();
        productWithQuantities.forEach(productWithQuantityDto -> productRepository.save(productWithQuantityDto.product()));

        Order order = EntityFactory.createOorder(member, productWithQuantities);
        orderRepository.save(order);
        em.flush();
        em.clear();

        assertThat(paymentRepository.findAll()).isEmpty();

        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.RECEIPTED);
        PaymentMethod method = PaymentMethod.PHONE;
        findOrder.pay(publisher, method);

        em.flush();
        em.clear();

        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1);
        Payment savedPayment = payments.stream().findFirst().get();
        assertThat(savedPayment.getMethod()).isEqualTo(method);
        assertThat(savedPayment.getOrder().getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}

package io.yangbob.order.domain.orderproduct.repository;

import io.yangbob.order.EntityFactory;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.entity.Order;
import io.yangbob.order.domain.order.repository.OrderRepository;
import io.yangbob.order.domain.orderproduct.entity.OrderProduct;
import io.yangbob.order.domain.orderproduct.entity.OrderProductId;
import io.yangbob.order.domain.product.entity.Product;
import io.yangbob.order.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderProductRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("기본적인 저장 및 조회")
    void saveAndFindAndDeleteTest() {
        Member member = EntityFactory.createMember();
        memberRepository.save(member);

        Order order = EntityFactory.createOrder(member);
        orderRepository.save(order);

        Product product = EntityFactory.createProduct();
        productRepository.save(product);

        int quantity = 5;
        OrderProduct orderProduct = new OrderProduct(order, product, quantity);
        OrderProductId orderProductId = orderProduct.getId();
        orderProductRepository.save(orderProduct);

        em.flush();
        em.clear();

        OrderProduct findOrderProduct = orderProductRepository.findById(orderProductId).get();
        assertThat(findOrderProduct).isNotNull();
        assertThat(findOrderProduct).isEqualTo(orderProduct);
        assertThat(findOrderProduct.getOrder()).isEqualTo(order);
        assertThat(findOrderProduct.getProduct()).isEqualTo(product);
        assertThat(findOrderProduct.getQuantity()).isEqualTo(quantity);

        orderProductRepository.delete(findOrderProduct);
        em.flush();
        em.clear();

        assertThat(orderProductRepository.findById(orderProductId).isPresent()).isFalse();
    }
}

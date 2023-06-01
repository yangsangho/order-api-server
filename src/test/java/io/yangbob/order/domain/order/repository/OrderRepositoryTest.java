package io.yangbob.order.domain.order.repository;

import io.yangbob.order.EntityFactory;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import io.yangbob.order.domain.order.entity.order.OrderStatus;
import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;
import io.yangbob.order.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager em;


    @Test
    @DisplayName("기본적인 저장 및 조회")
    void saveAndFindAndDeleteTest() {
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

        Order findOrder = orderRepository.findById(orderId).get();
        assertThat(findOrder).isNotNull();
        assertThat(findOrder).isEqualTo(order);
        assertThat(findOrder.getOrderer()).isEqualTo(member);
        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.RECEIPTED);
        assertThat(findOrder.getShippingInfo()).isEqualTo(order.getShippingInfo());
        List<OrderProduct> orderProducts = findOrder.getOrderProducts();
        assertThat(orderProducts).hasSize(2);
        for (int i = 0; i < orderProducts.size(); i++) {
            assertThat(orderProducts.get(i).getProduct()).isEqualTo(productWithQuantities.get(i).product());
            assertThat(orderProducts.get(i).getQuantity()).isEqualTo(productWithQuantities.get(i).quantity());
        }

        orderRepository.delete(findOrder);
        em.flush();
        em.clear();

        assertThat(orderRepository.findById(orderId).isPresent()).isFalse();
    }
}

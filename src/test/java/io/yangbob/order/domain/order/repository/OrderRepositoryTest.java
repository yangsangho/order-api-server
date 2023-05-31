package io.yangbob.order.domain.order.repository;

import io.yangbob.order.EntityFactory;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.order.*;
import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;
import io.yangbob.order.domain.product.entity.Product;
import io.yangbob.order.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
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

    private final Member member = EntityFactory.createMember();
    private final Receiver receiver = new Receiver(member.getName(), member.getPhoneNumber());
    private final ShippingInfo shippingInfo = new ShippingInfo(receiver, "서울특별시", "문 앞에 두세요");
    private final ArrayList<ProductWithQuantityDto> productWithQuantityList = new ArrayList<>();

    {
        Product p1 = EntityFactory.createProduct();
        Product p2 = EntityFactory.createProduct("충전기", 6700);
        productWithQuantityList.add(new ProductWithQuantityDto(p1, 1));
        productWithQuantityList.add(new ProductWithQuantityDto(p2, 4));
    }

    @Test
    @DisplayName("기본적인 저장 및 조회")
    void saveAndFindAndDeleteTest() {
        memberRepository.save(member);
        productWithQuantityList.forEach(productWithQuantityDto -> productRepository.save(productWithQuantityDto.getProduct()));
        Order order = new Order(member, shippingInfo, productWithQuantityList);
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
        List<OrderProduct> orderProducts = findOrder.getOrderProducts();
        assertThat(orderProducts).hasSize(2);
        for (int i = 0; i < orderProducts.size(); i++) {
            assertThat(orderProducts.get(i).getProduct()).isEqualTo(productWithQuantityList.get(i).getProduct());
            assertThat(orderProducts.get(i).getQuantity()).isEqualTo(productWithQuantityList.get(i).getQuantity());
        }

        orderRepository.delete(findOrder);
        em.flush();
        em.clear();

        assertThat(orderRepository.findById(orderId).isPresent()).isFalse();
    }
}

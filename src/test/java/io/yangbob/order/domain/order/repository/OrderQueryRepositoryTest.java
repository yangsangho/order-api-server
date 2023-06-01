package io.yangbob.order.domain.order.repository;

import io.yangbob.order.domain.event.PayEvent;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.data.OrderFilter;
import io.yangbob.order.domain.order.data.OrderSort;
import io.yangbob.order.domain.order.dto.OrdersResponseDto;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import io.yangbob.order.domain.order.entity.order.ShippingInfo;
import io.yangbob.order.domain.payment.entity.Payment;
import io.yangbob.order.domain.payment.entity.PaymentMethod;
import io.yangbob.order.domain.payment.repository.PaymentRepository;
import io.yangbob.order.domain.product.entity.Product;
import io.yangbob.order.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import util.EntityFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@DataJpaTest
class OrderQueryRepositoryTest {

    @Autowired
    public OrderQueryRepositoryTest(MemberRepository memberRepository, ProductRepository productRepository, OrderRepository orderRepository, PaymentRepository paymentRepository, EntityManager em) {
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.em = em;
        this.orderQueryRepository = new OrderQueryRepository(em);
    }

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
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

    private static Stream<Arguments> findOrdersTestParams() {
        return Stream.of(
                Arguments.of(
                        new FindOrdersArgs(
                                0,
                                Sort.by(OrderSort.ORDER_TIME.name()).ascending(), OrderFilter.NONE,
                                5, 2,
                                new FindOrderContent("상품0", 0),
                                new FindOrderContent("상품1 외 1건", 1)
                        )
                ),
                Arguments.of(
                        new FindOrdersArgs(
                                1,
                                Sort.by(OrderSort.ORDER_TIME.name()).ascending(), null,
                                5, 2,
                                new FindOrderContent("상품0 외 1건", 2),
                                new FindOrderContent("상품2 외 2건", 3)
                        )
                ),
                Arguments.of(
                        new FindOrdersArgs(
                                2,
                                Sort.by(OrderSort.ORDER_TIME.name()).ascending(), OrderFilter.NONE,
                                5, 1,
                                new FindOrderContent("상품4", 4),
                                null
                        )
                ),
                Arguments.of(
                        new FindOrdersArgs(
                                1,
                                Sort.by(OrderSort.ORDER_TIME.name()).descending(), OrderFilter.NONE,
                                5, 2,
                                new FindOrderContent("상품0 외 1건", 2),
                                new FindOrderContent("상품1 외 1건", 1)
                        )
                ),
                Arguments.of(
                        new FindOrdersArgs(
                                0,
                                Sort.by(OrderSort.ADDRESS.name()).ascending(), OrderFilter.NONE,
                                5, 2,
                                new FindOrderContent("상품4", 4),
                                new FindOrderContent("상품0 외 1건", 2)
                        )
                ),
                Arguments.of(
                        new FindOrdersArgs(
                                1,
                                Sort.by(OrderSort.ADDRESS.name()).descending(), OrderFilter.NONE,
                                5, 2,
                                new FindOrderContent("상품2 외 2건", 3),
                                new FindOrderContent("상품0 외 1건", 2)
                        )
                ),
                Arguments.of(
                        new FindOrdersArgs(
                                0,
                                Sort.by(OrderSort.ORDER_TIME.name()).ascending(), OrderFilter.STATUS_RECEIPTED,
                                3, 2,
                                new FindOrderContent("상품1 외 1건", 1),
                                new FindOrderContent("상품2 외 2건", 3)
                        )
                ),
                Arguments.of(
                        new FindOrdersArgs(
                                0,
                                Sort.by(OrderSort.ORDER_TIME.name()).ascending(), OrderFilter.STATUS_COMPLETED,
                                2, 2,
                                new FindOrderContent("상품0", 0),
                                new FindOrderContent("상품0 외 1건", 2)
                        )
                )
        );
    }

    private record FindOrdersArgs(int page, Sort sort, OrderFilter filter, int expectedTotalSize,
                                  int expectedContentSize,
                                  FindOrderContent firstDto, FindOrderContent secondDto) {
    }

    private record FindOrderContent(String representativeProductName, int orderIdx) {
    }

    @ParameterizedTest
    @MethodSource("findOrdersTestParams")
    void findOrdersTest(FindOrdersArgs args) {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            members.add(new Member("yangbob" + i, "010" + String.valueOf(i).repeat(8)));
        }
        memberRepository.saveAll(members);

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            products.add(new Product("상품" + i, i * 1000));
        }
        productRepository.saveAll(products);

        List<Order> orders = List.of(
                new Order(
                        members.get(0),
                        new ShippingInfo(EntityFactory.createReceiver(members.get(0)), "서울특별시", null),
                        List.of(
                                new ProductWithQuantityDto(products.get(0), 3)
                        )
                ),
                new Order(
                        members.get(0),
                        new ShippingInfo(EntityFactory.createReceiver(members.get(0)), "서울특별시", null),
                        List.of(
                                new ProductWithQuantityDto(products.get(1), 1),
                                new ProductWithQuantityDto(products.get(2), 2)
                        )
                ),
                new Order(
                        members.get(1),
                        new ShippingInfo(EntityFactory.createReceiver(members.get(1)), "경기도", null),
                        List.of(
                                new ProductWithQuantityDto(products.get(0), 5),
                                new ProductWithQuantityDto(products.get(3), 1)
                        )
                ),
                new Order(
                        members.get(2),
                        new ShippingInfo(EntityFactory.createReceiver(members.get(2)), "부산광역시", null),
                        List.of(
                                new ProductWithQuantityDto(products.get(2), 1),
                                new ProductWithQuantityDto(products.get(3), 1),
                                new ProductWithQuantityDto(products.get(4), 1)
                        )
                ),
                new Order(
                        members.get(3),
                        new ShippingInfo(EntityFactory.createReceiver(members.get(3)), "강원도", null),
                        List.of(
                                new ProductWithQuantityDto(products.get(4), 10)
                        )
                )
        );

        ApplicationEventPublisher publisher = mock();
        doNothing().when(publisher).publishEvent(any(PayEvent.class));
        orders.get(0).pay(publisher, PaymentMethod.CARD);
        orders.get(2).pay(publisher, PaymentMethod.EASY);
        orderRepository.saveAll(orders);

        List<Payment> payments = List.of(
                new Payment(orders.get(0), PaymentMethod.CARD, orders.get(0).getAmounts()),
                new Payment(orders.get(2), PaymentMethod.EASY, orders.get(2).getAmounts())
        );
        paymentRepository.saveAll(payments);

        em.flush();
        em.clear();

        System.out.println("--------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------");

        Pageable pageable = PageRequest.of(args.page, 2, args.sort);
        System.out.println("pageable = " + pageable);
        Page<OrdersResponseDto> pageResponse = orderQueryRepository.findAll(pageable, args.filter);
        System.out.println(pageResponse.getContent());

        assertThat(pageResponse.getTotalElements()).isEqualTo(args.expectedTotalSize);
        assertThat(pageResponse.getPageable().getPageNumber()).isEqualTo(args.page);
        assertThat(pageResponse.getPageable().getPageSize()).isEqualTo(2);
        assertThat(pageResponse.getContent()).hasSize(args.expectedContentSize);
        if (args.firstDto != null) {
            OrdersResponseDto dto = pageResponse.getContent().get(0);
            Order order = orders.get(args.firstDto.orderIdx);
            checkOrdersResponseDto(dto, order, args.firstDto.representativeProductName());
        }
        if (args.secondDto != null) {
            OrdersResponseDto dto = pageResponse.getContent().get(1);
            Order order = orders.get(args.secondDto.orderIdx);
            checkOrdersResponseDto(dto, order, args.secondDto.representativeProductName());
        }
    }

    private void checkOrdersResponseDto(OrdersResponseDto dto, Order order, String representativeProductName) {
        assertThat(dto.representativeProductName()).isEqualTo(representativeProductName);
        assertThat(dto.status()).isEqualTo(order.getStatus());
        assertThat(dto.totalAmount()).isEqualTo(order.getAmounts().getTotal());
        assertThat(dto.orderTime()).isEqualToIgnoringNanos(order.getCreatedAt());
    }
}

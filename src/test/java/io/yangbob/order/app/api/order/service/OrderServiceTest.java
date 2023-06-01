package io.yangbob.order.app.api.order.service;

import io.yangbob.order.app.api.order.reqres.completeorder.CompleteOrderRequest;
import io.yangbob.order.app.api.order.reqres.takeorder.ProductWithQuantityRequest;
import io.yangbob.order.app.api.order.reqres.takeorder.ShippingInfoReqRes;
import io.yangbob.order.app.api.order.reqres.takeorder.TakeOrderRequest;
import io.yangbob.order.app.common.exception.NoResourceException;
import io.yangbob.order.domain.event.PayEvent;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import io.yangbob.order.domain.order.entity.order.OrderStatus;
import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;
import io.yangbob.order.domain.order.repository.OrderQueryRepository;
import io.yangbob.order.domain.order.repository.OrderRepository;
import io.yangbob.order.domain.payment.entity.PaymentMethod;
import io.yangbob.order.domain.product.entity.Product;
import io.yangbob.order.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import util.EntityFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderQueryRepository orderQueryRepository;
    @Mock
    private ApplicationEventPublisher publisher;

    @Test
    void takeOrderTest() {
        Member member = EntityFactory.createMember();
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        Product p1 = new Product("상품1", 1000);
        Product p2 = new Product("상품2", 2000);
        when(productRepository.findById(p1.getId())).thenReturn(Optional.of(p1));
        when(productRepository.findById(p2.getId())).thenReturn(Optional.of(p2));

        when(orderRepository.save(any(Order.class))).then(AdditionalAnswers.returnsFirstArg());

        TakeOrderRequest request = new TakeOrderRequest(
                member.getId().toString(),
                List.of(
                        new ProductWithQuantityRequest(p1.getId().toString(), 1),
                        new ProductWithQuantityRequest(p2.getId().toString(), 2)
                ),
                new ShippingInfoReqRes("yangbob", "01012341234", "서울특별시", "문 앞에 두세요")
        );

        OrderId createdId = orderService.takeOrder(request);

        ArgumentCaptor<Order> acOrder = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(acOrder.capture());

        Order savedOrder = acOrder.getValue();
        assertThat(createdId).isEqualTo(savedOrder.getId());
        assertThat(savedOrder.getOrderer()).isEqualTo(member);
        assertThat(savedOrder.getShippingInfo()).isEqualTo(request.makeShippingInfo());
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.RECEIPTED);
        List<OrderProduct> orderProducts = savedOrder.getOrderProducts();
        assertThat(orderProducts).hasSize(request.productWithQuantityRequests().size());
        for (int i = 0; i < orderProducts.size(); i++) {
            OrderProduct orderProduct = orderProducts.get(i);
            ProductWithQuantityRequest productWithQuantityRequest = request.productWithQuantityRequests().get(i);
            assertThat(orderProduct.getQuantity()).isEqualTo(productWithQuantityRequest.quantity());
            assertThat(orderProduct.getProduct().getId().toString()).isEqualTo(productWithQuantityRequest.productId());
            assertThat(orderProduct.getOrder()).isEqualTo(savedOrder);
        }
    }

    @Test
    void takeOrderNoMemberTest() {
        when(memberRepository.findById(any())).thenReturn(Optional.empty());

        TakeOrderRequest request = new TakeOrderRequest(
                UUID.randomUUID().toString(),
                List.of(),
                new ShippingInfoReqRes("yangbob", "01012341234", "서울특별시", "문 앞에 두세요")
        );

        assertThatThrownBy(() -> orderService.takeOrder(request)).isInstanceOf(NoResourceException.class);
    }

    @Test
    void takeOrderNoProductTest() {
        Member member = EntityFactory.createMember();
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        when(productRepository.findById(any())).thenReturn(Optional.empty());

        TakeOrderRequest request = new TakeOrderRequest(
                member.getId().toString(),
                List.of(new ProductWithQuantityRequest(UUID.randomUUID().toString(), 1)),
                new ShippingInfoReqRes("yangbob", "01012341234", "서울특별시", "문 앞에 두세요")
        );

        assertThatThrownBy(() -> orderService.takeOrder(request)).isInstanceOf(NoResourceException.class);
    }

    @Test
    void completeOrderTest() {
        Order order = EntityFactory.createOorder();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.RECEIPTED);

        when(orderQueryRepository.find(order.getId())).thenReturn(Optional.of(order));
        doNothing().when(publisher).publishEvent(any(PayEvent.class));

        PaymentMethod method = PaymentMethod.CARD;
        orderService.completeOrder(order.getId().toString(), new CompleteOrderRequest(method));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);

        ArgumentCaptor<PayEvent> acPayEvent = ArgumentCaptor.forClass(PayEvent.class);
        verify(publisher).publishEvent(acPayEvent.capture());
        assertThat(acPayEvent.getValue().payment().getMethod()).isEqualTo(method);
    }

    @Test
    void completeOrderNoOrderTest() {
        OrderId orderId = new OrderId();
        when(orderQueryRepository.find(orderId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.completeOrder(orderId.toString(), new CompleteOrderRequest(PaymentMethod.CARD))).isInstanceOf(NoResourceException.class);
    }
}

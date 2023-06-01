package io.yangbob.order.app.api.order.service;

import io.yangbob.order.app.api.order.reqres.order.OrderResponse;
import io.yangbob.order.app.api.order.reqres.order.ProductWithQuantityResponse;
import io.yangbob.order.app.common.exception.NoResourceException;
import io.yangbob.order.app.common.reqres.CommonPageResponse;
import io.yangbob.order.domain.event.PayEvent;
import io.yangbob.order.domain.order.data.OrderFilter;
import io.yangbob.order.domain.order.dto.OrdersResponseDto;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import io.yangbob.order.domain.order.entity.order.OrderStatus;
import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;
import io.yangbob.order.domain.order.repository.OrderQueryRepository;
import io.yangbob.order.domain.payment.entity.AmountInfo;
import io.yangbob.order.domain.payment.entity.Payment;
import io.yangbob.order.domain.payment.entity.PaymentMethod;
import io.yangbob.order.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import util.EntityFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {
    @InjectMocks
    private OrderQueryService orderQueryService;
    @Mock
    private OrderQueryRepository orderQueryRepository;
    @Mock
    private PaymentRepository paymentRepository;

    private static Stream<Arguments> findOrderTestParams() {
        Order order1 = EntityFactory.createOorder();
        Order order2 = EntityFactory.createOorder();
        ApplicationEventPublisher publisher = mock();
        doNothing().when(publisher).publishEvent(any());
        order2.pay(publisher, PaymentMethod.PHONE);
        Payment payment = new Payment(order2, PaymentMethod.PHONE, order2.getAmounts());

        return Stream.of(
                Arguments.of(order1, null, order1.getAmounts()),
                Arguments.of(order2, payment, payment.getAmountInfo())
        );
    }

    @ParameterizedTest
    @MethodSource("findOrderTestParams")
    void findOrderTest(Order order, Payment payment, AmountInfo amountInfo) {
        when(orderQueryRepository.find(order.getId())).thenReturn(Optional.of(order));
        if (payment != null) {
            when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(payment));
        }

        OrderResponse response = orderQueryService.findOrder(order.getId().toString());

        assertThat(response.orderId()).isEqualTo(order.getId().toString());
        assertThat(response.status()).isEqualTo(order.getStatus());

        for (int i = 0; i < order.getOrderProducts().size(); i++) {
            ProductWithQuantityResponse productWithQuantityResponse = response.productWithQuantities().get(i);
            OrderProduct orderProduct = order.getOrderProducts().get(i);

            assertThat(productWithQuantityResponse.productId()).isEqualTo(orderProduct.getProduct().getId().toString());
            assertThat(productWithQuantityResponse.name()).isEqualTo(orderProduct.getProduct().getName());
            assertThat(productWithQuantityResponse.price()).isEqualTo(orderProduct.getProduct().getPrice());
            assertThat(productWithQuantityResponse.quantity()).isEqualTo(orderProduct.getQuantity());
        }

        assertThat(response.amountInfo().shipping()).isEqualTo(amountInfo.getShipping());
        assertThat(response.amountInfo().hasDiscount()).isEqualTo(amountInfo.hasDiscount());
        assertThat(response.amountInfo().products()).isEqualTo(amountInfo.getProducts());
        assertThat(response.amountInfo().total()).isEqualTo(amountInfo.getTotal());

        assertThat(response.orderTime()).isEqualTo(order.getCreatedAt());
        assertThat(response.ordererInfo().name()).isEqualTo(order.getOrderer().getName());
        assertThat(response.ordererInfo().phoneNumber()).isEqualTo(order.getOrderer().getPhoneNumber());
        assertThat(response.shippingInfo().receiverName()).isEqualTo(order.getShippingInfo().getReceiver().getName());
        assertThat(response.shippingInfo().receiverPhoneNumber()).isEqualTo(order.getShippingInfo().getReceiver().getPhoneNumber());
        assertThat(response.shippingInfo().address()).isEqualTo(order.getShippingInfo().getAddress());
        assertThat(response.shippingInfo().message()).isEqualTo(order.getShippingInfo().getMessage());
    }

    @Test
    void findOrderNoOrderTest() {
        OrderId orderId = new OrderId();
        when(orderQueryRepository.find(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderQueryService.findOrder(orderId.toString())).isInstanceOf(NoResourceException.class);
    }

    @Test
    void findOrderNoPaymentTest() {
        Order order = EntityFactory.createOorder();
        ApplicationEventPublisher publisher = mock();
        doNothing().when(publisher).publishEvent(any(PayEvent.class));
        order.pay(publisher, PaymentMethod.PHONE);

        when(orderQueryRepository.find(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderQueryService.findOrder(order.getId().toString())).isInstanceOf(NoResourceException.class);
    }

    @Test
    void findOrdersTest() {
        List<OrdersResponseDto> dtos = List.of(
                new OrdersResponseDto("상품1 외 1건", OrderStatus.COMPLETED, "서울특별시", 10000, LocalDateTime.now()),
                new OrdersResponseDto("상품2", OrderStatus.RECEIPTED, "강원도", 20000, LocalDateTime.now()),
                new OrdersResponseDto("상품3 외 4건", OrderStatus.COMPLETED, "부산광역시", 45000, LocalDateTime.now())
        );

        Pageable pageable = PageRequest.of(10, 3);
        Page<OrdersResponseDto> pageResponse = new PageImpl<>(dtos, pageable, 99);
        when(orderQueryRepository.findAll(any(Pageable.class), any(OrderFilter.class))).thenReturn(pageResponse);

        CommonPageResponse<OrdersResponseDto> response = orderQueryService.findOrders(pageable, OrderFilter.NONE);
        assertThat(response.totalElementsCount()).isEqualTo(99);
        assertThat(response.totalPage()).isEqualTo(33);
        assertThat(response.page()).isEqualTo(10);
        assertThat(response.size()).isEqualTo(3);
        assertThat(response.elements()).containsAll(dtos);
    }
}

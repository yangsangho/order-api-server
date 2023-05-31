package io.yangbob.order.domain.order.entity.order;

import io.yangbob.order.EntityFactory;
import io.yangbob.order.domain.event.PayEvent;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.order.dto.OrderAmountDto;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;
import io.yangbob.order.domain.payment.entity.PaymentMethod;
import io.yangbob.order.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OrderTest {
    @Test
    @DisplayName("OrderProducts Immutable 테스트")
    void orderProductsImmutableTest() {
        Order order = EntityFactory.createOorder();

        Product product = new Product("이어폰", 55000);
        OrderProduct orderProduct = new OrderProduct(order, product, 1);
        assertThatThrownBy(() -> order.getOrderProducts().add(orderProduct)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> order.getOrderProducts().set(0, orderProduct)).isInstanceOf(UnsupportedOperationException.class);
    }

    static private class AmountTestParam {
        public AmountTestParam(int p1Quantity, int p2Quantity, String address, int expectedShippingAmount, boolean expectedHasDiscount, long expectedProductsAmount, long expectedTotalAmount) {
            this.p1Quantity = p1Quantity;
            this.p2Quantity = p2Quantity;
            this.address = address;
            this.expectedShippingAmount = expectedShippingAmount;
            this.expectedHasDiscount = expectedHasDiscount;
            this.expectedProductsAmount = expectedProductsAmount;
            this.expectedTotalAmount = expectedTotalAmount;
        }

        int p1Quantity;
        int p2Quantity;
        String address;
        int expectedShippingAmount;
        boolean expectedHasDiscount;
        long expectedProductsAmount;
        long expectedTotalAmount;
    }


    private static Stream<Arguments> amountTestParameters() {
        return Stream.of(
                Arguments.of(
                        "배송비 없고, 할인 없고",
                        new AmountTestParam(1, 3, "서울특별시", 0, false, 75000, 75000)
                ),
                Arguments.of(
                        "배송비 있고, 할인 없고",
                        new AmountTestParam(1, 3, "경기도", 3000, false, 75000, 78000)
                ),
                Arguments.of(
                        "배송비 없고, 할인 있고",
                        new AmountTestParam(1, 4, "서울특별시", 0, true, 85000, 76500)
                ),
                Arguments.of(
                        "배송비 있고, 할인 있고",
                        new AmountTestParam(1, 4, "부산광역시", 3000, true, 85000, 79500)
                )
        );
    }


    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("amountTestParameters")
    void amountTest(String msg, AmountTestParam param) {
        Member member = EntityFactory.createMember();
        Product p1 = new Product("선풍기", 45000);
        Product p2 = new Product("충전기", 10000);
        List<ProductWithQuantityDto> productWithQuantityList = List.of(new ProductWithQuantityDto(p1, param.p1Quantity), new ProductWithQuantityDto(p2, param.p2Quantity));

        Receiver receiver = new Receiver(member.getName(), member.getPhoneNumber());
        ShippingInfo shippingInfo = new ShippingInfo(receiver, param.address, "문 앞에 두세요");
        Order order = new Order(member, shippingInfo, productWithQuantityList);

        OrderAmountDto amounts = order.getAmounts();
        assertThat(amounts.shippingAmount()).isEqualTo(param.expectedShippingAmount);
        assertThat(amounts.hasDiscount()).isEqualTo(param.expectedHasDiscount);
        assertThat(amounts.productsAmount()).isEqualTo(param.expectedProductsAmount);
        assertThat(amounts.totalAmount()).isEqualTo(param.expectedTotalAmount);
    }

    @Test
    @DisplayName("결제 테스트 - 주문완료")
    void payTest() {
        ApplicationEventPublisher publisher = mock();
        doNothing().when(publisher).publishEvent(any(PayEvent.class));

        Order order = EntityFactory.createOorder();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.RECEIPTED);

        PaymentMethod method = PaymentMethod.EASY;
        order.pay(publisher, method);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);

        ArgumentCaptor<PayEvent> acPayEvent = ArgumentCaptor.forClass(PayEvent.class);
        verify(publisher).publishEvent(acPayEvent.capture());
        assertThat(acPayEvent.getValue().payment().getMethod()).isEqualTo(method);
    }
}



package io.yangbob.order.domain.order.entity.order;

import io.yangbob.order.EntityFactory;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.order.dto.OrderAmountDto;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.orderproduct.OrderProduct;
import io.yangbob.order.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {
    @Test
    @DisplayName("OrderProducts Immutable 테스트")
    void orderProductsImmutableTest() {
        Order order = EntityFactory.createOrder();

        Product product = EntityFactory.createProduct("이어폰", 55000);
        OrderProduct orderProduct = new OrderProduct(order, product, 1);
        assertThatThrownBy(() -> order.getOrderProducts().add(orderProduct)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> order.getOrderProducts().set(0, orderProduct)).isInstanceOf(UnsupportedOperationException.class);
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
        ArrayList<ProductWithQuantityDto> productWithQuantityList = new ArrayList<>();
        Product p1 = EntityFactory.createProduct("선풍기", 45000);
        Product p2 = EntityFactory.createProduct("충전기", 10000);
        productWithQuantityList.add(new ProductWithQuantityDto(p1, param.p1Quantity));
        productWithQuantityList.add(new ProductWithQuantityDto(p2, param.p2Quantity));

        Receiver receiver = new Receiver(member.getName(), member.getPhoneNumber());
        ShippingInfo shippingInfo = new ShippingInfo(receiver, param.address, "문 앞에 두세요");
        Order order = new Order(member, shippingInfo, productWithQuantityList);

        OrderAmountDto amounts = order.getAmounts();
        assertThat(amounts.getShippingAmount()).isEqualTo(param.expectedShippingAmount);
        assertThat(amounts.hasDiscount()).isEqualTo(param.expectedHasDiscount);
        assertThat(amounts.getProductsAmount()).isEqualTo(param.expectedProductsAmount);
        assertThat(amounts.getTotalAmount()).isEqualTo(param.expectedTotalAmount);
    }
}


class AmountTestParam {
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

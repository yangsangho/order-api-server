package io.yangbob.order.app.api.order.reqres;

import io.yangbob.order.app.api.order.reqres.takeorder.ProductWithQuantityRequest;
import io.yangbob.order.app.api.order.reqres.takeorder.ShippingInfoReqRes;
import io.yangbob.order.app.api.order.reqres.takeorder.TakeOrderRequest;
import io.yangbob.order.domain.order.entity.order.Receiver;
import io.yangbob.order.domain.order.entity.order.ShippingInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TakeOrderRequestTest {
    private static Stream<Arguments> createShippingInfoParams() {
        return Stream.of(
                Arguments.of("yangbob", "01012341234", "서울특별시", "문 앞에 두세요"),
                Arguments.of("yangbob", "01012341234", "서울특별시", null),
                Arguments.of(null, null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("createShippingInfoParams")
    void createShippingInfo(String name, String phoneNumber, String address, String message) {
        TakeOrderRequest request = new TakeOrderRequest(
                UUID.randomUUID().toString(),
                List.of(
                        new ProductWithQuantityRequest(UUID.randomUUID().toString(), 1),
                        new ProductWithQuantityRequest(UUID.randomUUID().toString(), 2)
                ),
                new ShippingInfoReqRes(name, phoneNumber, address, message)
        );

        assertThat(request.makeShippingInfo()).isEqualTo(
                new ShippingInfo(new Receiver(name, phoneNumber), address, message)
        );
    }
}

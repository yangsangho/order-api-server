package io.yangbob.order.app.api.order.reqres.takeorder;

import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.ShippingInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ShippingInfoReqRes(
        @NotNull
        @NotBlank
        String receiverName,
        @NotNull
        @NotBlank
        @Size(min = 11, max = 11)
        String receiverPhoneNumber,
        @NotNull
        @NotBlank
        String address,
        String message
) {
    public static ShippingInfoReqRes from(Order order) {
        ShippingInfo shippingInfo = order.getShippingInfo();
        return new ShippingInfoReqRes(
                shippingInfo.getReceiver().getName(),
                shippingInfo.getReceiver().getPhoneNumber(),
                shippingInfo.getAddress(),
                shippingInfo.getMessage()
        );
    }
}

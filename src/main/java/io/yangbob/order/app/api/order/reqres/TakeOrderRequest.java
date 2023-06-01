package io.yangbob.order.app.api.order.reqres;

import io.yangbob.order.app.common.validation.UUID;
import io.yangbob.order.domain.order.entity.order.Receiver;
import io.yangbob.order.domain.order.entity.order.ShippingInfo;
import jakarta.validation.Valid;

import java.util.List;

public record TakeOrderRequest(
        @UUID
        String ordererId,
        @Valid
        List<ProductWithQuantityRequest> productWithQuantityRequests,
        @Valid
        ShippingInfoRequest shippingInfoRequest
) {

    public ShippingInfo makeShippingInfo() {
        return new ShippingInfo(
                new Receiver(shippingInfoRequest.receiverName(), shippingInfoRequest.receiverPhoneNumber()),
                shippingInfoRequest.address(),
                shippingInfoRequest.message()
        );
    }
}


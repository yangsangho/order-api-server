package io.yangbob.order.app.api.order.reqres.takeorder;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.yangbob.order.app.common.validation.UUID;
import io.yangbob.order.domain.order.entity.order.Receiver;
import io.yangbob.order.domain.order.entity.order.ShippingInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TakeOrderRequest(
        @NotNull
        @UUID
        String ordererId,
        @NotNull
        @NotEmpty
        @Valid
        @JsonProperty("productWithQuantities")
        List<ProductWithQuantityRequest> productWithQuantityRequests,
        @NotNull
        @Valid
        @JsonProperty("shippingInfo")
        ShippingInfoReqRes shippingInfoRequest
) {

    public ShippingInfo makeShippingInfo() {
        return new ShippingInfo(
                new Receiver(shippingInfoRequest.receiverName(), shippingInfoRequest.receiverPhoneNumber()),
                shippingInfoRequest.address(),
                shippingInfoRequest.message()
        );
    }
}


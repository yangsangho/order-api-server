package io.yangbob.order.app.api.order.reqres;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShippingInfoRequest(
        @NotBlank
        String receiverName,
        @NotBlank
        @Size(min = 11, max = 11)
        String receiverPhoneNumber,
        @NotBlank
        String address,
        String message
) {
}

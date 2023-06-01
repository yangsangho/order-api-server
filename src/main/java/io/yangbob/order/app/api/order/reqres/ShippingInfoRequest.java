package io.yangbob.order.app.api.order.reqres;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ShippingInfoRequest(
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
}

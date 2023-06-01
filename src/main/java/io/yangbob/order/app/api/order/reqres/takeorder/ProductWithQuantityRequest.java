package io.yangbob.order.app.api.order.reqres.takeorder;

import io.yangbob.order.app.common.validation.UUID;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record ProductWithQuantityRequest(
        @NotNull
        @UUID
        String productId,
        @NotNull
        @Range(min = 1, max = 999)
        int quantity
) {
}

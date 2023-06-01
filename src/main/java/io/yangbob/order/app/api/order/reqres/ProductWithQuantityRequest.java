package io.yangbob.order.app.api.order.reqres;

import io.yangbob.order.app.common.validation.UUID;
import org.hibernate.validator.constraints.Range;

public record ProductWithQuantityRequest(
        @UUID
        String productId,
        @Range(min = 1, max = 999)
        int quantity
) {
}

package io.yangbob.order.app.common.reqres;

import java.util.List;

public record CommonPageResponse<T>(
        long totalElementsCount,
        int totalPage,
        int page,
        int size,
        List<T> elements
) {
}

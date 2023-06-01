package io.yangbob.order.app.api.order.controller;

import io.yangbob.order.app.api.order.reqres.completeorder.CompleteOrderRequest;
import io.yangbob.order.app.api.order.reqres.order.OrderResponse;
import io.yangbob.order.app.api.order.reqres.takeorder.CreatedOrderId;
import io.yangbob.order.app.api.order.reqres.takeorder.TakeOrderRequest;
import io.yangbob.order.app.api.order.service.OrderQueryService;
import io.yangbob.order.app.api.order.service.OrderService;
import io.yangbob.order.app.common.reqres.CommonPageResponse;
import io.yangbob.order.app.common.validation.UUID;
import io.yangbob.order.domain.order.data.OrderFilter;
import io.yangbob.order.domain.order.dto.OrdersResponseDto;
import io.yangbob.order.domain.order.entity.order.OrderId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrderController {
    private final OrderService orderService;
    private final OrderQueryService orderQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreatedOrderId takeOrder(@Valid @RequestBody TakeOrderRequest request) {
        log.info("takeOrder request = {}", request);
        OrderId createdID = orderService.takeOrder(request);
        return new CreatedOrderId(createdID.toString());
    }

    @PostMapping("/{order-id}")
    void completeOrder(
            @Valid @UUID @PathVariable("order-id") String orderId,
            @RequestBody CompleteOrderRequest request
    ) {
        log.info("completeOrder orderId = {}, request = {}", orderId, request);
        orderService.completeOrder(orderId, request);
    }

    @GetMapping
    CommonPageResponse<OrdersResponseDto> findOrders(
            @PageableDefault(size = 5, sort = "ORDER_TIME", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false, defaultValue = "NONE") OrderFilter filter
    ) {
        log.info("findOrders pageable = {}, filter = {}", pageable, filter);
        return orderQueryService.findOrders(pageable, filter);
    }

    @GetMapping("/{order-id}")
    OrderResponse findOrder(@Valid @UUID @PathVariable("order-id") String orderId) {
        log.info("findOrder orderId = {}", orderId);
        return orderQueryService.findOrder(orderId);
    }
}

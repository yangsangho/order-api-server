package io.yangbob.order.app.api.order.controller;

import io.yangbob.order.app.api.order.reqres.CompleteOrderRequest;
import io.yangbob.order.app.api.order.reqres.CreatedOrderId;
import io.yangbob.order.app.api.order.reqres.TakeOrderRequest;
import io.yangbob.order.app.api.order.service.OrderService;
import io.yangbob.order.app.common.validation.UUID;
import io.yangbob.order.domain.order.entity.order.OrderId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("takeOrder orderId = {}, request = {}", orderId, request);
        orderService.completeOrder(orderId, request);
    }
}

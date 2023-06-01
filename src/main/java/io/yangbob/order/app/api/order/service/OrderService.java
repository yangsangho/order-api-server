package io.yangbob.order.app.api.order.service;

import io.yangbob.order.app.api.order.reqres.completeorder.CompleteOrderRequest;
import io.yangbob.order.app.api.order.reqres.takeorder.TakeOrderRequest;
import io.yangbob.order.app.common.exception.NoResourceException;
import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.entity.MemberId;
import io.yangbob.order.domain.member.repository.MemberRepository;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import io.yangbob.order.domain.order.entity.order.OrderStatus;
import io.yangbob.order.domain.order.repository.OrderQueryRepository;
import io.yangbob.order.domain.order.repository.OrderRepository;
import io.yangbob.order.domain.product.entity.Product;
import io.yangbob.order.domain.product.entity.ProductId;
import io.yangbob.order.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ApplicationEventPublisher publisher;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    public OrderId takeOrder(TakeOrderRequest request) {
        Member member = memberRepository.findById(new MemberId(request.ordererId())).orElseThrow(() -> new NoResourceException("member"));

        Order order = new Order(
                member,
                request.makeShippingInfo(),
                request.productWithQuantityRequests().stream().map(p -> {
                    Product product = productRepository.findById(new ProductId(p.productId())).orElseThrow(() -> new NoResourceException("product"));
                    return new ProductWithQuantityDto(product, p.quantity());
                }).toList()
        );

        return orderRepository.save(order).getId();
    }

    public void completeOrder(String orderId, CompleteOrderRequest request) {
        Order order = orderQueryRepository.find(new OrderId(orderId)).orElseThrow(() -> new NoResourceException("order"));
        if (order.getStatus() == OrderStatus.COMPLETED) return;

        order.pay(publisher, request.paymentMethod());
    }
}

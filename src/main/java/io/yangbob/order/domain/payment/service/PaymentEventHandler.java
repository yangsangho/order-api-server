package io.yangbob.order.domain.payment.service;

import io.yangbob.order.domain.event.PayEvent;
import io.yangbob.order.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventHandler {
    private final PaymentRepository paymentRepository;

    @EventListener
    public void payEventHandler(PayEvent payEvent) {
        log.info("payEvent = {}", payEvent);
        paymentRepository.save(payEvent.payment());
    }
}

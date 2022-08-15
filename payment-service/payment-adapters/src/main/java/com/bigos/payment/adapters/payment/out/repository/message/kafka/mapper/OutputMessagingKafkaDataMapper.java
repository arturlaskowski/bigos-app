package com.bigos.payment.adapters.payment.out.repository.message.kafka.mapper;

import com.bigos.infrastructure.kafka.model.PaymentMessageDto;
import com.bigos.infrastructure.kafka.model.events.PaymentStatusEventDtoKafka;
import com.bigos.payment.domain.event.PaymentCancelledEvent;
import com.bigos.payment.domain.event.PaymentCompletedEvent;
import com.bigos.payment.domain.event.PaymentRejectedEvent;
import com.bigos.payment.domain.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class OutputMessagingKafkaDataMapper {

    public PaymentStatusEventDtoKafka paymentCompletedEventToPaymentStatusEventDtoKafka(PaymentCompletedEvent paymentCompletedEvent) {
        Payment payment = paymentCompletedEvent.getPayment();
        String paymentId = payment.getId().id().toString();
        PaymentMessageDto paymentMessageDto = paymentToPaymentMessageDto(payment, paymentId);
        return new PaymentStatusEventDtoKafka(paymentMessageDto, paymentId, paymentCompletedEvent.getCreatedAt(), "toDo");
    }

    public PaymentStatusEventDtoKafka paymentCancelledEventToPaymentStatusEventDtoKafka(PaymentCancelledEvent paymentCancelledEvent) {
        Payment payment = paymentCancelledEvent.getPayment();
        String paymentId = payment.getId().id().toString();
        PaymentMessageDto paymentMessageDto = paymentToPaymentMessageDto(payment, paymentId);
        return new PaymentStatusEventDtoKafka(paymentMessageDto, paymentId, paymentCancelledEvent.getCreatedAt(), "toDo");
    }

    public PaymentStatusEventDtoKafka paymentRejectedEventToPaymentStatusEventDtoKafka(PaymentRejectedEvent paymentRejectedEvent) {
        Payment payment = paymentRejectedEvent.getPayment();
        String paymentId = payment.getId().id().toString();
        PaymentMessageDto paymentMessageDto = PaymentMessageDto.builder()
                .paymentId(paymentId)
                .orderId(payment.getOrderId().id().toString())
                .customerId(payment.getCustomerId().id().toString())
                .price(payment.getPrice().amount())
                .paymentStatus(payment.getStatus().name())
                .failureMessages(paymentRejectedEvent.getFailureMessages())
                .build();
        return new PaymentStatusEventDtoKafka(paymentMessageDto, paymentId, paymentRejectedEvent.getCreatedAt(), "toDo");
    }

    private static PaymentMessageDto paymentToPaymentMessageDto(Payment payment, String paymentId) {
        return PaymentMessageDto.builder()
                .paymentId(paymentId)
                .orderId(payment.getOrderId().id().toString())
                .customerId(payment.getCustomerId().id().toString())
                .price(payment.getPrice().amount())
                .paymentStatus(payment.getStatus().name())
                .build();
    }
}

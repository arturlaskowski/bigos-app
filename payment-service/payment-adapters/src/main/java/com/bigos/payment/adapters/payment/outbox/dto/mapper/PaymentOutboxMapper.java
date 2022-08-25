package com.bigos.payment.adapters.payment.outbox.dto.mapper;

import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.infrastructure.kafka.model.PaymentMessageDto;
import com.bigos.infrastructure.kafka.model.events.PaymentStatusEventDtoKafka;
import com.bigos.payment.adapters.payment.outbox.dto.PaymentStatusEventPayload;
import com.bigos.payment.domain.event.PaymentEvent;
import com.bigos.payment.domain.event.PaymentRejectedEvent;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.ports.dto.outbox.PaymentCancelledOutboxMessage;
import com.bigos.payment.domain.ports.dto.outbox.PaymentCompletedOutboxMessage;
import com.bigos.payment.domain.ports.dto.outbox.PaymentOutboxMessage;
import com.bigos.payment.domain.ports.dto.outbox.PaymentRejectedOutboxMessage;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentOutboxMapper {

    public PaymentCompletedOutboxMessage paymentEventToPaymentCompletedOutboxMessage(PaymentEvent paymentEvent, UUID sagaId) {
        Payment payment = paymentEvent.getPayment();
        PaymentCompletedOutboxMessage paymentCompletedOutboxMessage = new PaymentCompletedOutboxMessage();
        paymentCompletedOutboxMessage.setId(UUID.randomUUID());
        paymentCompletedOutboxMessage.setSagaId(sagaId);
        paymentCompletedOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        paymentCompletedOutboxMessage.setAggregateId(payment.getId().id());
        paymentCompletedOutboxMessage.setAggregateName(payment.getClass().getSimpleName());
        paymentCompletedOutboxMessage.setPaymentStatus(payment.getStatus());
        paymentCompletedOutboxMessage.setCreatedDate(paymentEvent.getCreatedAt());

        PaymentStatusEventPayload payload = PaymentStatusEventPayload.builder()
                .paymentId(payment.getId().id().toString())
                .createdDate(payment.getCreationDate())
                .orderId(payment.getOrderId().id().toString())
                .customerId(payment.getCustomerId().id().toString())
                .paymentStatus(payment.getStatus().name())
                .price(payment.getPrice().amount())
                .build();

        paymentCompletedOutboxMessage.setPayload(payload);

        return paymentCompletedOutboxMessage;
    }

    public PaymentRejectedOutboxMessage paymentEventToPaymentRejectedOutboxMessage(PaymentEvent paymentEvent, UUID sagaId) {
        Payment payment = paymentEvent.getPayment();
        PaymentRejectedOutboxMessage paymentRejectedOutboxMessage = new PaymentRejectedOutboxMessage();
        paymentRejectedOutboxMessage.setId(UUID.randomUUID());
        paymentRejectedOutboxMessage.setSagaId(sagaId);
        paymentRejectedOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        paymentRejectedOutboxMessage.setAggregateId(payment.getId().id());
        paymentRejectedOutboxMessage.setAggregateName(payment.getClass().getSimpleName());
        paymentRejectedOutboxMessage.setPaymentStatus(payment.getStatus());
        paymentRejectedOutboxMessage.setCreatedDate(paymentEvent.getCreatedAt());

        PaymentStatusEventPayload payload = PaymentStatusEventPayload.builder()
                .paymentId(payment.getId().id().toString())
                .createdDate(payment.getCreationDate())
                .orderId(payment.getOrderId().id().toString())
                .customerId(payment.getCustomerId().id().toString())
                .paymentStatus(payment.getStatus().name())
                .price(payment.getPrice().amount())
                .failureMessages(((PaymentRejectedEvent) paymentEvent).getFailureMessages())
                .build();

        paymentRejectedOutboxMessage.setPayload(payload);

        return paymentRejectedOutboxMessage;
    }

    public PaymentCancelledOutboxMessage paymentEventToPaymentCancelledOutboxMessage(PaymentEvent paymentEvent, UUID sagaId) {
        Payment payment = paymentEvent.getPayment();
        PaymentCancelledOutboxMessage paymentCancelledOutboxMessage = new PaymentCancelledOutboxMessage();
        paymentCancelledOutboxMessage.setId(UUID.randomUUID());
        paymentCancelledOutboxMessage.setSagaId(sagaId);
        paymentCancelledOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        paymentCancelledOutboxMessage.setAggregateId(payment.getId().id());
        paymentCancelledOutboxMessage.setAggregateName(payment.getClass().getSimpleName());
        paymentCancelledOutboxMessage.setPaymentStatus(payment.getStatus());
        paymentCancelledOutboxMessage.setCreatedDate(paymentEvent.getCreatedAt());

        PaymentStatusEventPayload payload = PaymentStatusEventPayload.builder()
                .paymentId(payment.getId().id().toString())
                .createdDate(payment.getCreationDate())
                .orderId(payment.getOrderId().id().toString())
                .customerId(payment.getCustomerId().id().toString())
                .paymentStatus(payment.getStatus().name())
                .price(payment.getPrice().amount())
                .build();

        paymentCancelledOutboxMessage.setPayload(payload);

        return paymentCancelledOutboxMessage;
    }

    public PaymentStatusEventDtoKafka paymentOutboxMessageToPaymentStatusEventDtoKafka(PaymentOutboxMessage paymentOutboxMessage) {
        PaymentStatusEventPayload payload = (PaymentStatusEventPayload) paymentOutboxMessage.getPayload();
        PaymentMessageDto paymentMessageDto = paymentStatusEventPayloadToPaymentMessageDto(payload);

        return new PaymentStatusEventDtoKafka(paymentMessageDto, payload.paymentId(), paymentOutboxMessage.getCreatedDate(), paymentOutboxMessage.getSagaId().toString());
    }

    private static PaymentMessageDto paymentStatusEventPayloadToPaymentMessageDto(PaymentStatusEventPayload payload) {
        return PaymentMessageDto.builder()
                .paymentId(payload.paymentId())
                .orderId(payload.orderId())
                .customerId(payload.customerId())
                .price(payload.price())
                .paymentStatus(payload.paymentStatus())
                .failureMessages(payload.failureMessages())
                .build();
    }
}

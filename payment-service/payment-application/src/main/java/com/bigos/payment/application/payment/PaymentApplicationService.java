package com.bigos.payment.application.payment;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.payment.application.payment.dto.CancelPaymentCommand;
import com.bigos.payment.application.payment.dto.MakePaymentCommand;
import com.bigos.payment.application.payment.outbox.dto.*;
import com.bigos.payment.application.payment.outbox.repository.PaymentOutboxRepository;
import com.bigos.payment.domain.PaymentDomainService;
import com.bigos.payment.domain.core.Payment;
import com.bigos.payment.domain.core.Wallet;
import com.bigos.payment.domain.event.PaymentEvent;
import com.bigos.payment.domain.port.PaymentRepository;
import com.bigos.payment.domain.port.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class PaymentApplicationService {

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final PaymentDomainService paymentDomainService;
    private final PaymentOutboxRepository outboxRepository;
    private final PaymentOutboxMapper outboxMapper;

    public PaymentApplicationService(final PaymentRepository paymentRepository,
                                     final WalletRepository walletRepository,
                                     final PaymentDomainService paymentDomainService,
                                     final PaymentOutboxRepository outboxRepository,
                                     final PaymentOutboxMapper outboxMapper) {
        this.paymentRepository = paymentRepository;
        this.walletRepository = walletRepository;
        this.paymentDomainService = paymentDomainService;
        this.outboxRepository = outboxRepository;
        this.outboxMapper = outboxMapper;
    }

    @Transactional
    public void makePayment(MakePaymentCommand makePaymentCommand) {
        UUID sagaId = makePaymentCommand.sagaId();

        if (messageAlreadyProcessed(makePaymentCommand.sagaId(), PaymentStatus.COMPLETED)) {
            log.info("Message with saga id: {} is already processed", makePaymentCommand.sagaId());
            return;
        }

        Wallet wallet = walletRepository.getByCustomerId(new CustomerId((makePaymentCommand.customerId())));
        Payment payment = completePaymentCommandToPayment(makePaymentCommand);

        PaymentEvent paymentEvent = paymentDomainService.makePayment(payment, wallet);

        paymentRepository.save(payment);

        if (payment.isCompleted()) {
            walletRepository.save(wallet);

            savePaymentCompletedOutboxMessage(sagaId, paymentEvent);

        } else if (payment.isRejected()) {
            savePaymentRejectedOutboxMessage(sagaId, paymentEvent);
        }
    }

    private boolean messageAlreadyProcessed(UUID makePaymentCommand, PaymentStatus completed) {
        return outboxRepository.existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus
                (PaymentOutboxMessage.class, makePaymentCommand, completed, OutboxStatus.COMPLETED);
    }

    @Transactional
    public void cancelPayment(CancelPaymentCommand cancelPaymentCommand) {
        UUID sagaId = cancelPaymentCommand.sagaId();

        boolean messageProcessed = messageAlreadyProcessed(sagaId, PaymentStatus.CANCELLED);

        if (messageProcessed) {
            log.info("Message with saga id: {} is already processed", sagaId);
            return;
        }

        Wallet wallet = walletRepository.getByCustomerId(new CustomerId((cancelPaymentCommand.customerId())));
        Payment payment = paymentRepository.getByOrderId(new OrderId(cancelPaymentCommand.orderId()));

        PaymentEvent paymentEvent = paymentDomainService.cancelPayment(payment, wallet);

        paymentRepository.save(payment);

        if (payment.isCanceled()) {
            walletRepository.save(wallet);

            savePaymentCancelledOutboxMessage(sagaId, paymentEvent);

        } else if (payment.isRejected()) {
            //TODO Add a repetition counter and, after analysis, make a decision on whether it's worth implementing such a scenario or better to resolve it manually.
            savePaymentRejectedOutboxMessage(sagaId, paymentEvent);
        }
    }

    private void savePaymentCancelledOutboxMessage(UUID sagaId, PaymentEvent paymentEvent) {
        PaymentCancelledOutboxMessage paymentCancelledOutboxMessage =
                outboxMapper.paymentEventToPaymentCancelledOutboxMessage(paymentEvent, sagaId);

        outboxRepository.save(paymentCancelledOutboxMessage);
    }

    private void savePaymentRejectedOutboxMessage(UUID sagaId, PaymentEvent paymentEvent) {
        PaymentRejectedOutboxMessage paymentRejectedOutboxMessage =
                outboxMapper.paymentEventToPaymentRejectedOutboxMessage(paymentEvent, sagaId);

        outboxRepository.save(paymentRejectedOutboxMessage);
    }

    private void savePaymentCompletedOutboxMessage(UUID sagaId, PaymentEvent paymentEvent) {
        PaymentCompletedOutboxMessage paymentCompletedOutboxMessage =
                outboxMapper.paymentEventToPaymentCompletedOutboxMessage(paymentEvent, sagaId);

        outboxRepository.save(paymentCompletedOutboxMessage);
    }

    private Payment completePaymentCommandToPayment(MakePaymentCommand makePaymentCommand) {
        return Payment.builder()
                .orderId(new OrderId(makePaymentCommand.orderId()))
                .customerId(new CustomerId(makePaymentCommand.customerId()))
                .price(new Money(makePaymentCommand.price()))
                .build();
    }
}

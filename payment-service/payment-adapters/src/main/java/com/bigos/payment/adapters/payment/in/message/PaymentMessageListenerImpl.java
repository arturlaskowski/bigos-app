package com.bigos.payment.adapters.payment.in.message;

import com.bigos.common.domain.outbox.OutboxStatus;
import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.payment.adapters.payment.in.message.mapper.PaymentMessageMapper;
import com.bigos.payment.adapters.payment.outbox.dto.mapper.PaymentOutboxMapper;
import com.bigos.payment.domain.event.PaymentEvent;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.model.Wallet;
import com.bigos.payment.domain.ports.dto.outbox.PaymentCancelledOutboxMessage;
import com.bigos.payment.domain.ports.dto.outbox.PaymentCompletedOutboxMessage;
import com.bigos.payment.domain.ports.dto.outbox.PaymentOutboxMessage;
import com.bigos.payment.domain.ports.dto.outbox.PaymentRejectedOutboxMessage;
import com.bigos.payment.domain.ports.dto.payment.CancelPaymentCommand;
import com.bigos.payment.domain.ports.dto.payment.MakePaymentCommand;
import com.bigos.payment.domain.ports.in.message.PaymentMessageListener;
import com.bigos.payment.domain.ports.out.repository.PaymentOutboxRepository;
import com.bigos.payment.domain.ports.out.repository.PaymentRepository;
import com.bigos.payment.domain.ports.out.repository.WalletRepository;
import com.bigos.payment.domain.service.PaymentDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Slf4j
public class PaymentMessageListenerImpl implements PaymentMessageListener {

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final PaymentMessageMapper paymentMessageMapper;
    private final PaymentDomainService paymentDomainService;
    private final PaymentOutboxRepository outboxRepository;
    private final PaymentOutboxMapper outboxMapper;

    public PaymentMessageListenerImpl(PaymentRepository paymentRepository,
                                      WalletRepository walletRepository,
                                      PaymentMessageMapper paymentMessageMapper,
                                      PaymentDomainService paymentDomainService,
                                      PaymentOutboxRepository outboxRepository,
                                      PaymentOutboxMapper outboxMapper) {
        this.paymentRepository = paymentRepository;
        this.walletRepository = walletRepository;
        this.paymentMessageMapper = paymentMessageMapper;
        this.paymentDomainService = paymentDomainService;
        this.outboxRepository = outboxRepository;
        this.outboxMapper = outboxMapper;
    }


    @Override
    @Transactional
    public void makePayment(MakePaymentCommand makePaymentCommand) {
        UUID sagaId = UUID.fromString(makePaymentCommand.getSagaId());

        if (messageAlreadyProcessed(UUID.fromString(makePaymentCommand.getSagaId()), PaymentStatus.COMPLETED)) {
            log.info("Message with saga id: {} is already processed", makePaymentCommand.getSagaId());
            return;
        }

        Wallet wallet = walletRepository.getByCustomerId(new CustomerId((UUID.fromString(makePaymentCommand.getCustomerId()))));
        Payment payment = paymentMessageMapper.completePaymentCommandToPayment(makePaymentCommand);

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

    @Override
    @Transactional
    public void cancelPayment(CancelPaymentCommand cancelPaymentCommand) {
        UUID sagaId = UUID.fromString(cancelPaymentCommand.getSagaId());

        boolean messageProcessed = messageAlreadyProcessed(sagaId, PaymentStatus.CANCELLED);

        if (messageProcessed) {
            log.info("Message with saga id: {} is already processed", cancelPaymentCommand.getSagaId());
            return;
        }

        Wallet wallet = walletRepository.getByCustomerId(new CustomerId((UUID.fromString(cancelPaymentCommand.getCustomerId()))));
        Payment payment = paymentRepository.getByOrderId(new OrderId(UUID.fromString(cancelPaymentCommand.getOrderId())));

        PaymentEvent paymentEvent = paymentDomainService.cancelPayment(payment, wallet);

        paymentRepository.save(payment);

        if (payment.isCanceled()) {
            walletRepository.save(wallet);

            savePaymentCancelledOutboxMessage(sagaId, paymentEvent);

        } else if (payment.isRejected()) {
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
}

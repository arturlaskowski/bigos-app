package com.bigos.payment.adapters.payment.in.message;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.adapters.payment.mapper.PaymentMessageMapper;
import com.bigos.payment.domain.event.PaymentCancelledEvent;
import com.bigos.payment.domain.event.PaymentCompletedEvent;
import com.bigos.payment.domain.event.PaymentEvent;
import com.bigos.payment.domain.event.PaymentRejectedEvent;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.model.Wallet;
import com.bigos.payment.domain.ports.dto.CancelPaymentCommand;
import com.bigos.payment.domain.ports.dto.MakePaymentCommand;
import com.bigos.payment.domain.ports.in.message.PaymentMessageListener;
import com.bigos.payment.domain.ports.out.message.PaymentCancelledMessagePublisher;
import com.bigos.payment.domain.ports.out.message.PaymentCompletedMessagePublisher;
import com.bigos.payment.domain.ports.out.message.PaymentRejectedMessagePublisher;
import com.bigos.payment.domain.ports.out.repository.PaymentRepository;
import com.bigos.payment.domain.ports.out.repository.WalletRepository;
import com.bigos.payment.domain.service.PaymentDomainService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class PaymentMessageListenerImpl implements PaymentMessageListener {

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final PaymentMessageMapper paymentMessageMapper;
    private final PaymentDomainService paymentDomainService;
    private final PaymentCompletedMessagePublisher paymentCompletedMessagePublisher;
    private final PaymentRejectedMessagePublisher paymentRejectedMessagePublisher;
    private final PaymentCancelledMessagePublisher paymentCancelledMessagePublisher;

    public PaymentMessageListenerImpl(PaymentRepository paymentRepository,
                                      WalletRepository walletRepository,
                                      PaymentMessageMapper paymentMessageMapper,
                                      PaymentDomainService paymentDomainService,
                                      PaymentCompletedMessagePublisher paymentCompletedMessagePublisher,
                                      PaymentRejectedMessagePublisher paymentRejectedMessagePublisher,
                                      PaymentCancelledMessagePublisher paymentCancelledMessagePublisher) {
        this.paymentRepository = paymentRepository;
        this.walletRepository = walletRepository;
        this.paymentMessageMapper = paymentMessageMapper;
        this.paymentDomainService = paymentDomainService;
        this.paymentCompletedMessagePublisher = paymentCompletedMessagePublisher;
        this.paymentRejectedMessagePublisher = paymentRejectedMessagePublisher;
        this.paymentCancelledMessagePublisher = paymentCancelledMessagePublisher;
    }

    @Override
    @Transactional
    public void makePayment(MakePaymentCommand makePaymentCommand) {
        Wallet wallet = walletRepository.getByCustomerId(new CustomerId((UUID.fromString(makePaymentCommand.getCustomerId()))));
        Payment payment = paymentMessageMapper.completePaymentCommandToPayment(makePaymentCommand);

        PaymentEvent paymentEvent = paymentDomainService.makePayment(payment, wallet);

        paymentRepository.save(payment);

        if (payment.isCompleted()) {
            walletRepository.save(wallet);
            paymentCompletedMessagePublisher.publish((PaymentCompletedEvent) paymentEvent);

        } else if (payment.isRejected()) {
            paymentRejectedMessagePublisher.publish((PaymentRejectedEvent) paymentEvent);
        }
    }

    @Override
    @Transactional
    public void cancelPayment(CancelPaymentCommand cancelPaymentCommand) {
        Wallet wallet = walletRepository.getByCustomerId(new CustomerId((UUID.fromString(cancelPaymentCommand.getCustomerId()))));
        Payment payment = paymentRepository.getByOrderId(new OrderId(UUID.fromString(cancelPaymentCommand.getOrderId())));

        PaymentEvent paymentEvent = paymentDomainService.cancelPayment(payment, wallet);

        paymentRepository.save(payment);

        if (payment.isCanceled()) {
            walletRepository.save(wallet);
            paymentCancelledMessagePublisher.publish((PaymentCancelledEvent) paymentEvent);

        } else if (payment.isRejected()) {
            paymentRejectedMessagePublisher.publish((PaymentRejectedEvent) paymentEvent);
        }
    }
}

package com.bigos.payment.adapters.payment.in.message;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.adapters.payment.mapper.PaymentMessageMapper;
import com.bigos.payment.domain.service.PaymentDomainService;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.model.Wallet;
import com.bigos.payment.domain.ports.dto.CancelPaymentCommand;
import com.bigos.payment.domain.ports.dto.MakePaymentCommand;
import com.bigos.payment.domain.ports.in.message.PaymentMessageListener;
import com.bigos.payment.domain.ports.out.repository.PaymentRepository;
import com.bigos.payment.domain.ports.out.repository.WalletRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class PaymentMessageListenerImpl implements PaymentMessageListener {

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final PaymentMessageMapper paymentMessageMapper;
    private final PaymentDomainService paymentDomainService;

    public PaymentMessageListenerImpl(PaymentRepository paymentRepository, WalletRepository walletRepository, PaymentMessageMapper paymentMessageMapper, PaymentDomainService paymentDomainService) {
        this.paymentRepository = paymentRepository;
        this.walletRepository = walletRepository;
        this.paymentMessageMapper = paymentMessageMapper;
        this.paymentDomainService = paymentDomainService;
    }

    @Override
    @Transactional
    public void makePayment(MakePaymentCommand makePaymentCommand) {
        Wallet wallet = walletRepository.getByCustomerId(new CustomerId((UUID.fromString(makePaymentCommand.getCustomerId()))));
        Payment payment = paymentMessageMapper.completePaymentCommandToPayment(makePaymentCommand, wallet.getId());

        paymentDomainService.makePayment(payment, wallet);

        paymentRepository.save(payment);

        if (payment.isCompleted()) {
            walletRepository.save(wallet);
        }
    }

    @Override
    @Transactional
    public void cancelPayment(CancelPaymentCommand cancelPaymentCommand) {
        Wallet wallet = walletRepository.getByCustomerId(new CustomerId((UUID.fromString(cancelPaymentCommand.getCustomerId()))));
        Payment payment = paymentRepository.getByOrderId(new OrderId(UUID.fromString(cancelPaymentCommand.getOrderId())));

        paymentDomainService.cancelPayment(payment, wallet);

        paymentRepository.save(payment);

        if (payment.isCanceled()) {
            walletRepository.save(wallet);
        }
    }
}

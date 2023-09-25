package com.bigos.payment.application.payment;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.payment.application.payment.dto.CancelPaymentCommand;
import com.bigos.payment.application.payment.dto.MakePaymentCommand;
import com.bigos.payment.application.payment.exception.WalletNotFoundException;
import com.bigos.payment.application.payment.outbox.dto.PaymentOutboxMapper;
import com.bigos.payment.application.payment.outbox.dto.PaymentStatusEventPayload;
import com.bigos.payment.domain.PaymentDomainService;
import com.bigos.payment.domain.core.Wallet;
import com.bigos.payment.domain.core.WalletId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentApplicationServiceTest {

    private final InMemoryPaymentRepository paymentRepository = new InMemoryPaymentRepository();
    private final InMemoryWalletRepository walletRepository = new InMemoryWalletRepository();
    private final PaymentDomainService paymentDomainService = new PaymentDomainService();
    private final InMemoryPaymentOutboxRepository outboxRepository = new InMemoryPaymentOutboxRepository();
    private final PaymentOutboxMapper outboxMapper = new PaymentOutboxMapper();

    private final PaymentApplicationService paymentApplicationService = new PaymentApplicationService(
            paymentRepository, walletRepository, paymentDomainService, outboxRepository, outboxMapper);

    private static final OrderId ORDER_ID = new OrderId(UUID.fromString("b22bbb99-8ae3-4adf-9ff8-c95a51c158eb"));
    private static final CustomerId CUSTOMER_ID = new CustomerId(UUID.fromString("29812457-bc1b-4b27-9088-9a06de82762d"));
    private static final WalletId WALLET_ID = new WalletId(UUID.fromString("1cceb192-37c4-4e2f-a525-972d6680205e"));
    private static final Money PAYMENT_PRICE = new Money(new BigDecimal("58.43"));

    @AfterEach
    void clean() {
        paymentRepository.deleteAll();
        walletRepository.deleteAll();
        outboxRepository.deleteAll();
    }

    @Test
    void shouldMakePayment() {
        //given
        var walletAmountBeforePayment = new Money(new BigDecimal(1000));
        walletRepository.save(new Wallet(WALLET_ID, CUSTOMER_ID, walletAmountBeforePayment));
        var makePaymentCommand = aMakePaymentCommand();

        //when
        paymentApplicationService.makePayment(makePaymentCommand);

        //then --payment
        var payment = paymentRepository.getByOrderId(ORDER_ID);
        assertThat(payment)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("creationDate")
                .hasFieldOrPropertyWithValue("orderId.id", makePaymentCommand.orderId())
                .hasFieldOrPropertyWithValue("customerId.id", makePaymentCommand.customerId())
                .hasFieldOrPropertyWithValue("price.amount", makePaymentCommand.price())
                .hasFieldOrPropertyWithValue("status", PaymentStatus.COMPLETED);

        //then --wallet
        var wallet = walletRepository.getByCustomerId(CUSTOMER_ID);
        assertThat(wallet)
                .isNotNull()
                .extracting(Wallet::getAmount)
                .isEqualTo(walletAmountBeforePayment.subtract(PAYMENT_PRICE));

        //then --outbox
        var outboxMessage = outboxRepository.findLastByPaymentId(payment.getId());
        assertThat(outboxMessage)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.COMPLETED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdDate")
                .hasFieldOrPropertyWithValue("sendDate", null)
                .hasFieldOrPropertyWithValue("aggregateId", payment.getId().id())
                .hasFieldOrPropertyWithValue("aggregateName", payment.getClass().getSimpleName())
                .hasFieldOrProperty("sagaId")
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(message -> (PaymentStatusEventPayload) message.getPayload())
                .hasFieldOrPropertyWithValue("paymentId", payment.getId().id().toString())
                .hasFieldOrPropertyWithValue("createdDate", payment.getCreationDate())
                .hasFieldOrPropertyWithValue("orderId", payment.getOrderId().id().toString())
                .hasFieldOrPropertyWithValue("customerId", payment.getCustomerId().id().toString())
                .hasFieldOrPropertyWithValue("paymentStatus", payment.getStatus().toString())
                .hasFieldOrPropertyWithValue("price", payment.getPrice().amount());
    }

    @Test
    void shouldNotMakePaymentWhenWalletNotExists() {
        //given
        var makePaymentCommand = aMakePaymentCommand();

        //when
        var walletNotFoundException = assertThrows(WalletNotFoundException.class,
                () -> paymentApplicationService.makePayment(makePaymentCommand));

        //then
        assertEquals("Could not find wallet with customerId: " + CUSTOMER_ID.id(), walletNotFoundException.getMessage());
    }

    @Test
    void shouldRejectPaymentWhenWalletDoesNotHaveEnoughMoney() {
        //given
        var walletAmountBeforePayment = new Money(new BigDecimal(10));
        walletRepository.save(new Wallet(WALLET_ID, CUSTOMER_ID, walletAmountBeforePayment));
        var makePaymentCommand = aMakePaymentCommand();

        //when
        paymentApplicationService.makePayment(makePaymentCommand);

        //then --payment
        var payment = paymentRepository.getByOrderId(ORDER_ID);
        assertThat(payment)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("creationDate")
                .hasFieldOrPropertyWithValue("orderId.id", makePaymentCommand.orderId())
                .hasFieldOrPropertyWithValue("customerId.id", makePaymentCommand.customerId())
                .hasFieldOrPropertyWithValue("price.amount", makePaymentCommand.price())
                .hasFieldOrPropertyWithValue("status", PaymentStatus.REJECTED);

        //then --wallet
        var wallet = walletRepository.getByCustomerId(CUSTOMER_ID);
        assertThat(wallet)
                .isNotNull()
                .extracting(Wallet::getAmount)
                .isEqualTo(walletAmountBeforePayment);

        //then --outbox
        var outboxMessage = outboxRepository.findLastByPaymentId(payment.getId());
        assertThat(outboxMessage)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.REJECTED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdDate")
                .hasFieldOrPropertyWithValue("sendDate", null)
                .hasFieldOrPropertyWithValue("aggregateId", payment.getId().id())
                .hasFieldOrPropertyWithValue("aggregateName", payment.getClass().getSimpleName())
                .hasFieldOrProperty("sagaId")
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(message -> (PaymentStatusEventPayload) message.getPayload())
                .hasFieldOrPropertyWithValue("paymentId", payment.getId().id().toString())
                .hasFieldOrPropertyWithValue("createdDate", payment.getCreationDate())
                .hasFieldOrPropertyWithValue("orderId", payment.getOrderId().id().toString())
                .hasFieldOrPropertyWithValue("customerId", payment.getCustomerId().id().toString())
                .hasFieldOrPropertyWithValue("paymentStatus", payment.getStatus().toString())
                .hasFieldOrPropertyWithValue("price", payment.getPrice().amount());
    }

    @Test
    void shouldCancelPayment() {
        //given
        var walletAmountBeforePayment = new Money(new BigDecimal(1000));
        walletRepository.save(new Wallet(WALLET_ID, CUSTOMER_ID, walletAmountBeforePayment));
        var makePaymentCommand = aMakePaymentCommand();

        //when
        paymentApplicationService.makePayment(makePaymentCommand);

        //given
        var cancelPaymentCommand = aCancelPaymentCommand();

        //when
        paymentApplicationService.cancelPayment(cancelPaymentCommand);

        //then --payment
        var payment = paymentRepository.getByOrderId(ORDER_ID);
        assertThat(payment).isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("creationDate")
                .hasFieldOrPropertyWithValue("orderId.id", makePaymentCommand.orderId())
                .hasFieldOrPropertyWithValue("customerId.id", makePaymentCommand.customerId())
                .hasFieldOrPropertyWithValue("price.amount", makePaymentCommand.price())
                .hasFieldOrPropertyWithValue("status", PaymentStatus.CANCELLED);

        //then --wallet
        var wallet = walletRepository.getByCustomerId(CUSTOMER_ID);
        assertThat(wallet)
                .isNotNull()
                .extracting(Wallet::getAmount)
                .isEqualTo(walletAmountBeforePayment);
        var all = outboxRepository.getAll();
        //then --outbox
        var outboxMessage = outboxRepository.findLastByPaymentId(payment.getId());
        assertThat(outboxMessage)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.CANCELLED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdDate")
                .hasFieldOrPropertyWithValue("sendDate", null)
                .hasFieldOrPropertyWithValue("aggregateId", payment.getId().id())
                .hasFieldOrPropertyWithValue("aggregateName", payment.getClass().getSimpleName())
                .hasFieldOrProperty("sagaId")
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(message -> (PaymentStatusEventPayload) message.getPayload())
                .hasFieldOrPropertyWithValue("paymentId", payment.getId().id().toString())
                .hasFieldOrPropertyWithValue("createdDate", payment.getCreationDate())
                .hasFieldOrPropertyWithValue("orderId", payment.getOrderId().id().toString())
                .hasFieldOrPropertyWithValue("customerId", payment.getCustomerId().id().toString())
                .hasFieldOrPropertyWithValue("paymentStatus", payment.getStatus().toString())
                .hasFieldOrPropertyWithValue("price", payment.getPrice().amount());
    }

    private MakePaymentCommand aMakePaymentCommand() {
        return new MakePaymentCommand(UUID.randomUUID(), UUID.randomUUID(), ORDER_ID.id(), CUSTOMER_ID.id(), PAYMENT_PRICE.amount());
    }

    private CancelPaymentCommand aCancelPaymentCommand() {
        return new CancelPaymentCommand(UUID.randomUUID(), UUID.randomUUID(), ORDER_ID.id(), CUSTOMER_ID.id(), PAYMENT_PRICE.amount());
    }
}
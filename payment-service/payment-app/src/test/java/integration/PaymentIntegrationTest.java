package integration;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.app.PaymentServiceApplication;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.model.Wallet;
import com.bigos.payment.domain.ports.dto.payment.CancelPaymentCommand;
import com.bigos.payment.domain.ports.dto.payment.MakePaymentCommand;
import com.bigos.payment.domain.ports.in.message.PaymentMessageListener;
import com.bigos.payment.domain.ports.out.repository.PaymentRepository;
import com.bigos.payment.domain.ports.out.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(classes = PaymentServiceApplication.class)
@Sql(value = {"classpath:sql/PaymentIntegrationTestSetUp.sql"})
@Sql(value = {"classpath:sql/PaymentIntegrationTestCleanUp.sql"}, executionPhase = AFTER_TEST_METHOD)
class PaymentIntegrationTest {

    public static final UUID ORDER_UUID = UUID.fromString("56fa002a-0850-47b9-a7f8-d000095d1fe3");
    public static final UUID CUSTOMER_UUID = UUID.fromString("ddc3b837-3db2-4e9c-9ee4-a9ca172b6e10");
    public static final BigDecimal PAYMENT_PRICE = new BigDecimal("55.43");

    @Autowired
    private PaymentMessageListener paymentMessageListener;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void canCompletePayment() {
        //given
        MakePaymentCommand makePaymentCommand = aMakePaymentCommand();
        //when
        paymentMessageListener.makePayment(makePaymentCommand);
        //then
        Payment payment = paymentRepository.getByOrderId(new OrderId(ORDER_UUID));
        Wallet wallet = walletRepository.getByCustomerId(new CustomerId(CUSTOMER_UUID));

        assertNotNull(payment.getCreationDate());
        assertTrue(payment.isCompleted());
        assertEquals(ORDER_UUID, payment.getOrderId().id());
        assertEquals(wallet.getCustomerId(), payment.getCustomerId());
        assertEquals(new Money(PAYMENT_PRICE), payment.getPrice());
        assertEquals(new Money(new BigDecimal("250.00")).subtract(new Money(PAYMENT_PRICE)),
                wallet.getAmount()); //before wallet has 250.00 amount - PaymentIntegrationTestSetUp.sql
    }

    @Test
    void canCancelPayment() {
        //given
        MakePaymentCommand makePaymentCommand = aMakePaymentCommand();
        //when
        paymentMessageListener.makePayment(makePaymentCommand);
        //and
        //given
        CancelPaymentCommand cancelPaymentCommand = aCancelPaymentCommand();
        //when
        paymentMessageListener.cancelPayment(cancelPaymentCommand);
        //then
        Payment payment = paymentRepository.getByOrderId(new OrderId(ORDER_UUID));
        Wallet wallet = walletRepository.getByCustomerId(new CustomerId(CUSTOMER_UUID));

        assertNotNull(payment.getCreationDate());
        assertTrue(payment.isCanceled());
        assertEquals(ORDER_UUID, payment.getOrderId().id());
        assertEquals(wallet.getCustomerId(), payment.getCustomerId());
        assertEquals(new Money(PAYMENT_PRICE), payment.getPrice());
        assertEquals(new Money(new BigDecimal("250.00")),
                wallet.getAmount()); //before make payment wallet has 250.00 amount - PaymentIntegrationTestSetUp.sql
    }

    private static MakePaymentCommand aMakePaymentCommand() {
        return new MakePaymentCommand(UUID.randomUUID().toString(), UUID.randomUUID().toString(), ORDER_UUID.toString(), CUSTOMER_UUID.toString(), PAYMENT_PRICE);
    }

    private static CancelPaymentCommand aCancelPaymentCommand() {
        return new CancelPaymentCommand(UUID.randomUUID().toString(), UUID.randomUUID().toString(), ORDER_UUID.toString(), CUSTOMER_UUID.toString(), PAYMENT_PRICE);
    }
}

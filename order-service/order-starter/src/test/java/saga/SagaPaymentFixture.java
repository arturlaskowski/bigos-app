package saga;

import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.order.domain.event.PaymentStatusEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

class SagaPaymentFixture {

    final static UUID SAGA_ID = UUID.fromString("9be90e1a-f43c-4ecc-a68f-7d2987478492");
    final static UUID ORDER_ID = UUID.fromString("8942f16e-1137-401b-9055-3701c0252877");
    final static UUID CUSTOMER_ID = UUID.fromString("5ca03955-f73a-4e74-abcc-35adf4d68fda");

    static PaymentStatusEvent aCompletedPaymentStatusEvent() {
        return aPaymentStatusEvent(PaymentStatus.COMPLETED);
    }

    private static PaymentStatusEvent aPaymentStatusEvent(PaymentStatus status) {
        return PaymentStatusEvent.builder()
                .id(UUID.randomUUID().toString())
                .paymentId(UUID.randomUUID().toString())
                .sagaId(SAGA_ID.toString())
                .orderId(ORDER_ID.toString())
                .customerId(CUSTOMER_ID.toString())
                .paymentStatus(status)
                .price(new BigDecimal("100.00"))
                .createdAt(Instant.now())
                .build();
    }
}

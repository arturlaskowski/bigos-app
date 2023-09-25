package saga;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.applciaiton.saga.SagaStatus;
import com.bigos.order.application.outbox.dto.OrderCreatedOutboxMessage;
import com.bigos.order.application.outbox.dto.OrderPaidOutboxMessage;
import com.bigos.order.application.outbox.repository.OrderOutboxRepository;
import com.bigos.order.application.saga.OrderPaymentSaga;
import com.bigos.order.starter.OrderServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static saga.SagaPaymentFixture.*;

@SpringBootTest(classes = OrderServiceApplication.class)
@Sql(value = {"classpath:sql/OrderPaymentSagaTestSetUp.sql"})
@Sql(value = {"classpath:sql/OrderPaymentSagaTestCleanUp.sql"}, executionPhase = AFTER_TEST_METHOD)
class PaymentSagaTest {

    @Autowired
    private OrderPaymentSaga orderPaymentSaga;

    @Autowired
    private OrderOutboxRepository outboxRepository;

    @Test
    void canProcessWhenCompletedPayment() {
        //when
        orderPaymentSaga.process(aCompletedPaymentStatusEvent());

        OrderCreatedOutboxMessage orderCreatedOutboxMessage = outboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(OrderCreatedOutboxMessage.class, SAGA_ID, SagaStatus.PROCESSING).get();
        OrderPaidOutboxMessage orderPaidOutboxMessage = outboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(OrderPaidOutboxMessage.class, SAGA_ID, SagaStatus.PROCESSING).get();
        //then
        assertEquals("Order", orderCreatedOutboxMessage.getAggregateName());
        assertEquals(ORDER_ID, orderCreatedOutboxMessage.getAggregateId());
        assertEquals(OutboxStatus.STARTED, orderCreatedOutboxMessage.getOutboxStatus());
        assertNotNull(orderCreatedOutboxMessage.getCreatedDate());
        assertNotNull(orderCreatedOutboxMessage.getPayload());
        assertNotNull(orderCreatedOutboxMessage.getProcessedDate());

        assertEquals("Order", orderPaidOutboxMessage.getAggregateName());
        assertEquals(ORDER_ID, orderPaidOutboxMessage.getAggregateId());
        assertEquals(OutboxStatus.STARTED, orderPaidOutboxMessage.getOutboxStatus());
        assertNotNull(orderPaidOutboxMessage.getCreatedDate());
        assertNotNull(orderPaidOutboxMessage.getPayload());
        assertNull(orderPaidOutboxMessage.getProcessedDate());
    }

    //todo the same for different case
}

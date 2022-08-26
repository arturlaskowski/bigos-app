package saga.payment;

import com.bigos.common.domain.saga.SagaStatus;
import com.bigos.order.adapters.saga.OrderPaymentSaga;
import com.bigos.order.app.OrderServiceApplication;
import com.bigos.order.domain.ports.dto.outbox.OrderOutboxMessage;
import com.bigos.order.domain.ports.out.repository.OrderOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static saga.payment.SagaPaymentFixture.SAGA_ID;
import static saga.payment.SagaPaymentFixture.aCompletedPaymentStatusEvent;

@SpringBootTest(classes = OrderServiceApplication.class)
@Slf4j
@Sql(value = {"classpath:sql/OrderPaymentSagaTestSetUp.sql"})
@Sql(value = {"classpath:sql/OrderPaymentSagaTestCleanUp.sql"}, executionPhase = AFTER_TEST_METHOD)
class ProcessingTheSamePaymentStatusEventSagaTest {

    @Autowired
    private OrderPaymentSaga orderPaymentSaga;

    @Autowired
    private OrderOutboxRepository outboxRepository;

    @Test
    void shouldIgnoreProcessingTheSameCompletedPaymentStatusEventAgain() {
        //expect
        List<OrderOutboxMessage> beforeProcessPaymentEvent = outboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.STARTED);
        assertEquals(1, beforeProcessPaymentEvent.size());

        //when
        orderPaymentSaga.process(aCompletedPaymentStatusEvent());

        //then
        List<OrderOutboxMessage> afterProcessPaymentEvent = outboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.PROCESSING);
        assertEquals(2, afterProcessPaymentEvent.size());

        //when
        orderPaymentSaga.process(aCompletedPaymentStatusEvent());

        //then
        List<OrderOutboxMessage> afterRepeatingProcessTheSamePaymentEvent = outboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.PROCESSING);
        assertEquals(2, afterRepeatingProcessTheSamePaymentEvent.size());
    }

    @Test
    void shouldProcessOnlyOneTimeCompletedPaymentStatusEventWhenIsProcessingInFewThreads() throws InterruptedException {
        //expect
        List<OrderOutboxMessage> beforeProcessPaymentEvent = outboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.STARTED);
        assertEquals(1, beforeProcessPaymentEvent.size());

        //when
        Thread thread1 = new Thread(() -> orderPaymentSaga.process(aCompletedPaymentStatusEvent()));
        Thread thread2 = new Thread(() -> orderPaymentSaga.process(aCompletedPaymentStatusEvent()));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        //then
        List<OrderOutboxMessage> afterRepeatingProcessTheSamePaymentEvent = outboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.PROCESSING);
        assertEquals(2, afterRepeatingProcessTheSamePaymentEvent.size());
    }

    @Test
    void shouldProcessOnlyOneTimeCompletedPaymentStatusEventWhenIsProcessingInFewThreadsWithLatch() throws InterruptedException {
        //expect
        List<OrderOutboxMessage> beforeProcessPaymentEvent = outboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.STARTED);
        assertEquals(1, beforeProcessPaymentEvent.size());

        //when
        CountDownLatch latch = new CountDownLatch(2);

        Thread thread1 = new Thread(() -> {
            try {
                orderPaymentSaga.process(aCompletedPaymentStatusEvent());
            } catch (OptimisticLockingFailureException e) {
                log.error("OptimisticLockingFailureException from thread1");
            } finally {
                latch.countDown();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                orderPaymentSaga.process(aCompletedPaymentStatusEvent());
            } catch (OptimisticLockingFailureException e) {
                log.error("OptimisticLockingFailureException from thread2");
            } finally {
                latch.countDown();
            }
        });

        thread1.start();
        thread2.start();

        latch.await();

        //then
        List<OrderOutboxMessage> afterRepeatingProcessTheSamePaymentEvent = outboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.PROCESSING);
        assertEquals(2, afterRepeatingProcessTheSamePaymentEvent.size());
    }

    //todo write the same tests for different events type

}

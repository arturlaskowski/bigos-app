package integration;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.restaurant.adapters.restaurant.exception.RestaurantNotFoundException;
import com.bigos.restaurant.app.RestaurantServiceApplication;
import com.bigos.restaurant.domain.model.OrderProcessed;
import com.bigos.restaurant.domain.model.vo.OrderApprovalStatus;
import com.bigos.restaurant.domain.ports.dto.OrderItemDto;
import com.bigos.restaurant.domain.ports.dto.OrderPaidEvent;
import com.bigos.restaurant.domain.ports.in.message.OrderApprovalListener;
import com.bigos.restaurant.domain.ports.out.repository.OrderProcessedRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(classes = RestaurantServiceApplication.class)
@Sql(value = {"classpath:sql/RestaurantIntegrationTestSetUp.sql"})
@Sql(value = {"classpath:sql/RestaurantIntegrationTestCleanUp.sql"}, executionPhase = AFTER_TEST_METHOD)
class RestaurantIntegrationTest {

    private static final String ORDER_ID = "88144676-05e8-4498-8c31-3110e3376761";
    private static final String RESTAURANT_AVAILABLE_ID = "83525182-c527-4767-a332-30139f6c38e1";
    private static final String RESTAURANT_UNAVAILABLE_ID = "f16b42f7-6913-4a3f-9c71-fbf724e440b8";

    @Autowired
    private OrderApprovalListener orderApprovalListener;

    @Autowired
    private OrderProcessedRepository orderProcessedRepository;

    @Test
    void shouldApproveOrder() {
        //given
        OrderPaidEvent orderPaidEvent = aOrderPaidEvent();
        //when
        orderApprovalListener.approveOrder(orderPaidEvent);
        //then
        OrderProcessed orderProcessed = orderProcessedRepository.findById(new OrderId(UUID.fromString(ORDER_ID))).get();
        assertEquals(UUID.fromString(RESTAURANT_AVAILABLE_ID), orderProcessed.getRestaurantId().id());
        assertEquals(new BigDecimal("250.00"), orderProcessed.getPrice().amount());
        assertEquals(OrderApprovalStatus.APPROVED, orderProcessed.getApprovalStatus());
    }

    @Test
    void cannotApproveOrderWhenRestaurantNotExists() {
        //given
        OrderPaidEvent orderPaidEvent = aOrderPaidEventWithNotExistingRestaurant();
        //expected
        assertThatExceptionOfType(RestaurantNotFoundException.class)
                .isThrownBy(() -> orderApprovalListener.approveOrder(orderPaidEvent));
    }

    @Test
    void shouldRejectedOrderWhenRestaurantIsNotAvailable() {
        //given
        OrderPaidEvent orderPaidEvent = aOrderPaidEventWithUnavailableRestaurant();
        //when
        orderApprovalListener.approveOrder(orderPaidEvent);
        //then
        OrderProcessed orderProcessed = orderProcessedRepository.findById(new OrderId(UUID.fromString(ORDER_ID))).get();
        assertEquals(UUID.fromString(RESTAURANT_UNAVAILABLE_ID), orderProcessed.getRestaurantId().id());
        assertEquals(new BigDecimal("250.00"), orderProcessed.getPrice().amount());
        assertEquals(OrderApprovalStatus.REJECTED, orderProcessed.getApprovalStatus());
    }

    @Test
    void shouldRejectedOrderWhenOrderPriceIsDifferentThanOrderItemsTotal() {
        //given
        OrderPaidEvent orderPaidEvent = aOrderPaidEventWithWithWrongAmount();
        //when
        orderApprovalListener.approveOrder(orderPaidEvent);
        //then
        OrderProcessed orderProcessed = orderProcessedRepository.findById(new OrderId(UUID.fromString(ORDER_ID))).get();
        assertEquals(new BigDecimal("240.00"), orderProcessed.getPrice().amount());
        assertEquals(OrderApprovalStatus.REJECTED, orderProcessed.getApprovalStatus());
    }

    private static OrderPaidEvent aOrderPaidEvent() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(UUID.randomUUID().toString(), new BigDecimal("50.00"), 5));
        return new OrderPaidEvent(ORDER_ID, RESTAURANT_AVAILABLE_ID, new BigDecimal("250.00"), orderItems, "PAID");
    }

    private static OrderPaidEvent aOrderPaidEventWithNotExistingRestaurant() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(UUID.randomUUID().toString(), new BigDecimal("50.00"), 5));
        return new OrderPaidEvent(ORDER_ID, UUID.randomUUID().toString(), new BigDecimal("250.00"), orderItems, "PAID");
    }

    private static OrderPaidEvent aOrderPaidEventWithUnavailableRestaurant() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(UUID.randomUUID().toString(), new BigDecimal("50.00"), 5));
        return new OrderPaidEvent(ORDER_ID, RESTAURANT_UNAVAILABLE_ID, new BigDecimal("250.00"), orderItems, "PAID");
    }

    private static OrderPaidEvent aOrderPaidEventWithWithWrongAmount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(UUID.randomUUID().toString(), new BigDecimal("50.00"), 5));
        return new OrderPaidEvent(ORDER_ID, RESTAURANT_AVAILABLE_ID, new BigDecimal("240.00"), orderItems, "PAID");
    }
}
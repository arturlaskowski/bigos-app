package com.bigos.restaurant.application;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.domain.vo.OrderApprovalStatus;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedEventPayload;
import com.bigos.restaurant.application.orderprocessed.outbox.dto.OrderProcessedOutboxMapper;
import com.bigos.restaurant.domain.RestaurantDomainService;
import com.bigos.restaurant.domain.orderprocessed.core.OrderProcessed;
import com.bigos.restaurant.domain.orderprocessed.event.OrderItemDto;
import com.bigos.restaurant.domain.orderprocessed.event.OrderPaidEvent;
import com.bigos.restaurant.domain.restaurant.core.Restaurant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantApplicationServiceTest {

    private final RestaurantDomainService restaurantDomainService = new RestaurantDomainService();
    private final InMemoryRestaurantRepository restaurantRepository = new InMemoryRestaurantRepository();
    private final InMemoryOrderProcessedRepository orderProcessedRepository = new InMemoryOrderProcessedRepository();
    private final OrderProcessedMapper orderProcessedMapper = new OrderProcessedMapper();
    private final InMemoryOrderProcessedOutboxRepository outboxRepository = new InMemoryOrderProcessedOutboxRepository();
    private final OrderProcessedOutboxMapper outboxMapper = new OrderProcessedOutboxMapper();

    private final RestaurantApplicationService restaurantApplicationService = new RestaurantApplicationService(
            restaurantDomainService, restaurantRepository, orderProcessedRepository, orderProcessedMapper, outboxRepository, outboxMapper);


    private static final OrderId ORDER_ID = new OrderId(UUID.fromString("88144676-05e8-4498-8c31-3110e3376761"));
    private static final RestaurantId RESTAURANT_ID = new RestaurantId(UUID.fromString("83525182-c527-4767-a332-30139f6c38e1"));

    @AfterEach
    void clean() {
        restaurantRepository.deleteAll();
        orderProcessedRepository.deleteAll();
        outboxRepository.deleteAll();
    }

    @Test
    void shouldAcceptOrder() {
        //given
        restaurantRepository.addRestaurant(new Restaurant(RESTAURANT_ID, true, "MniamMniam"));
        var orderPaidEvent = aOrderPaidEvent();

        //when
        restaurantApplicationService.acceptOrder(orderPaidEvent);

        //then --orderprocessed
        var orderProcessedOptional = orderProcessedRepository.findById(ORDER_ID);
        assertThat(orderProcessedOptional)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("id.id", UUID.fromString(orderPaidEvent.orderId()))
                .hasFieldOrPropertyWithValue("restaurantId.id", UUID.fromString(orderPaidEvent.restaurantId()))
                .hasFieldOrPropertyWithValue("price.amount", orderPaidEvent.price())
                .hasFieldOrPropertyWithValue("approvalStatus", OrderApprovalStatus.ACCEPTED)
                .extracting(OrderProcessed::getItems)
                .satisfies(basketItems ->
                        assertThat(basketItems.get(0))
                                .hasFieldOrProperty("id.id")
                                .hasFieldOrPropertyWithValue("productId.id", UUID.fromString(orderPaidEvent.orderItems().get(0).productId()))
                                .hasFieldOrPropertyWithValue("price.amount", orderPaidEvent.orderItems().get(0).price())
                                .hasFieldOrPropertyWithValue("quantity.numberOfElements", orderPaidEvent.orderItems().get(0).quantity())
                );

        //then --outbox
        var outboxMessage = outboxRepository.findByOrderProcessedId(ORDER_ID);
        assertThat(outboxMessage)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("approvalStatus", OrderApprovalStatus.ACCEPTED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdDate")
                .hasFieldOrPropertyWithValue("sendDate", null)
                .hasFieldOrPropertyWithValue("aggregateId", orderProcessedOptional.get().getId().id())
                .hasFieldOrPropertyWithValue("aggregateName", orderProcessedOptional.get().getClass().getSimpleName())
                .hasFieldOrProperty("sagaId")
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(message -> (OrderProcessedEventPayload) message.getPayload())
                .hasFieldOrPropertyWithValue("orderId", orderProcessedOptional.get().getId().id().toString())
                .hasFieldOrPropertyWithValue("restaurantId", orderProcessedOptional.get().getRestaurantId().id().toString())
                .hasFieldOrPropertyWithValue("orderApprovalStatus", orderProcessedOptional.get().getApprovalStatus().name())
                .hasFieldOrPropertyWithValue("failureMessages", null);
    }

    @Test
    void shouldRejectedOrderWhenRestaurantIsNotAvailable() {
        //given
        restaurantRepository.addRestaurant(new Restaurant(RESTAURANT_ID, false, "MniamMniam"));
        var orderPaidEvent = aOrderPaidEvent();

        //when
        restaurantApplicationService.acceptOrder(orderPaidEvent);

        //then --orderprocessed
        var orderProcessedOptional = orderProcessedRepository.findById(ORDER_ID);
        assertThat(orderProcessedOptional)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("id.id", UUID.fromString(orderPaidEvent.orderId()))
                .hasFieldOrPropertyWithValue("restaurantId.id", UUID.fromString(orderPaidEvent.restaurantId()))
                .hasFieldOrPropertyWithValue("price.amount", orderPaidEvent.price())
                .hasFieldOrPropertyWithValue("approvalStatus", OrderApprovalStatus.REJECTED)
                .extracting(OrderProcessed::getItems)
                .satisfies(basketItems ->
                        assertThat(basketItems.get(0))
                                .hasFieldOrProperty("id.id")
                                .hasFieldOrPropertyWithValue("productId.id", UUID.fromString(orderPaidEvent.orderItems().get(0).productId()))
                                .hasFieldOrPropertyWithValue("price.amount", orderPaidEvent.orderItems().get(0).price())
                                .hasFieldOrPropertyWithValue("quantity.numberOfElements", orderPaidEvent.orderItems().get(0).quantity())
                );

        //then --outbox
        var outboxMessage = outboxRepository.findByOrderProcessedId(ORDER_ID);
        assertThat(outboxMessage)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("approvalStatus", OrderApprovalStatus.REJECTED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdDate")
                .hasFieldOrPropertyWithValue("sendDate", null)
                .hasFieldOrPropertyWithValue("aggregateId", orderProcessedOptional.get().getId().id())
                .hasFieldOrPropertyWithValue("aggregateName", orderProcessedOptional.get().getClass().getSimpleName())
                .hasFieldOrProperty("sagaId")
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(message -> (OrderProcessedEventPayload) message.getPayload())
                .hasFieldOrPropertyWithValue("orderId", orderProcessedOptional.get().getId().id().toString())
                .hasFieldOrPropertyWithValue("restaurantId", orderProcessedOptional.get().getRestaurantId().id().toString())
                .hasFieldOrPropertyWithValue("orderApprovalStatus", orderProcessedOptional.get().getApprovalStatus().name())
                .hasFieldOrPropertyWithValue("failureMessages", "Restaurant is unavailable. RestaurantId[id=" + RESTAURANT_ID.id() + "]");
    }

    private OrderPaidEvent aOrderPaidEvent() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(UUID.randomUUID().toString(), new BigDecimal("50.00"), 5));
        return OrderPaidEvent.builder()
                .orderId(ORDER_ID.id().toString())
                .restaurantId(RESTAURANT_ID.id().toString())
                .sageId(UUID.randomUUID().toString())
                .price(new BigDecimal("250.00"))
                .orderItems(orderItems)
                .status("PAID")
                .build();
    }
}

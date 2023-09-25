package com.bigos.order.application;

import com.bigos.common.applciaiton.outbox.model.OutboxStatus;
import com.bigos.common.applciaiton.saga.SagaStatus;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.OrderStatus;
import com.bigos.order.application.command.OrderCommandMapper;
import com.bigos.order.application.command.OrderCreateCommandHandler;
import com.bigos.order.application.command.dto.BasketItemDto;
import com.bigos.order.application.command.dto.CreateOrderCommand;
import com.bigos.order.application.command.dto.OrderAddressDto;
import com.bigos.order.application.outbox.dto.OrderEventPayload;
import com.bigos.order.application.outbox.dto.mapper.OrderOutboxMapper;
import com.bigos.order.application.saga.SagaStatusMapper;
import com.bigos.order.domain.OrderDomainService;
import com.bigos.order.domain.core.Order;
import com.bigos.order.domain.exception.OrderDomainException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderCreateCommandHandlerTest {

    private final OrderCommandMapper orderCommandMapper = new OrderCommandMapper();
    private final OrderDomainService orderDomainService = new OrderDomainService();
    private final InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
    private final SagaStatusMapper sagaStatusMapper = new SagaStatusMapper();
    private final OrderOutboxMapper orderOutboxMapper = new OrderOutboxMapper(sagaStatusMapper);
    private final InMemoryOrderOutboxRepository orderOutboxRepository = new InMemoryOrderOutboxRepository();
    private final OrderCreateCommandHandler orderCreateCommandHandler =
            new OrderCreateCommandHandler(orderCommandMapper, orderDomainService, orderRepository,
                    orderOutboxMapper, orderOutboxRepository);

    private static final UUID CUSTOMER_UUID = UUID.fromString("4ece4645-2658-4c54-a182-b0edcfa46d3b");
    private static final UUID RESTAURANT_UUID = UUID.fromString("0d94b4a5-a33a-42f5-995f-081c193441ee");
    private static final UUID PRODUCT_1_UUID = UUID.fromString("1ecf862b-6395-412c-ba56-59d38e2764a3");
    private static final UUID PRODUCT_2_UUID = UUID.fromString("9ce63b2e-50bd-4f32-a967-4cc17bd32225");

    @AfterEach
    void clean() {
        orderRepository.deleteAll();
        orderOutboxRepository.deleteAll();
    }

    @Test
    void cannotCreateOrderWithWrongPrice() {
        //given
        var createOrderCommandWithWrongPrice = aOrderCommandWithWrongPrice();

        //when
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderCreateCommandHandler.createOrder(createOrderCommandWithWrongPrice));

        //then
        assertEquals("Incorrect basket item price: 30.00", orderDomainException.getMessage());
    }

    @Test
    void cannotCreateOrderWithWrongBasketPrice() {
        //given
        var createOrderCommandWithBasketPrice = aOrderCommandWithWrongBasketPrice();

        //when
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderCreateCommandHandler.createOrder(createOrderCommandWithBasketPrice));

        //then
        assertEquals("Incorrect basket item price: 10.00", orderDomainException.getMessage());
    }

    @Test
    void canCreateOrder() {
        //given
        CreateOrderCommand createOrderCommand = aOrderCommand();

        //when
        OrderId orderId = orderCreateCommandHandler.createOrder(createOrderCommand);

        //then
        Order order = orderRepository.getOrder(orderId);
        assertThat(order)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id.id", orderId.id())
                .hasFieldOrPropertyWithValue("customerId.id", createOrderCommand.customerId())
                .hasFieldOrPropertyWithValue("restaurantId.id", createOrderCommand.restaurantId())
                .hasFieldOrPropertyWithValue("price.amount", createOrderCommand.price())
                .hasFieldOrPropertyWithValue("status", OrderStatus.PENDING)
                .extracting(Order::getBasket)
                .satisfies(basketItems -> {
                    assertThat(basketItems.get(0).getItemNumber()).isEqualTo(1);
                    assertThat(basketItems.get(0).getProduct().getId().id()).isEqualTo(createOrderCommand.items().get(0).productId());
                    assertThat(basketItems.get(0).getProduct().getPrice().amount()).isEqualTo(createOrderCommand.items().get(0).price());
                    assertThat(basketItems.get(0).getQuantity().numberOfElements()).isEqualTo(createOrderCommand.items().get(0).quantity());
                    assertThat(basketItems.get(0).getTotalPrice().amount()).isEqualTo(createOrderCommand.items().get(0).totalPrice());

                    assertThat(basketItems.get(1).getItemNumber()).isEqualTo(2);
                    assertThat(basketItems.get(1).getProduct().getId().id()).isEqualTo(createOrderCommand.items().get(1).productId());
                    assertThat(basketItems.get(1).getProduct().getPrice().amount()).isEqualTo(createOrderCommand.items().get(1).price());
                    assertThat(basketItems.get(1).getQuantity().numberOfElements()).isEqualTo(createOrderCommand.items().get(1).quantity());
                    assertThat(basketItems.get(1).getTotalPrice().amount()).isEqualTo(createOrderCommand.items().get(1).totalPrice());
                });
        assertThat(order)
                .extracting(Order::getDeliveryAddress)
                .hasFieldOrPropertyWithValue("street", createOrderCommand.address().street())
                .hasFieldOrPropertyWithValue("city", createOrderCommand.address().city())
                .hasFieldOrPropertyWithValue("postCode", createOrderCommand.address().postalCode())
                .hasFieldOrPropertyWithValue("houseNo", createOrderCommand.address().houseNo());
    }

    @Test
    void shouldCreateOrderOutboxDuringCreateOrder() {
        //given
        CreateOrderCommand createOrderCommand = aOrderCommand();

        //when
        OrderId orderId = orderCreateCommandHandler.createOrder(createOrderCommand);
        var outboxMessageOptional = orderOutboxRepository.findByOrderId(orderId);
        assertThat(outboxMessageOptional).isPresent();
        var outboxMessage = outboxMessageOptional.get();
        assertThat(outboxMessage)
                .hasFieldOrPropertyWithValue("sagaStatus", SagaStatus.STARTED)
                .hasFieldOrPropertyWithValue("processedDate", null)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdDate")
                .hasFieldOrPropertyWithValue("sendDate", null)
                .hasFieldOrPropertyWithValue("aggregateId", orderId.id())
                .hasFieldOrPropertyWithValue("aggregateName", "Order")
                .hasFieldOrProperty("sagaId")
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(message -> (OrderEventPayload) message.getPayload())
                .hasFieldOrPropertyWithValue("orderId", orderId.id().toString())
                .hasFieldOrPropertyWithValue("customerId", CUSTOMER_UUID.toString())
                .hasFieldOrPropertyWithValue("restaurantId", RESTAURANT_UUID.toString())
                .hasFieldOrPropertyWithValue("failureMessages", null)
                .hasFieldOrProperty("creationDate")
                .hasFieldOrPropertyWithValue("price", createOrderCommand.price())
                .hasFieldOrPropertyWithValue("status", "PENDING")
                .extracting(OrderEventPayload::basket)
                .satisfies(basketItems -> {
                    assertThat(basketItems.get(0))
                            .hasFieldOrPropertyWithValue("itemNumber", 1)
                            .hasFieldOrPropertyWithValue("productId", createOrderCommand.items().get(0).productId().toString())
                            .hasFieldOrPropertyWithValue("price", createOrderCommand.items().get(0).price())
                            .hasFieldOrPropertyWithValue("quantity", createOrderCommand.items().get(0).quantity())
                            .hasFieldOrPropertyWithValue("totalPrice", createOrderCommand.items().get(0).totalPrice());

                    assertThat(basketItems.get(1))
                            .hasFieldOrPropertyWithValue("itemNumber", 2)
                            .hasFieldOrPropertyWithValue("productId", createOrderCommand.items().get(1).productId().toString())
                            .hasFieldOrPropertyWithValue("price", createOrderCommand.items().get(1).price())
                            .hasFieldOrPropertyWithValue("quantity", createOrderCommand.items().get(1).quantity())
                            .hasFieldOrPropertyWithValue("totalPrice", createOrderCommand.items().get(1).totalPrice());
                });
        assertThat(outboxMessage)
                .extracting(message -> (OrderEventPayload) message.getPayload())
                .extracting(OrderEventPayload::deliveryAddress)
                .hasFieldOrProperty("orderAddressId")
                .hasFieldOrPropertyWithValue("street", createOrderCommand.address().street())
                .hasFieldOrPropertyWithValue("postCode", createOrderCommand.address().postalCode())
                .hasFieldOrPropertyWithValue("city", createOrderCommand.address().city())
                .hasFieldOrPropertyWithValue("houseNo", createOrderCommand.address().houseNo());
    }

    private CreateOrderCommand aOrderCommand() {
        List<BasketItemDto> basketItemsDto = List.of(new BasketItemDto(PRODUCT_1_UUID, 2, new BigDecimal("10.00"), new BigDecimal("20.00")),
                new BasketItemDto(PRODUCT_2_UUID, 3, new BigDecimal("15.50"), new BigDecimal("46.50")));

        OrderAddressDto orderAddressDto = new OrderAddressDto("Prosta", "999-333", "Krzewie", "4");

        return new CreateOrderCommand(CUSTOMER_UUID, RESTAURANT_UUID, new BigDecimal("66.50"), basketItemsDto, orderAddressDto);
    }

    private CreateOrderCommand aOrderCommandWithWrongPrice() {
        List<BasketItemDto> basketItemsDto = List.of(new BasketItemDto(PRODUCT_1_UUID, 2, new BigDecimal("10.00"), new BigDecimal("20.00")),
                new BasketItemDto(PRODUCT_2_UUID, 3, new BigDecimal("15.50"), new BigDecimal("30.00")));

        OrderAddressDto orderAddressDto = new OrderAddressDto("Prosta", "999-333", "Krzewie", "4");

        return new CreateOrderCommand(CUSTOMER_UUID, RESTAURANT_UUID, new BigDecimal("66.50"), basketItemsDto, orderAddressDto);
    }

    private CreateOrderCommand aOrderCommandWithWrongBasketPrice() {
        List<BasketItemDto> basketItemsDto = List.of(new BasketItemDto(PRODUCT_1_UUID, 2, new BigDecimal("10.00"), new BigDecimal("10.00")),
                new BasketItemDto(PRODUCT_2_UUID, 3, new BigDecimal("15.50"), new BigDecimal("30.00")));

        OrderAddressDto orderAddressDto = new OrderAddressDto("Prosta", "999-333", "Krzewie", "4");

        return new CreateOrderCommand(CUSTOMER_UUID, RESTAURANT_UUID, new BigDecimal("56.50"), basketItemsDto, orderAddressDto);
    }
}
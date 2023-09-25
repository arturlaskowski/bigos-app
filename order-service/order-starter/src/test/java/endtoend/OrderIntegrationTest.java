
package endtoend;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.OrderStatus;
import com.bigos.order.application.OrderApplicationService;
import com.bigos.order.application.command.dto.BasketItemDto;
import com.bigos.order.application.command.dto.CreateOrderCommand;
import com.bigos.order.application.command.dto.OrderAddressDto;
import com.bigos.order.domain.core.Order;
import com.bigos.order.domain.exception.OrderDomainException;
import com.bigos.order.domain.port.OrderRepository;
import com.bigos.order.starter.OrderServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(classes = OrderServiceApplication.class)
@Sql(value = {"classpath:sql/OrderIntegrationTestCleanUp.sql"}, executionPhase = AFTER_TEST_METHOD)
class OrderIntegrationTest {

    public static final UUID CUSTOMER_UUID = UUID.fromString("4ece4645-2658-4c54-a182-b0edcfa46d3b");
    public static final UUID RESTAURANT_UUID = UUID.fromString("0d94b4a5-a33a-42f5-995f-081c193441ee");
    public static final UUID PRODUCT_1_UUID = UUID.fromString("1ecf862b-6395-412c-ba56-59d38e2764a3");
    public static final UUID PRODUCT_2_UUID = UUID.fromString("9ce63b2e-50bd-4f32-a967-4cc17bd32225");

    @Autowired
    OrderApplicationService orderApplicationService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void cannotCreateOrderWithWrongPrice() {
        //given
        var createOrderCommandWithWrongPrice = aOrderCommandWithWrongPrice();

        //expected
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderApplicationService.createOrder(createOrderCommandWithWrongPrice));
    }

    @Test
    void cannotCreateOrderWithWrongBasketPrice() {
        //given
        var createOrderCommandWithBasketPrice = aOrderCommandWithWrongBasketPrice();

        //expected
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderApplicationService.createOrder(createOrderCommandWithBasketPrice));
    }

    @Test
    void canCreateOrder() {
        //given
        CreateOrderCommand createOrderCommand = aOrderCommand();

        //when
        OrderId orderId = orderApplicationService.createOrder(createOrderCommand);
        Order order = orderRepository.getOrder(orderId);

        //then
        //mapping order
        assertNotNull(order.getId().id());
        assertEquals(createOrderCommand.customerId(), order.getCustomerId().id());
        assertEquals(createOrderCommand.restaurantId(), order.getRestaurantId().id());
        assertEquals(createOrderCommand.price().stripTrailingZeros(), order.getPrice().amount().stripTrailingZeros());
        assertEquals(OrderStatus.PENDING, order.getStatus());

        //mapping address
        assertEquals(createOrderCommand.address().street(), order.getDeliveryAddress().getStreet());
        assertEquals(createOrderCommand.address().city(), order.getDeliveryAddress().getCity());
        assertEquals(createOrderCommand.address().postalCode(), order.getDeliveryAddress().getPostCode());
        assertEquals(createOrderCommand.address().houseNo(), order.getDeliveryAddress().getHouseNo());

        //mapping basket
        order.getBasket().forEach(basketItem -> {
            var basketItemDtoOptional = createOrderCommand.items().stream()
                    .filter(dto -> dto.productId().equals(basketItem.getProduct().getId().id())).findFirst();
            assertThat(basketItemDtoOptional).isPresent();

            var basketItemDto = basketItemDtoOptional.get();
            assertEquals(basketItemDto.price().stripTrailingZeros(), basketItem.getProduct().getPrice().amount().stripTrailingZeros());
            assertEquals(basketItemDto.quantity(), basketItem.getQuantity().numberOfElements());
            assertEquals(basketItemDto.totalPrice().stripTrailingZeros(), basketItem.getTotalPrice().amount().stripTrailingZeros());
            assertNotNull(basketItem.getItemNumber());
            assertNotNull(basketItem.getOrderId());
        });
    }

    private static CreateOrderCommand aOrderCommand() {
        List<BasketItemDto> basketItemsDto = List.of(new BasketItemDto(PRODUCT_1_UUID, 2, new BigDecimal("10.00"), new BigDecimal("20.00")),
                new BasketItemDto(PRODUCT_2_UUID, 3, new BigDecimal("15.50"), new BigDecimal("46.50")));

        OrderAddressDto orderAddressDto = new OrderAddressDto("Prosta", "999-333", "Krzewie", "4");

        return new CreateOrderCommand(CUSTOMER_UUID, RESTAURANT_UUID, new BigDecimal("66.50"), basketItemsDto, orderAddressDto);
    }

    private static CreateOrderCommand aOrderCommandWithWrongPrice() {
        List<BasketItemDto> basketItemsDto = List.of(new BasketItemDto(PRODUCT_1_UUID, 2, new BigDecimal("10.00"), new BigDecimal("20.00")),
                new BasketItemDto(PRODUCT_2_UUID, 3, new BigDecimal("15.50"), new BigDecimal("30.00")));

        OrderAddressDto orderAddressDto = new OrderAddressDto("Prosta", "999-333", "Krzewie", "4");

        return new CreateOrderCommand(CUSTOMER_UUID, RESTAURANT_UUID, new BigDecimal("66.50"), basketItemsDto, orderAddressDto);
    }

    private static CreateOrderCommand aOrderCommandWithWrongBasketPrice() {
        List<BasketItemDto> basketItemsDto = List.of(new BasketItemDto(PRODUCT_1_UUID, 2, new BigDecimal("10.00"), new BigDecimal("10.00")),
                new BasketItemDto(PRODUCT_2_UUID, 3, new BigDecimal("15.50"), new BigDecimal("30.00")));

        OrderAddressDto orderAddressDto = new OrderAddressDto("Prosta", "999-333", "Krzewie", "4");

        return new CreateOrderCommand(CUSTOMER_UUID, RESTAURANT_UUID, new BigDecimal("56.50"), basketItemsDto, orderAddressDto);
    }
}


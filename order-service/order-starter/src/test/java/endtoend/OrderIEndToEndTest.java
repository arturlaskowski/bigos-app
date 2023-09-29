
package endtoend;

import com.bigos.order.adapters.rest.dto.CreateOrderRequest;
import com.bigos.order.adapters.rest.dto.CreateOrderResponse;
import com.bigos.order.adapters.rest.dto.GetOrderDetailsResponse;
import com.bigos.order.application.OrderApplicationService;
import com.bigos.order.application.command.dto.BasketItemDto;
import com.bigos.order.application.command.dto.CreateOrderCommand;
import com.bigos.order.application.command.dto.OrderAddressDto;
import com.bigos.order.starter.OrderServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.bigos.common.domain.vo.OrderStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = {"classpath:sql/OrderIntegrationTestCleanUp.sql"}, executionPhase = AFTER_TEST_METHOD)
class OrderIEndToEndTest {

    public static final UUID CUSTOMER_UUID = UUID.fromString("4ece4645-2658-4c54-a182-b0edcfa46d3b");
    public static final UUID RESTAURANT_UUID = UUID.fromString("0d94b4a5-a33a-42f5-995f-081c193441ee");
    public static final UUID PRODUCT_1_UUID = UUID.fromString("1ecf862b-6395-412c-ba56-59d38e2764a3");
    public static final UUID PRODUCT_2_UUID = UUID.fromString("9ce63b2e-50bd-4f32-a967-4cc17bd32225");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Test
    void canCreateOrder() {
        //given
        var createOrderRequest = aCreateOrderRequest();

        // when
        ResponseEntity<CreateOrderResponse> response = restTemplate.postForEntity("/orders",
                createOrderRequest, CreateOrderResponse.class);

        // then
        assertThat(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.OK.value())
                .hasFieldOrProperty("body")
                .extracting(ResponseEntity::getBody)
                .hasFieldOrProperty("orderId");
    }

    @Test
    void canGetOrder() {
        //given
        var createOrderCommand = aOrderCommand();
        var orderId = orderApplicationService.createOrder(createOrderCommand);

        //when
        ResponseEntity<GetOrderDetailsResponse> getResponse = restTemplate.exchange(
                "/orders/{id}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                orderId.id());

        //then
        assertThat(getResponse)
                .hasFieldOrPropertyWithValue("status", HttpStatus.OK.value())
                .hasFieldOrProperty("body")
                .extracting(ResponseEntity::getBody)
                .hasFieldOrProperty("orderId")
                .hasFieldOrPropertyWithValue("customerId", createOrderCommand.customerId())
                .hasFieldOrPropertyWithValue("restaurantId", createOrderCommand.restaurantId())
                .hasFieldOrPropertyWithValue("status", PENDING)
                .hasFieldOrPropertyWithValue("price", createOrderCommand.price())
                .hasFieldOrPropertyWithValue("failureMessages", null)
                .extracting(GetOrderDetailsResponse::items)
                .satisfies(basketItems -> {
                    assertThat(basketItems.get(0).itemNumber()).isEqualTo(1);
                    assertThat(basketItems.get(0).productId()).isEqualTo(createOrderCommand.items().get(0).productId());
                    assertThat(basketItems.get(0).quantity()).isEqualTo(createOrderCommand.items().get(0).quantity());
                    assertThat(basketItems.get(0).price()).isEqualTo(createOrderCommand.items().get(0).price());
                    assertThat(basketItems.get(0).totalPrice()).isEqualTo(createOrderCommand.items().get(0).totalPrice());
                });

        assertThat(getResponse)
                .extracting(ResponseEntity::getBody)
                .extracting(GetOrderDetailsResponse::address)
                .hasFieldOrPropertyWithValue("street", createOrderCommand.address().street())
                .hasFieldOrPropertyWithValue("postalCode", createOrderCommand.address().postalCode())
                .hasFieldOrPropertyWithValue("city", createOrderCommand.address().city())
                .hasFieldOrPropertyWithValue("houseNo", createOrderCommand.address().houseNo());
    }

    private CreateOrderRequest aCreateOrderRequest() {
        var basketItemCreateRequest = new CreateOrderRequest.BasketItemCreateRequest(PRODUCT_1_UUID, 2, new BigDecimal(10), new BigDecimal(20));
        var basketItemCreateRequest2 = new CreateOrderRequest.BasketItemCreateRequest(PRODUCT_2_UUID, 2, new BigDecimal(20), new BigDecimal(40));
        var orderAddressCreateRequest = new CreateOrderRequest.OrderAddressCreateRequest("Cicha", "94-000", "Krzewie", "19");

        return new CreateOrderRequest(CUSTOMER_UUID, RESTAURANT_UUID, new BigDecimal("60.00"),
                List.of(basketItemCreateRequest, basketItemCreateRequest2), orderAddressCreateRequest);
    }

    private CreateOrderCommand aOrderCommand() {
        var basketItemsDto = List.of(new BasketItemDto(PRODUCT_1_UUID, 2, new BigDecimal("10.00"), new BigDecimal("20.00")));
        var orderAddressDto = new OrderAddressDto("Prosta", "999-333", "Krzewie", "4");

        return new CreateOrderCommand(CUSTOMER_UUID, RESTAURANT_UUID, new BigDecimal("20.00"), basketItemsDto, orderAddressDto);
    }
}

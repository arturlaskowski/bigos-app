insert into "order".orders(id, customer_id, restaurant_id, price, status, creation_date)
values ('8942f16e-1137-401b-9055-3701c0252877', '5ca03955-f73a-4e74-abcc-35adf4d68fda',
        '77243579-81d9-477f-85a5-f8cc327db0f4', 100.00, 'PENDING', current_timestamp);

insert into "order".basket_items(item_number, order_id, product_id, price, quantity, total_price)
values (1, '8942f16e-1137-401b-9055-3701c0252877', '49bd6cfc-70b1-4679-a9eb-8a9ce8bb9995', 100.00, 1, 100.00);

insert into "order".order_address(id, order_id, street, post_code, city, house_no)
values ('096c6e2d-5404-4e4e-818b-d72b2c083ceb', '8942f16e-1137-401b-9055-3701c0252877', 'Kokosowa', '87-840', 'Krzewie',
        '20');

insert into "order".order_outbox(id, saga_id, created_date, aggregate_id, aggregate_name, message_type,
                                 saga_status, outbox_status, payload, payload_type, version)
values ('64bcacc7-4175-4069-8e22-c3fa1ddda6b0', '9be90e1a-f43c-4ecc-a68f-7d2987478492', current_timestamp,
        '8942f16e-1137-401b-9055-3701c0252877', 'Order', 'OrderCreatedOutboxMessage',
        'STARTED', 'STARTED', '{
   "price":100.00,
   "basket":[
      {
         "price":100.00,
         "quantity":1,
         "productId":"1ecf862b-6395-412c-ba56-59d38e2764a3",
         "itemNumber":1,
         "totalPrice":100.00
      }
   ],
   "status":"PENDING",
   "orderId":"8942f16e-1137-401b-9055-3701c0252877",
   "customerId":"5ca03955-f73a-4e74-abcc-35adf4d68fda",
   "creationDate":"2022-08-20T19:49:56.378119600Z",
   "restaurantId":"77243579-81d9-477f-85a5-f8cc327db0f4",
   "deliveryAddress":{
      "city":"Krzewie",
      "street":"Cicha",
      "houseNo":"19",
      "postCode":"87-820"
   }
}', 'com.bigos.order.adapters.outbox.dto.OrderEventPayload', 0);
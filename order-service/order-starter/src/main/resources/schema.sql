drop schema if exists "order" cascade;

create schema "order";

create extension if not exists "uuid-ossp";

drop type if exists order_status cascade;
create type order_status as enum ('PENDING', 'PAID', 'APPROVED', 'CANCELLED', 'CANCELLING');
create cast (character varying as order_status) with inout as implicit;

drop table if exists "order".orders cascade;

create table "order".orders
(
    id               uuid           not null,
    customer_id      uuid           not null,
    restaurant_id    uuid           not null,
    price            numeric(10, 2) not null,
    status           order_status   not null,
    creation_date    timestamp      not null,
    failure_messages varchar(200),
    constraint orders_pkey primary key (id)
);

drop table if exists "order".basket_items cascade;

create table "order".basket_items
(
    item_number bigint         not null,
    order_id    uuid           not null,
    product_id  uuid           not null,
    price       numeric(10, 2) not null,
    quantity    integer        not null,
    total_price numeric(10, 2) not null,
    constraint order_items_pkey primary key (item_number, order_id)
);

alter table "order".basket_items
    add constraint fk_order_id
        foreign key (order_id)
            references "order".orders (id) match simple
            on update no action
            on delete cascade
        not valid;

drop table if exists "order".order_address cascade;

create table "order".order_address
(
    id        uuid        not null,
    order_id  uuid unique not null,
    street    varchar(50) not null,
    post_code varchar(50) not null,
    city      varchar(50) not null,
    house_no  varchar(10),
    constraint order_address_pkey primary key (id, order_id)
);

alter table "order".order_address
    add constraint fk_order_id
        foreign key (order_id)
            references "order".orders (id) match simple
            on update no action
            on delete cascade
        not valid;

drop table if exists "order".outbox cascade;

create table "order".order_outbox
(
    id             uuid         not null,
    saga_id        uuid         not null,
    created_date   timestamp    not null,
    send_date      timestamp,
    processed_date timestamp,
    aggregate_id   uuid         not null,
    aggregate_name varchar(50)  not null,
    message_type   varchar(100) not null,
    saga_status    varchar(50)  not null,
    outbox_status  varchar(50)  not null,
    payload        jsonb        not null,
    payload_type   varchar(200) not null,
    version        integer      not null,
    constraint outbox_pkey primary key (id)
);

create index "order_outbox_saga_status"
    on "order".order_outbox
        (message_type, outbox_status, saga_status);

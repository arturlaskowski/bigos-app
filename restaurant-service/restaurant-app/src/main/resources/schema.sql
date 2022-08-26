drop schema if exists restaurant cascade;

create schema restaurant;

create extension if not exists "uuid-ossp";

drop type if exists approval_status cascade;

create type approval_status as enum ('ACCEPTED', 'REJECTED');
create cast (character varying as approval_status) with inout as implicit;

drop table if exists "restaurant".restaurants cascade;

create table "restaurant".restaurants
(
    id        uuid    not null,
    available boolean not null,
    name      varchar(100),
    constraint restaurants_pkey primary key (id)
);

drop table if exists "restaurant".order_processed cascade;

create table "restaurant".order_processed
(
    id              uuid            not null,
    restaurant_id   uuid            not null,
    price           numeric(10, 2)  not null,
    approval_status approval_status not null,
    constraint order_processed_pkey primary key (id)
);

drop table if exists "restaurant".order_items cascade;

create table "restaurant".order_items
(
    id         uuid           not null,
    product_id uuid           not null,
    price      numeric(10, 2) not null,
    quantity   integer        not null,
    order_id   uuid           not null,
    constraint order_items_pkey primary key (id)
);

create table "restaurant".order_processed_outbox
(
    id              uuid         not null,
    saga_id         uuid         not null,
    created_date    timestamp    not null,
    send_date       timestamp,
    aggregate_id    uuid         not null,
    aggregate_name  varchar(50)  not null,
    message_type    varchar(100) not null,
    approval_status varchar(50)  not null,
    outbox_status   varchar(50)  not null,
    payload         jsonb        not null,
    payload_type    varchar(200) not null,
    version         integer      not null,
    constraint outbox_pkey primary key (id)
);

create index "order_outbox_approval_status"
    on "restaurant".order_processed_outbox
        (message_type, approval_status);

create unique index "order_outbox_saga_id_approval_status"
    on "restaurant".order_processed_outbox
        (message_type, saga_id, approval_status, outbox_status);
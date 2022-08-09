drop schema if exists restaurant cascade;

create schema restaurant;

create extension if not exists "uuid-ossp";

drop type if exists approval_status cascade;

create type approval_status as enum ('APPROVED', 'REJECTED');
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
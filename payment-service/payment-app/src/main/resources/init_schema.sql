drop schema if exists payment cascade;

create schema payment;

create extension if not exists "uuid-ossp";

drop type if exists payment_status cascade;

create type payment_status as enum ('COMPLETED', 'CANCELLED', 'REJECTED');
create cast (character varying as payment_status) with inout as implicit;

drop table if exists "payment".payments cascade;

create table "payment".payments
(
    id           uuid           not null,
    customer_id  uuid           not null,
    order_id     uuid           not null,
    price        numeric(10, 2) not null,
    creation_date timestamp      not null,
    status       payment_status not null,
    constraint payments_pkey primary key (id)
);

drop table if exists "payment".wallets cascade;

create table "payment".wallets
(
    id          uuid           not null,
    customer_id uuid           not null,
    amount      numeric(10, 2) not null,
    constraint wallets_pkey primary key (id)
);
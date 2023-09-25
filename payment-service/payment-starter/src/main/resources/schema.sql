drop schema if exists payment cascade;

create schema payment;

create extension if not exists "uuid-ossp";

drop type if exists payment_status cascade;

create type payment_status as enum ('COMPLETED', 'CANCELLED', 'REJECTED');
create cast (character varying as payment_status) with inout as implicit;

drop table if exists "payment".payments cascade;

create table "payment".payments
(
    id            uuid           not null,
    customer_id   uuid           not null,
    order_id      uuid           not null,
    price         numeric(10, 2) not null,
    creation_date timestamp      not null,
    status        payment_status not null,
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

drop table if exists "payment".outbox cascade;

create table "payment".payment_outbox
(
    id             uuid         not null,
    saga_id        uuid         not null,
    created_date   timestamp    not null,
    send_date      timestamp,
    aggregate_id   uuid         not null,
    aggregate_name varchar(50)  not null,
    message_type   varchar(100) not null,
    payment_status varchar(50)  not null,
    outbox_status  varchar(50)  not null,
    payload        jsonb        not null,
    payload_type   varchar(200) not null,
    version        integer      not null,
    constraint outbox_pkey primary key (id)
);

create index "payment_outbox_saga_status"
    on "payment".payment_outbox
        (message_type, payment_status);

create unique index "payment_outbox_saga_id_payment_status"
    on "payment".payment_outbox
        (message_type, saga_id, payment_status, outbox_status);
create table member
(
    member_id    uuid primary key,
    name         varchar      not null,
    phone_number char(11)     not null,
    created_at   timestamp(3) not null,
    updated_at   timestamp(3) not null
);

create table product
(
    product_id uuid primary key,
    name       varchar      not null,
    price      integer      not null,
    created_at timestamp(3) not null,
    updated_at timestamp(3) not null
);

create table orders
(
    orders_id             uuid primary key,
    member_id             uuid         not null references member on delete cascade,
    status                varchar      not null,
    receiver_name         varchar      not null,
    receiver_phone_number char(11)     not null,
    address               varchar      not null,
    message               varchar,
    created_at            timestamp(3) not null,
    updated_at            timestamp(3) not null

);

create table orders_product
(
    orders_product_id uuid primary key,
    orders_id         uuid         not null references orders on delete cascade,
    product_id        uuid         not null references product on delete cascade,
    quantity          smallint     not null,
    created_at        timestamp(3) not null,
    updated_at        timestamp(3) not null
);

CREATE INDEX orders_product_orders_id ON orders_product (orders_id);

create table payment
(
    payment_id      uuid primary key,
    orders_id       uuid         not null references orders on delete cascade,
    method          varchar      not null,
    method_data     varchar      not null,
    amount_shipping integer      not null,
    amount_products bigint       not null,
    has_discount    boolean      not null,
    amount_total    bigint       not null,
    created_at      timestamp(3) not null,
    updated_at      timestamp(3) not null
);

CREATE INDEX payment_orders_id ON payment (orders_id);

create table product
(
    id bigint auto_increment primary key,
    name varchar(255) not null,
    price int not null,
    image_url varchar(2083) not null,
    validated bit not null,
    deleted bit not null default 0
);

create table member
(
    identify_number bigint auto_increment primary key,
    email varchar(255) not null unique,
    password varchar(255) not null,
    authority varchar(255) not null
);

create table wish
(
    id bigint auto_increment primary key,
    member_id bigint not null
        references member (identify_number)
        on delete cascade,
    product_id bigint not null
        references product (id),
    added_at timestamp not null default current_timestamp,
    unique (member_id, product_id)
);
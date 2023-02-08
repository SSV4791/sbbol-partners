-- liquibase formatted sql
-- changeset 17888863:1675674210531_add_shedlock

drop table if exists shedlock;

create table shedlock(
    name varchar(64) not null,
    lock_until timestamp(3) not null,
    locked_at timestamp(3) not null default current_timestamp(3),
    locked_by VARCHAR(255) not null,
    primary key (name)
);

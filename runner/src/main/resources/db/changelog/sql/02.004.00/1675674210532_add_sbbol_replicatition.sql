-- liquibase formatted sql
-- changeset 17888863:1675674210532_add_sbbol_replicatition

drop table if exists sbbol_replication;

create table sbbol_replication(
    uuid UUID PRIMARY KEY,
    digital_Id varchar(40) not null,
    version bigint default 0 not null,
    entity_id UUID,
    entity_type varchar(50) not null,
    entity_status varchar(50) not null,
    retry integer not null,
    entity_data varchar not null,
    create_date timestamp(3) not null default current_timestamp(3),
    sys_lastchangedate timestamp not null
);

create index I_SBBOL_REPLICATION_ENTITY_STATUS_RETRY on sbbol_replication (entity_status, retry);
create index I_SBBOL_REPLICATION_DIGITAL_ID_ENTITY_ID_ENTITY_TYPE_ENTITY_STATUS_RETRY on sbbol_replication (digital_Id, entity_id, entity_type, entity_status, retry);

-- liquibase formatted sql
-- changeset 17866673:1697276539887_add_columns_for_sbbol_replication

alter table sbbol_replication
    add column request_id varchar(255);

alter table sbbol_replication
    add column error_message varchar(3000);

-- rollback alter table sbbol_replication drop column error_message
-- rollback alter table sbbol_replication drop column request_id

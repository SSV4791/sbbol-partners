-- liquibase formatted sql
-- changeset 17866673:1697276539888_add_column_session_id_for_sbbol_replication

truncate table sbbol_replication;

alter table sbbol_replication
    add column session_id UUID;

-- rollback alter table sbbol_replication drop column session_id

-- liquibase formatted sql
-- changeset 17866673:1692625249906_update_column_create_date_sbbol_replication

ALTER TABLE sbbol_replication
    ALTER COLUMN create_date DROP default;

ALTER TABLE sbbol_replication
    ALTER COLUMN create_date TYPE TIMESTAMP;

-- rollback alter table sbbol_replication alter column create_date type timestamp(3);
-- rollback alter table sbbol_replication alter column create_date default current_timestamp(3);

-- liquibase formatted sql
-- changeset 21293375:1701782592090_add_new_column_migration_date_for_partner

alter table partner
    add column migration_date TIMESTAMP;

COMMENT ON COLUMN partner.migration_date IS 'Дата и время миграции арендатора';

-- rollback alter table partner drop column migration_date;

-- liquibase formatted sql
-- changeset 21293375:1692625249805_update_column_region_address_table

ALTER TABLE address
ALTER COLUMN region TYPE VARCHAR (300);

-- rollback alter table address alter column region type varchar(50);

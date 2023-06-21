-- liquibase formatted sql
-- changeset 17888863:1687181518334_update_address

ALTER TABLE address
ALTER COLUMN building_block TYPE VARCHAR (300);

--rollback alter table address alter column building_block type varchar(20);

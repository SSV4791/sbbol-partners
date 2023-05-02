-- liquibase formatted sql
-- changeset 17888863:1683022500829_update_address

ALTER TABLE address
    ADD COLUMN area VARCHAR(300);

ALTER TABLE address
    ALTER COLUMN flat TYPE VARCHAR (300);

--rollback alter table address drop column area;
--rollback alter table address alter column flat type varchar(20);

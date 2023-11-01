-- liquibase formatted sql
-- changeset 21293375:1698758221494_add_column_partner_type_for_account.sql

ALTER TABLE account
    ADD COLUMN partner_type VARCHAR(254) default 'PARTNER' NOT NULL;
ALTER TABLE account
    ADD CONSTRAINT CK_ACCOUNT_PARTNER_TYPE CHECK
        (
                partner_type = 'PARTNER' OR
                partner_type = 'RENTER'
        );

-- rollback alter table account drop column partner_type
-- rollback alter table account drop constraint CK_ACCOUNT_PARTNER_TYPE

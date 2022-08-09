-- liquibase formatted sql
-- changeset 17480332:1660057076128_change_field_account_for_account

ALTER TABLE BANK_ACCOUNT
    ALTER COLUMN ACCOUNT DROP not null;

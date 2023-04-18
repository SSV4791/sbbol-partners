-- liquibase formatted sql
-- changeset 17888863:1683723052711_change_account

ALTER TABLE account
    ADD COLUMN currency_iso_code VARCHAR(3) NOT NULL DEFAULT 'RUB';

ALTER TABLE account
    ADD COLUMN currency_code VARCHAR(3) NOT NULL DEFAULT '643';

COMMENT ON COLUMN account.currency_iso_code IS 'ISO-код валюты счета';
COMMENT ON COLUMN account.currency_code IS 'Код валюты счета';

--rollback alter table account drop column currency_iso_code;
--rollback alter table account drop column currency_code;

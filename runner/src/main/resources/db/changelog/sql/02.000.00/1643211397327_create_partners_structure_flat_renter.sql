-- liquibase formatted sql
--changeset 17480332:1643211397327_create_partners_structure_flat_renter.sql

CREATE TABLE FLAT_RENTER
(
    uuid                  UUID PRIMARY KEY,
    partner_uuid          UUID NOT NULL,
    document_uuid         UUID,
    account_uuid          UUID,
    bank_uuid             UUID,
    bank_account_uuid     UUID,
    email_uuid            UUID,
    legal_address_uuid    UUID,
    physical_address_uuid UUID,
    phone_uuid            UUID
);

COMMENT ON TABLE FLAT_RENTER IS 'Служебная таблица для учета Арендаторов (Временная таблица на момент работы с старым API Арендаторов)';
COMMENT ON COLUMN FLAT_RENTER.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN FLAT_RENTER.PARTNER_UUID IS 'Уникальный идентификатор партнера';
COMMENT ON COLUMN FLAT_RENTER.DOCUMENT_UUID IS 'Уникальный идентификатор документа';
COMMENT ON COLUMN FLAT_RENTER.ACCOUNT_UUID IS 'Уникальный идентификатор счёта';
COMMENT ON COLUMN FLAT_RENTER.BANK_UUID IS 'Уникальный идентификатор банка';
COMMENT ON COLUMN FLAT_RENTER.BANK_ACCOUNT_UUID IS 'Уникальный идентификатор корреспондентского счёта';
COMMENT ON COLUMN FLAT_RENTER.EMAIL_UUID IS 'Уникальный идентификатор адреса электронной почты';
COMMENT ON COLUMN FLAT_RENTER.LEGAL_ADDRESS_UUID IS 'Уникальный идентификатор физического адреса';
COMMENT ON COLUMN FLAT_RENTER.PHYSICAL_ADDRESS_UUID IS 'Уникальный идентификатор адреса';
COMMENT ON COLUMN FLAT_RENTER.PHONE_UUID IS 'Уникальный идентификатор телефона';

CREATE UNIQUE INDEX I_FLAT_RENTER_PARTNER_UUID ON FLAT_RENTER (PARTNER_UUID);

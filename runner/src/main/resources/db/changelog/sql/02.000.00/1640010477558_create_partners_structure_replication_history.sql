-- liquibase formatted sql
--changeset 17480332:1640010477558_create_partners_structure_replication_history

CREATE TABLE REPLICATION_HISTORY
(
    uuid              UUID PRIMARY KEY,
    partner_uuid      UUID NOT NULL,
    document_uuid     UUID,
    account_uuid      UUID,
    bank_uuid         UUID,
    bank_account_uuid UUID,
    email_uuid        UUID,
    address_uuid      UUID,
    phone_uuid        UUID,
    sbbol_guid        VARCHAR(36)
);

COMMENT ON TABLE REPLICATION_HISTORY IS 'Служебная таблица для учета репликации в СББОЛ (Временная таблица на момент работы с СББОЛ)';
COMMENT ON COLUMN REPLICATION_HISTORY.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN REPLICATION_HISTORY.PARTNER_UUID IS 'Уникальный идентификатор партнера';
COMMENT ON COLUMN REPLICATION_HISTORY.DOCUMENT_UUID IS 'Уникальный идентификатор документа';
COMMENT ON COLUMN REPLICATION_HISTORY.ACCOUNT_UUID IS 'Уникальный идентификатор счёта';
COMMENT ON COLUMN REPLICATION_HISTORY.BANK_UUID IS 'Уникальный идентификатор банка';
COMMENT ON COLUMN REPLICATION_HISTORY.BANK_ACCOUNT_UUID IS 'Уникальный идентификатор корреспондентского счёта';
COMMENT ON COLUMN REPLICATION_HISTORY.EMAIL_UUID IS 'Уникальный идентификатор адреса электронной почты';
COMMENT ON COLUMN REPLICATION_HISTORY.ADDRESS_UUID IS 'Уникальный идентификатор адреса';
COMMENT ON COLUMN REPLICATION_HISTORY.PHONE_UUID IS 'Уникальный идентификатор телефона';
COMMENT ON COLUMN REPLICATION_HISTORY.SBBOL_GUID IS 'Уникальный идентификатор записи в legacy СББОЛ';

CREATE INDEX I_REPLICATION_HISTORY_PARTNER_UUID ON REPLICATION_HISTORY (PARTNER_UUID);
CREATE INDEX I_REPLICATION_HISTORY_DOCUMENT_UUID ON REPLICATION_HISTORY (DOCUMENT_UUID);
CREATE INDEX I_REPLICATION_HISTORY_ACCOUNT_UUID ON REPLICATION_HISTORY (ACCOUNT_UUID);
CREATE INDEX I_REPLICATION_HISTORY_BANK_UUID ON REPLICATION_HISTORY (BANK_UUID);
CREATE INDEX I_REPLICATION_HISTORY_BANK_ACCOUNT_UUID ON REPLICATION_HISTORY (BANK_ACCOUNT_UUID);
CREATE INDEX I_REPLICATION_HISTORY_EMAIL_UUID ON REPLICATION_HISTORY (EMAIL_UUID);
CREATE INDEX I_REPLICATION_HISTORY_ADDRESS_UUID ON REPLICATION_HISTORY (ADDRESS_UUID);
CREATE INDEX I_REPLICATION_HISTORY_PHONE_UUID ON REPLICATION_HISTORY (PHONE_UUID);
CREATE INDEX I_REPLICATION_HISTORY_SBBOL_GUID ON REPLICATION_HISTORY (SBBOL_GUID);
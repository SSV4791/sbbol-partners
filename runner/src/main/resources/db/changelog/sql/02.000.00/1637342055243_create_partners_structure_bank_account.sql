-- liquibase formatted sql
-- changeset 17480332:1637342055243_create_partners_structure_bank_account

CREATE TABLE BANK_ACCOUNT
(
    uuid               UUID PRIMARY KEY,
    bank_uuid          UUID             NOT NULL,
    version            BIGINT default 0 NOT NULL,
    account            VARCHAR(20)      NOT NULL,
    SYS_LASTCHANGEDATE TIMESTAMP        NOT NULL
);

COMMENT ON TABLE BANK_ACCOUNT IS 'Корреспондентский счёт банка';
COMMENT ON COLUMN BANK_ACCOUNT.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN BANK_ACCOUNT.BANK_UUID IS 'Уникальный идентификатор банка';
COMMENT ON COLUMN BANK_ACCOUNT.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN BANK_ACCOUNT.SYS_LASTCHANGEDATE IS 'Время изменения записи системное поле для сверок в двух контурах с помощью ПЖ';
COMMENT ON COLUMN BANK_ACCOUNT.ACCOUNT IS 'Счёт';

CREATE INDEX I_BANK_ACCOUNT_BANK_UUID ON BANK_ACCOUNT (BANK_UUID);

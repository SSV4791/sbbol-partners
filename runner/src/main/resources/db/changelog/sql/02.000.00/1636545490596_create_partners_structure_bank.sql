-- liquibase formatted sql
-- changeset 17480332:1636545490596_create_partners_structure_bank

CREATE TABLE BANK
(
    uuid               UUID PRIMARY KEY,
    account_uuid       UUID             NOT NULL,
    version            BIGINT default 0 NOT NULL,
    name               VARCHAR(50),
    bic                VARCHAR(9),
    intermediary       BOOLEAN,
    SYS_LASTCHANGEDATE TIMESTAMP        NOT NULL
);

COMMENT ON TABLE BANK IS 'Банки';
COMMENT ON COLUMN BANK.UUID IS 'Уникальный идентификатор банка';
COMMENT ON COLUMN BANK.ACCOUNT_UUID IS 'Уникальный идентификатор счёта';
COMMENT ON COLUMN BANK.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN BANK.SYS_LASTCHANGEDATE IS 'Время изменения записи системное поле для сверок в двух контурах с помощью ПЖ';
COMMENT ON COLUMN BANK.NAME IS 'Наименование банка';
COMMENT ON COLUMN BANK.BIC IS 'БИК банка';
COMMENT ON COLUMN BANK.INTERMEDIARY IS 'Признак /"Банк посредник/"';

CREATE INDEX I_BANK_ACCOUNT_UUID ON BANK (ACCOUNT_UUID);

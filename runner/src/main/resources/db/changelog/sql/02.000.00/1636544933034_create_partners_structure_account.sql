--liquibase formatted sql
--changeset 17480332:1636544933034_create_partners_structure_account

CREATE TABLE ACCOUNT
(
    uuid               UUID PRIMARY KEY,
    partner_uuid       UUID                  NOT NULL,
    digital_id         VARCHAR(40)           NOT NULL,
    version            BIGINT      default 0 NOT NULL,
    name               VARCHAR(50),
    account            VARCHAR(20),
    state              VARCHAR(10) default 'NOT_SIGNED',
    sign_collection_id VARCHAR(36),
    CONSTRAINT CK_ACCOUNT_STATE CHECK
        (STATE = 'SIGNED' OR
         STATE = 'NOT_SIGNED')
);

COMMENT ON TABLE ACCOUNT IS 'Счета';
COMMENT ON COLUMN ACCOUNT.UUID IS 'Уникальный идентификатор адреса';
COMMENT ON COLUMN ACCOUNT.DIGITAL_ID IS 'Идентификатор личного кабинета клиента';
COMMENT ON COLUMN ACCOUNT.PARTNER_UUID IS 'Уникальный идентификатор партнера';
COMMENT ON COLUMN ACCOUNT.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN ACCOUNT.NAME IS 'Наименование счёта';
COMMENT ON COLUMN ACCOUNT.ACCOUNT IS 'Счёт';
COMMENT ON COLUMN ACCOUNT.STATE IS 'Состояние \"Подписан\"';
COMMENT ON COLUMN ACCOUNT.SIGN_COLLECTION_ID IS 'Идентификатор подписи';

CREATE INDEX I_ACCOUNT_DIGITAL_ID ON ACCOUNT (DIGITAL_ID);
CREATE INDEX I_ACCOUNT_PARTNER_UUID ON ACCOUNT (PARTNER_UUID);

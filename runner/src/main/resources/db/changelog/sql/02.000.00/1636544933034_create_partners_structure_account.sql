-- liquibase formatted sql
-- changeset 17480332:1636544933034_create_partners_structure_account

CREATE TABLE ACCOUNT
(
    uuid               UUID PRIMARY KEY,
    create_date        TIMESTAMP             NOT NULL,
    partner_uuid       UUID                  NOT NULL,
    digital_id         VARCHAR(40)           NOT NULL,
    version            BIGINT      default 0 NOT NULL,
    priority_account   BOOLEAN     default false,
    account            VARCHAR(20),
    state              VARCHAR(10) default 'NOT_SIGNED',
    comment            VARCHAR(50),
    SYS_LASTCHANGEDATE TIMESTAMP             NOT NULL,
    CONSTRAINT CK_ACCOUNT_STATE CHECK
        (STATE = 'SIGNED' OR
         STATE = 'NOT_SIGNED')
);

COMMENT ON TABLE ACCOUNT IS 'Счёт';
COMMENT ON COLUMN ACCOUNT.UUID IS 'Уникальный идентификатор адреса';
COMMENT ON COLUMN ACCOUNT.CREATE_DATE IS 'Время создания записи';
COMMENT ON COLUMN ACCOUNT.SYS_LASTCHANGEDATE IS 'Время изменения записи системное поле для сверок в двух контурах с помощью ПЖ';
COMMENT ON COLUMN ACCOUNT.DIGITAL_ID IS 'Идентификатор личного кабинета клиента';
COMMENT ON COLUMN ACCOUNT.PARTNER_UUID IS 'Уникальный идентификатор партнера';
COMMENT ON COLUMN ACCOUNT.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN ACCOUNT.ACCOUNT IS 'Счёт';
COMMENT ON COLUMN ACCOUNT.PRIORITY_ACCOUNT IS 'Приоритетный избранный счёт';
COMMENT ON COLUMN ACCOUNT.STATE IS 'Состояние \"Подписан\"';
COMMENT ON COLUMN ACCOUNT.COMMENT IS 'Комментарий для счёта';

CREATE INDEX I_ACCOUNT_DIGITAL_ID ON ACCOUNT (DIGITAL_ID);
CREATE INDEX I_ACCOUNT_DIGITAL_ID_PARTNER_UUID ON ACCOUNT (DIGITAL_ID, PARTNER_UUID);
CREATE INDEX I_ACCOUNT_DIGITAL_ID_PARTNER_UUID_ACCOUNT ON ACCOUNT (DIGITAL_ID, PARTNER_UUID, ACCOUNT);
CREATE INDEX I_ACCOUNT_PARTNER_UUID ON ACCOUNT (PARTNER_UUID);

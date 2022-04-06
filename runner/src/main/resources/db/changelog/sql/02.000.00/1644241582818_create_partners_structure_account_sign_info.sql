-- liquibase formatted sql
-- changeset 17480332:1644241582818_create_partners_structure_account_sign_info

CREATE TABLE SIGN
(
    uuid                       UUID PRIMARY KEY,
    version                    BIGINT default 0 NOT NULL,
    entity_uuid                UUID             NOT NULL,
    digest                     VARCHAR(4000)    NOT NULL,
    sign                       VARCHAR(4000)    NOT NULL,
    partner_uuid               UUID             NOT NULL,
    account_uuid               UUID             NOT NULL,
    external_data_file_id      VARCHAR(255),
    external_data_sign_file_id VARCHAR(255),
    date_time_of_sign          TIMESTAMP
);

COMMENT ON TABLE SIGN IS 'Подписи';
COMMENT ON COLUMN SIGN.UUID IS 'Уникальный идентификатор подписи';
COMMENT ON COLUMN SIGN.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN SIGN.ENTITY_UUID IS 'Глобальный уникальный идентификатор объекта';
COMMENT ON COLUMN SIGN.DIGEST IS 'Дайджест';
COMMENT ON COLUMN SIGN.SIGN IS 'Подпись';
COMMENT ON COLUMN SIGN.PARTNER_UUID IS 'Уникальный идентификатор Партнера';
COMMENT ON COLUMN SIGN.ACCOUNT_UUID IS 'Уникальный идентификатор акаунта';
COMMENT ON COLUMN SIGN.EXTERNAL_DATA_FILE_ID IS 'Идентификатор созданного документа в ЕСМ';
COMMENT ON COLUMN SIGN.EXTERNAL_DATA_SIGN_FILE_ID IS 'Идентификатор созданного файла клиентской подписи документа в ЕСМ';

CREATE UNIQUE INDEX I_SIGN_ACCOUNT_UUID ON SIGN (ACCOUNT_UUID);

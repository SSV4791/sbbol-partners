-- liquibase formatted sql
-- changeset 17480332:1640009957159_create_partners_structure_phone

CREATE TABLE PHONE
(
    uuid         UUID PRIMARY KEY,
    digital_id   VARCHAR(40)      NOT NULL,
    unified_uuid UUID             NOT NULL,
    version      BIGINT default 0 NOT NULL,
    phone        VARCHAR(12)
);

COMMENT ON TABLE PHONE IS 'Телефоны';
COMMENT ON COLUMN PHONE.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN CONTACT.DIGITAL_ID IS 'Идентификатор личного кабинета клиента';
COMMENT ON COLUMN PHONE.UNIFIED_UUID IS 'Уникальный идентификатор связанной записи';
COMMENT ON COLUMN PHONE.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN PHONE.PHONE IS 'Телефон';

CREATE INDEX I_PHONE_UNIFIED_UUID ON PHONE (UNIFIED_UUID);

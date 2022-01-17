-- liquibase formatted sql
--changeset 17480332:1640010377139_create_partners_structure_email

CREATE TABLE EMAIL
(
    uuid         UUID PRIMARY KEY,
    unified_uuid UUID             NOT NULL,
    version      BIGINT default 0 NOT NULL,
    email        VARCHAR(100)
);

COMMENT ON TABLE EMAIL IS 'Адрес электронной почты';
COMMENT ON COLUMN EMAIL.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN EMAIL.UNIFIED_UUID IS 'Уникальный идентификатор связанной записи';
COMMENT ON COLUMN EMAIL.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN EMAIL.EMAIL IS 'Адрес электронной почты';

CREATE UNIQUE INDEX I_EMAIL_UNIFIED_UUID ON EMAIL (UNIFIED_UUID);

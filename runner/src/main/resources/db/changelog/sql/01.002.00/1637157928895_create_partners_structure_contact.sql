--liquibase formatted sql
--changeset 17480332:1637157928895_create_partners_structure_contact

CREATE TABLE CONTACT
(
    uuid         UUID PRIMARY KEY,
    partner_uuid UUID             NOT NULL,
    version      BIGINT default 0 NOT NULL,
    name         VARCHAR(50),
    position     VARCHAR(100),
    phone        VARCHAR(100),
    email        VARCHAR(320)
);

COMMENT ON TABLE CONTACT IS 'Контакты';
COMMENT ON COLUMN CONTACT.UUID IS 'Уникальный идентификатор контакта';
COMMENT ON COLUMN CONTACT.PARTNER_UUID IS 'Уникальный идентификатор партнера';
COMMENT ON COLUMN CONTACT.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN CONTACT.NAME IS 'Наименование/ФИО контактного лица';
COMMENT ON COLUMN CONTACT.POSITION IS 'Должность контактного лица';
COMMENT ON COLUMN CONTACT.PHONE IS 'Телефон контактного лица';
COMMENT ON COLUMN CONTACT.EMAIL IS 'Адрес электронной почты контактного лица';

CREATE INDEX I_CONTACT_PARTNER_UUID ON CONTACT (PARTNER_UUID);

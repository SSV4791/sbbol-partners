-- liquibase formatted sql
-- changeset 17480332:1637157928895_create_partners_structure_contact

CREATE TABLE CONTACT
(
    uuid         UUID PRIMARY KEY,
    partner_uuid UUID             NOT NULL,
    digital_id   VARCHAR(40)      NOT NULL,
    legal_type   VARCHAR(254)     NOT NULL,
    version      BIGINT default 0 NOT NULL,
    org_name     VARCHAR(350),
    first_name   VARCHAR(50),
    second_name  VARCHAR(50),
    middle_name  VARCHAR(50),
    position     VARCHAR(100),
    CONSTRAINT CK_CONTACT_LEGAL_TYPE CHECK
        (LEGAL_TYPE = 'LEGAL_ENTITY' OR
         LEGAL_TYPE = 'ENTREPRENEUR' OR
         LEGAL_TYPE = 'PHYSICAL_PERSON')
);

COMMENT ON TABLE CONTACT IS 'Контакты';
COMMENT ON COLUMN CONTACT.UUID IS 'Уникальный идентификатор контакта';
COMMENT ON COLUMN CONTACT.PARTNER_UUID IS 'Уникальный идентификатор партнера';
COMMENT ON COLUMN CONTACT.DIGITAL_ID IS 'Идентификатор личного кабинета клиента';
COMMENT ON COLUMN CONTACT.LEGAL_TYPE IS 'Организационно-правовая форма контакта';
COMMENT ON COLUMN CONTACT.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN CONTACT.ORG_NAME IS 'Наименование организации контактного лица';
COMMENT ON COLUMN CONTACT.FIRST_NAME IS 'Имя контактного лица';
COMMENT ON COLUMN CONTACT.SECOND_NAME IS 'Фамилия контактного лица';
COMMENT ON COLUMN CONTACT.MIDDLE_NAME IS 'Отчество контактного лица';
COMMENT ON COLUMN CONTACT.POSITION IS 'Должность контактного лица';

CREATE INDEX I_CONTACT_PARTNER_UUID ON CONTACT (PARTNER_UUID);
CREATE INDEX I_CONTACT_DIGITAL_ID ON CONTACT (DIGITAL_ID);

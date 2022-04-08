-- liquibase formatted sql
-- changeset 17480332:1636545764303_create_partners_structure_document

CREATE TABLE DOCUMENT
(
    uuid               UUID PRIMARY KEY,
    unified_uuid       UUID             NOT NULL,
    digital_id         VARCHAR(40)      NOT NULL,
    type_uuid          UUID,
    version            BIGINT default 0 NOT NULL,
    series             VARCHAR(50),
    number             VARCHAR(50),
    date_issue         DATE,
    division_issue     VARCHAR(250),
    division_code      VARCHAR(50),
    certifier_name     VARCHAR(100),
    position_certifier VARCHAR(100),
    certifier_type     VARCHAR(10)
);

COMMENT ON TABLE DOCUMENT IS 'Документы';
COMMENT ON COLUMN DOCUMENT.UUID IS 'Уникальный идентификатор адреса';
COMMENT ON COLUMN DOCUMENT.UNIFIED_UUID IS 'Уникальный идентификатор связанной записи';
COMMENT ON COLUMN DOCUMENT.DIGITAL_ID IS 'Идентификатор личного кабинета клиента';
COMMENT ON COLUMN DOCUMENT.TYPE_UUID IS 'Уникальный идентификатор записи справочника Документы';
COMMENT ON COLUMN DOCUMENT.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN DOCUMENT.SERIES IS 'Серия документа';
COMMENT ON COLUMN DOCUMENT.NUMBER IS 'Номер документа';
COMMENT ON COLUMN DOCUMENT.DATE_ISSUE IS 'Дата выдачи документа';
COMMENT ON COLUMN DOCUMENT.DIVISION_ISSUE IS 'Место выдачи документа';
COMMENT ON COLUMN DOCUMENT.DIVISION_CODE IS 'Код документа';
COMMENT ON COLUMN DOCUMENT.CERTIFIER_NAME IS 'ФИО сотрудника выдавшего документ';
COMMENT ON COLUMN DOCUMENT.POSITION_CERTIFIER IS 'Должность сотрудника выдавшего документ';
COMMENT ON COLUMN DOCUMENT.CERTIFIER_TYPE IS 'Тип удостоверяющего сотрудника';

CREATE INDEX I_DOCUMENT_PARTNER_UUID ON DOCUMENT (UNIFIED_UUID);
CREATE INDEX I_DOCUMENT_DIGITAL_ID ON DOCUMENT (DIGITAL_ID);

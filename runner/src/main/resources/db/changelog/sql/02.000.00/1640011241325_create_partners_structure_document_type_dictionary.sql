-- liquibase formatted sql
-- changeset 17480332:1640011241325_create_partners_structure_document_type_dictionary

CREATE TABLE DOCUMENT_TYPE_DICTIONARY
(
    uuid        UUID        PRIMARY KEY,
    system_name VARCHAR(50) NOT NULL,
    description VARCHAR(100),
    deleted     BOOLEAN
);

COMMENT ON TABLE DOCUMENT_TYPE_DICTIONARY IS 'Справочник типов документов';
COMMENT ON COLUMN DOCUMENT_TYPE_DICTIONARY.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN DOCUMENT_TYPE_DICTIONARY.SYSTEM_NAME IS 'Тип документа';
COMMENT ON COLUMN DOCUMENT_TYPE_DICTIONARY.DESCRIPTION IS 'Наименование типа документа';
COMMENT ON COLUMN DOCUMENT_TYPE_DICTIONARY.DELETED IS 'Признак "Удалён';

CREATE UNIQUE INDEX I_DOCUMENT_TYPE_DICTIONARY_SYSTEM_NAME ON DOCUMENT_TYPE_DICTIONARY (SYSTEM_NAME);

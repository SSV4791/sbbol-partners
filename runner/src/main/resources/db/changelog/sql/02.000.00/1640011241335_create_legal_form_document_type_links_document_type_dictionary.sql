-- liquibase formatted sql
-- changeset 19720953:1640011241335_create_document_legal_form_document_type_links_document_type_dictionary

CREATE TABLE LEGAL_FORM_DOCUMENT_TYPE
(
    uuid               UUID PRIMARY KEY,
    legal_form         VARCHAR(100),
    document_type_uuid UUID NOT NULL,
    CONSTRAINT CK_DOCUMENT_TYPE_SUBJECT CHECK
        (legal_form = 'LEGAL_ENTITY' OR
         legal_form = 'ENTREPRENEUR' OR
         legal_form = 'PHYSICAL_PERSON')
);

COMMENT ON TABLE LEGAL_FORM_DOCUMENT_TYPE IS 'Справочник для связи типов документов с правовыми формами субъектов';
COMMENT ON COLUMN LEGAL_FORM_DOCUMENT_TYPE.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN LEGAL_FORM_DOCUMENT_TYPE.LEGAL_FORM IS 'Тип субъекта';
COMMENT ON COLUMN LEGAL_FORM_DOCUMENT_TYPE.DOCUMENT_TYPE_UUID IS 'Идентификатор связанного типа документа';

CREATE INDEX I_DOCUMENT_TYPE_UUID ON LEGAL_FORM_DOCUMENT_TYPE (document_type_uuid)

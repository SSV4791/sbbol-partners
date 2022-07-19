-- liquibase formatted sql
-- changeset 19720953:1658247099898_add_new_field_version_for_legal_form_document_type

ALTER TABLE LEGAL_FORM_DOCUMENT_TYPE
    ADD COLUMN VERSION BIGINT default 0 NOT NULL;
ALTER TABLE LEGAL_FORM_DOCUMENT_TYPE
    ADD COLUMN SYS_LASTCHANGEDATE TIMESTAMP NOT NULL default '1970-01-01 00:00:00';

COMMENT ON COLUMN LEGAL_FORM_DOCUMENT_TYPE.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN LEGAL_FORM_DOCUMENT_TYPE.SYS_LASTCHANGEDATE IS 'Время изменения записи системное поле для сверок в двух контурах с помощью ПЖ';

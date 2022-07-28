-- liquibase formatted sql
-- changeset 19720953:1658247099898_add_new_field_version_for_legal_form_document_type

ALTER TABLE FLAT_RENTER
    ADD COLUMN digital_id VARCHAR(40) NOT NULL default '';

COMMENT ON COLUMN FLAT_RENTER.DIGITAL_ID IS 'Идентификатор личного кабинета клиента';

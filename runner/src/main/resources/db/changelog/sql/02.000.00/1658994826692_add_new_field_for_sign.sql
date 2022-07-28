-- liquibase formatted sql
-- changeset 19720953:1658994826692_add_new_field_for_sign

ALTER TABLE SIGN
    ADD COLUMN digital_id VARCHAR(40) NOT NULL default '';

COMMENT ON COLUMN SIGN.DIGITAL_ID IS 'Идентификатор личного кабинета клиента';

-- liquibase formatted sql
-- changeset 19720953:1658246983565_add_new_field_version_for_flat_renter

ALTER TABLE FLAT_RENTER
    ADD COLUMN version BIGINT default 0 NOT NULL;
ALTER TABLE FLAT_RENTER
    ADD COLUMN SYS_LASTCHANGEDATE TIMESTAMP NOT NULL default '1970-01-01 00:00:00';

COMMENT ON COLUMN FLAT_RENTER.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN FLAT_RENTER.SYS_LASTCHANGEDATE IS 'Время изменения записи системное поле для сверок в двух контурах с помощью ПЖ';

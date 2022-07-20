-- liquibase formatted sql
-- changeset 19720953:1658247081892_add_new_field_version_for_gku_inn_dictionary

ALTER TABLE BUDGET_MASK_DICTIONARY
    ADD COLUMN VERSION BIGINT default 0 NOT NULL;
ALTER TABLE BUDGET_MASK_DICTIONARY
    ADD COLUMN SYS_LASTCHANGEDATE TIMESTAMP NOT NULL default '1970-01-01 00:00:00';

COMMENT ON COLUMN BUDGET_MASK_DICTIONARY.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN BUDGET_MASK_DICTIONARY.SYS_LASTCHANGEDATE IS 'Время изменения записи системное поле для сверок в двух контурах с помощью ПЖ';

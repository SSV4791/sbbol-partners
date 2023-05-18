-- liquibase formatted sql
-- changeset 17888863:1683723052714_change_partner.sql

ALTER TABLE partner
    ADD COLUMN latin_name VARCHAR(255);

COMMENT ON COLUMN partner.latin_name IS 'Латинское наименование партнера';

--rollback alter table partner drop column latin_name;

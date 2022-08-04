-- liquibase formatted sql
-- changeset 19720953:1659595273123_add_new_field_for_crtj_clientlock

ALTER TABLE T_CRTJ_CLIENTLOCK
    ADD COLUMN TXID VARCHAR(255);

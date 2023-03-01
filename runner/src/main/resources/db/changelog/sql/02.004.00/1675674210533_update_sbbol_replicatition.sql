-- liquibase formatted sql
-- changeset 17888863:1675674210533_update_sbbol_replicatition

alter table sbbol_replication
    add digital_user_Id varchar(40);

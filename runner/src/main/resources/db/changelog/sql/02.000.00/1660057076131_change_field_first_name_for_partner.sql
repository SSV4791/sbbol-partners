-- liquibase formatted sql
-- changeset 17480332:1660057076131_change_field_firstname_for_partner

ALTER TABLE partner
    ALTER COLUMN first_name TYPE VARCHAR (350);

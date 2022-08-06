-- liquibase formatted sql
-- changeset 17480332:1659699847762_add_unique_index_for_account

CREATE UNIQUE INDEX idx_account_digital_id_partner_uuid_search
    ON ACCOUNT (digital_id, partner_uuid, search);

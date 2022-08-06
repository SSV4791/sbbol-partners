-- liquibase formatted sql
-- changeset 17480332:1659699736830_add_unique_index_for_partner

CREATE UNIQUE INDEX idx_partner_digital_id_search
    ON PARTNER (digital_id, search);

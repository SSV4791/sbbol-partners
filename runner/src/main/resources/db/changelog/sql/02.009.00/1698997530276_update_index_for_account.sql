-- liquibase formatted sql
-- changeset 21293375:1698997530276_update_index_for_account.sql

drop index i_account_partner_uuid_digital_id;
create index i_account_partner_uuid_digital_id_partner_type on account (partner_uuid, digital_id, partner_type);

--rollback create index i_account_partner_uuid_digital_id on account (partner_uuid, digital_id);
--rollback drop index i_account_partner_uuid_digital_id_partner_type;

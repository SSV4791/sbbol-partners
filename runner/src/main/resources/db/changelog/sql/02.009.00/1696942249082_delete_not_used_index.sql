-- liquibase formatted sql
-- changeset 17480332:1696942249082_delete_not_used_index

drop index i_phone_unified_uuid;
drop index i_email_unified_uuid;
drop index i_address_unified_uuid;
drop index i_address_digital_id;
drop index i_document_partner_uuid;
drop index i_document_digital_id;
drop index i_contact_partner_uuid;
drop index i_contact_digital_id;
drop index i_account_digital_id;
drop index i_account_partner_uuid;
drop index i_account_search;
drop index i_account_digital_id_partner_uuid;
drop index i_account_digital_id_partner_uuid_account;
drop index i_partner_search;
drop index i_partner_digital_id;
drop index i_partner_digital_id_inn;
drop index i_partner_digital_id_legal_type;

--rollback create index i_phone_unified_uuid on phone (unified_uuid);
--rollback create index i_email_unified_uuid on email (unified_uuid);
--rollback create index i_address_unified_uuid on address (unified_uuid);
--rollback create index i_address_digital_id on address (digital_id);
--rollback create index i_document_partner_uuid on document (unified_uuid);
--rollback create index i_document_digital_id on document (digital_id);
--rollback create index i_contact_partner_uuid on contact (partner_uuid);
--rollback create index i_contact_digital_id on contact (digital_id);
--rollback create index i_account_digital_id on account (digital_id);
--rollback create index i_account_partner_uuid on account (partner_uuid);
--rollback create index i_account_search on account (search);
--rollback create index i_account_digital_id_partner_uuid on account (digital_id, partner_uuid);
--rollback create index i_account_digital_id_partner_uuid_account on account (digital_id, partner_uuid, account);
--rollback create index i_partner_search on partner (search);
--rollback create index i_partner_digital_id on partner (digital_id);
--rollback create index i_partner_digital_id_inn on partner (digital_id, inn);
--rollback create index i_partner_digital_id_legal_type on partner (digital_id, legal_type);

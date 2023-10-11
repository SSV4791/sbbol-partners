-- liquibase formatted sql
-- changeset 17480332:1696942682653_add_new_index

create index i_phone_unified_uuid_digital_id on phone (unified_uuid, digital_id);
create index i_email_unified_uuid_digital_id on email (unified_uuid, digital_id);
create index i_address_unified_uuid_digital_id on address (unified_uuid, digital_id);
create index i_document_unified_uuid_digital_id on document (unified_uuid, digital_id);
create index i_contact_partner_uuid_digital_id on contact (partner_uuid, digital_id);
create index i_account_partner_uuid_digital_id on account (partner_uuid, digital_id);

--rollback drop index i_phone_unified_uuid_digital_id;
--rollback drop index i_email_unified_uuid_digital_id;
--rollback drop index i_address_unified_uuid_digital_id;
--rollback drop index i_document_unified_uuid_digital_id;
--rollback drop index i_contact_partner_uuid_digital_id;
--rollback drop index i_account_partner_uuid_digital_id;

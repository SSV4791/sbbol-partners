-- liquibase formatted sql
-- changeset 17480332:1699868109130_add_index_to_sys_lastchangedate

CREATE INDEX i_account_sys_lastchangedate ON account(sys_lastchangedate);
CREATE INDEX i_address_sys_lastchangedate ON address(sys_lastchangedate);
CREATE INDEX i_bank_sys_lastchangedate ON bank(sys_lastchangedate);
CREATE INDEX i_bank_account_sys_lastchangedate ON bank_account(sys_lastchangedate);
CREATE INDEX i_budget_mask_dictionary_sys_lastchangedate ON budget_mask_dictionary(sys_lastchangedate);
CREATE INDEX i_contact_sys_lastchangedate ON contact(sys_lastchangedate);
CREATE INDEX i_document_sys_lastchangedate ON document(sys_lastchangedate);
CREATE INDEX i_document_type_dictionary_sys_lastchangedate ON document_type_dictionary(sys_lastchangedate);
CREATE INDEX i_email_sys_lastchangedate ON email(sys_lastchangedate);
CREATE INDEX i_flat_renter_sys_lastchangedate ON flat_renter(sys_lastchangedate);
CREATE INDEX i_gku_inn_dictionary_sys_lastchangedate ON gku_inn_dictionary(sys_lastchangedate);
CREATE INDEX i_ids_history_sys_lastchangedate ON ids_history(sys_lastchangedate);
CREATE INDEX i_legal_form_document_type_sys_lastchangedate ON legal_form_document_type(sys_lastchangedate);
CREATE INDEX i_partner_sys_lastchangedate ON partner(sys_lastchangedate);
CREATE INDEX i_phone_sys_lastchangedate ON phone(sys_lastchangedate);
CREATE INDEX i_sbbol_replication_sys_lastchangedate ON sbbol_replication(sys_lastchangedate);
CREATE INDEX i_sign_sys_lastchangedate ON sign(sys_lastchangedate);

--rollback drop index i_account_sys_lastchangedate;
--rollback drop index i_address_sys_lastchangedate;
--rollback drop index i_bank_sys_lastchangedate;
--rollback drop index i_bank_account_sys_lastchangedate;
--rollback drop index i_budget_mask_dictionary_sys_lastchangedate;
--rollback drop index i_contact_sys_lastchangedate;
--rollback drop index i_document_sys_lastchangedate;
--rollback drop index i_document_type_dictionary_sys_lastchangedate;
--rollback drop index i_email_sys_lastchangedate;
--rollback drop index i_flat_renter_sys_lastchangedate;
--rollback drop index i_gku_inn_dictionary_sys_lastchangedate;
--rollback drop index i_ids_history_sys_lastchangedate;
--rollback drop index i_legal_form_document_type_sys_lastchangedate;
--rollback drop index i_partner_sys_lastchangedate;
--rollback drop index i_phone_sys_lastchangedate;
--rollback drop index i_sbbol_replication_sys_lastchangedate;
--rollback drop index i_sign_sys_lastchangedate;


-- liquibase formatted sql
-- changeset 17866673:1699868109140_add_index_to_account

create index if not exists ix_account_dgtl_id_prt_type_sys_lstchgedate on account (digital_id, partner_type, sys_lastchangedate);

--rollback drop index if exists ix_account_dgtl_id_prt_type_sys_lstchgedate;

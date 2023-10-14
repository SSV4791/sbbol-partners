-- liquibase formatted sql
-- changeset 17480332:1697276539886_change_budget_mask

insert into BUDGET_MASK_DICTIONARY(UUID, MASK, CONDITION, TYPE)
values ('6c692948-f6be-43d6-a2cf-db0cb088ddd5', '03231###############', '03231%', 'TAX_ACCOUNT_RECEIVER');

--rollback delete from BUDGET_MASK_DICTIONARY where uuid='6c692948-f6be-43d6-a2cf-db0cb088ddd5';

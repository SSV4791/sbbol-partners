-- liquibase formatted sql
-- changeset 17480332:1671180627481_add_parameter_in_budget_mask_dictionary

insert into BUDGET_MASK_DICTIONARY(UUID, MASK, CONDITION, TYPE)
values ('461e2366-fa83-4487-987a-51e2cb3f92b6', '40204###############', '40204%', 'GIS_GMP_ACCOUNT');

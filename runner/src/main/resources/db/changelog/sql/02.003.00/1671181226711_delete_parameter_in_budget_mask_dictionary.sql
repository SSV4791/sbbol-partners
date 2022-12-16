-- liquibase formatted sql
-- changeset 17480332:1671181226711_delete_parameter_in_budget_mask_dictionary

DELETE
FROM BUDGET_MASK_DICTIONARY
WHERE uuid = '9d7d3131-f435-44cf-b705-8ce899162e9a';

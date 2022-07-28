-- liquibase formatted sql
-- changeset 19720953:1657775422524_create_column_search_account_table
ALTER TABLE ACCOUNT
    ADD search VARCHAR(500);

COMMENT ON COLUMN ACCOUNT.search IS 'Информация для поиска счета';
CREATE INDEX I_ACCOUNT_SEARCH ON ACCOUNT (search)

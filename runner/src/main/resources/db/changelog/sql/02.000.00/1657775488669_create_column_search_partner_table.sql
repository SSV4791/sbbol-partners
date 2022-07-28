-- liquibase formatted sql
-- changeset 19720953:1657775488669_create_column_search_partner_table
ALTER TABLE PARTNER
    ADD search VARCHAR(1000);

COMMENT ON COLUMN PARTNER.search IS 'Информация для поиска партнера';
CREATE INDEX I_PARTNER_SEARCH ON PARTNER (search)

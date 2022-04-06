-- liquibase formatted sql
-- changeset 17480332:1637235453910_create_partners_structure_merge_history

CREATE TABLE MERGE_HISTORY
(
    uuid         UUID PRIMARY KEY,
    partner_uuid UUID NOT NULL,
    main_uuid    UUID NOT NULL,
    sbbol_uuid   VARCHAR(36)
);

COMMENT ON TABLE MERGE_HISTORY IS 'История слияния';
COMMENT ON COLUMN MERGE_HISTORY.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN MERGE_HISTORY.PARTNER_UUID IS 'Уникальный идентификатор Арендатора/Контрагента/Бенифициара';
COMMENT ON COLUMN MERGE_HISTORY.MAIN_UUID IS 'Уникальный идентификатор записи основного партнера';
COMMENT ON COLUMN MERGE_HISTORY.SBBOL_UUID IS 'Уникальный идентификатор записи в legacy СББОЛ';

CREATE UNIQUE INDEX I_MERGE_HISTORY_PARTNER_UUID ON MERGE_HISTORY (PARTNER_UUID);

--liquibase formatted sql
--changeset 17480332:1637235453910_create_partners_structure_merge_history

CREATE TABLE MERGE_HISTORY
(
    uuid      UUID NOT NULL,
    main_uuid UUID NOT NULL
);

COMMENT ON TABLE MERGE_HISTORY IS 'История слияния';
COMMENT ON COLUMN MERGE_HISTORY.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN MERGE_HISTORY.MAIN_UUID IS 'Уникальный идентификатор записи после слияния';

CREATE UNIQUE INDEX I_MERGE_HISTORY_UUID ON MERGE_HISTORY (UUID);

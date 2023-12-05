-- liquibase formatted sql
-- changeset 17480332:1701693969470_add_new_column_in_ids_history

ALTER TABLE ids_history
    ADD COLUMN parent_type VARCHAR(254) default 'ACCOUNT' NOT NULL;
ALTER TABLE ids_history
    ADD CONSTRAINT CK_IDS_HISTORY_TYPE CHECK
        (
                parent_type = 'PARTNER' OR
                parent_type = 'ACCOUNT'
        );

-- rollback alter table ids_history drop column parent_type

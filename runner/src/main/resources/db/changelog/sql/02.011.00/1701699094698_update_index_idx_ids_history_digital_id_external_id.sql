-- liquibase formatted sql
-- changeset 17480332:1701699094698_update_index_idx_ids_history_digital_id_external_id

drop index idx_ids_history_digital_id_external_id;
create unique index idx_ids_history_digital_id_external_id_parent_type on ids_history (digital_id, external_id, parent_type);

-- rollback drop index idx_ids_history_digital_id_external_id_parent_type;
-- rollback create unique index idx_ids_history_digital_id_external_id on ids_history (digital_id, external_id);

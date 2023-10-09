-- liquibase formatted sql
-- changeset 17888863:1696849380838_add_index_on_column_pprb_entity_id_for_ids_history

DROP INDEX idx_ids_history_digital_id_pprb_entity_id;
CREATE INDEX idx_ids_history_pprb_entity_id_digital_id ON ids_history (pprb_entity_id, digital_id);

--rollback drop index idx_ids_history_pprb_entity_id_digital_id;
--rollback create index idx_ids_history_digital_id_pprb_entity_id ON ids_history (digital_id, pprb_entity_id);

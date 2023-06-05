-- liquibase formatted sql
-- changeset 17888863:1685954089665_create_ids_history

CREATE TABLE ids_history
(
    uuid               UUID PRIMARY KEY,
    digital_id         VARCHAR(40)      NOT NULL,
    external_id        UUID             NOT NULL,
    version            BIGINT DEFAULT 0 NOT NULL,
    pprb_entity_id     UUID             NOT NULL,
    sys_lastchangedate TIMESTAMP        NOT NULL
);

COMMENT ON TABLE ids_history IS 'Информация об историчных/внешних идентификаторах сущностей';
COMMENT ON COLUMN ids_history.uuid IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN ids_history.digital_id IS 'Идентификатор личного кабинета клиента';
COMMENT ON COLUMN ids_history.external_id IS 'Внешний идентификатор сущности';
COMMENT ON COLUMN ids_history.version IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN ids_history.pprb_entity_id IS 'Уникальный идентификатор сущности в ППРБ';
COMMENT ON COLUMN IDS_HISTORY.SYS_LASTCHANGEDATE IS 'Время изменения записи системное поле для сверок в двух контурах с помощью ПЖ';

CREATE UNIQUE INDEX idx_ids_history_digital_id_external_id ON ids_history (digital_id, external_id);
CREATE INDEX idx_ids_history_digital_id_pprb_entity_id ON ids_history (digital_id, pprb_entity_id);

--rollback drop index idx_ids_history_digital_id_external_id;
--rollback drop index idx_ids_history_digital_id_pprb_entity_id;
--rollback drop table ids_history;

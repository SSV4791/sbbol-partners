-- liquibase formatted sql
-- changeset 17888863:1683723052713_add_account_additional

DROP TABLE IF EXISTS account_additional;

CREATE TABLE account_additional (
    uuid UUID PRIMARY KEY,
    digital_id VARCHAR(40) NOT NULL,
    account_uuid UUID NOT NULL,
    ground VARCHAR(2048),
    operation_code VARCHAR(5),
    operation_name VARCHAR(2048),
    commission_type VARCHAR(3),
    version BIGINT DEFAULT 0 NOT NULL,
    create_date TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    sys_lastchangedate TIMESTAMP NOT NULL
);

COMMENT ON TABLE account_additional IS 'Дополнительная информация по счету';
COMMENT ON COLUMN account_additional.uuid IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN account_additional.create_date IS 'Время создания записи';
COMMENT ON COLUMN account_additional.sys_lastchangedate IS 'Время изменения записи системное поле для сверок в двух контурах с помощью ПЖ';
COMMENT ON COLUMN account_additional.version IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN account_additional.digital_id IS 'Идентификатор личного кабинета клиента';
COMMENT ON COLUMN account_additional.account_uuid IS 'Ссылка на запись партнера';
COMMENT ON COLUMN account_additional.ground IS 'Назначение платежа';
COMMENT ON COLUMN account_additional.operation_code IS 'Код операции';
COMMENT ON COLUMN account_additional.operation_name IS 'Наименование операции';
COMMENT ON COLUMN account_additional.commission_type IS 'Тип комиссии';

CREATE INDEX idx_account_additional_account_uuid ON account_additional (account_uuid);
CREATE INDEX idx_account_additional_digital_id_account_uuid ON account_additional (digital_id, account_uuid);

--rollback drop index idx_account_additional_digital_id_account_uuid;
--rollback drop index idx_account_additional_account_uuid;
--rollback drop table account_additional;

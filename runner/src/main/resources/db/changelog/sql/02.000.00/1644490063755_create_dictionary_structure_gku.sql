-- liquibase formatted sql
-- changeset 17480332:1644490063755_create_dictionary_structure_gku

CREATE TABLE GKU_INN_DICTIONARY
(
    uuid UUID        PRIMARY KEY,
    inn  VARCHAR(12) NOT NULL
);

COMMENT ON TABLE GKU_INN_DICTIONARY IS 'Справочник ИНН организаций поставщиков услуг ЖКУ';
COMMENT ON COLUMN GKU_INN_DICTIONARY.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN GKU_INN_DICTIONARY.INN IS 'ИНН организации поставщика ЖКУ';

CREATE INDEX I_GKU_INN_DICTIONARY_INN ON GKU_INN_DICTIONARY (INN);

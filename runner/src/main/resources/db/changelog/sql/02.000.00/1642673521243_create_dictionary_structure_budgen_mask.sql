-- liquibase formatted sql
--changeset 17480332:1642673521243_create_dictionary_structure_budgen_mask.sql

CREATE TABLE BUDGET_MASK_DICTIONARY
(
    uuid      uuid primary key,
    mask      varchar(20) not null,
    condition varchar(20) not null,
    type      varchar(50) not null,

    CONSTRAINT CK_BUDGET_MASK_DICTIONARY_TYPE CHECK
        (
                TYPE = 'GIS_GMP_ACCOUNT' OR
                TYPE = 'BIC' OR
                TYPE = 'BUDGET_CORR_ACCOUNT' OR
                TYPE = 'TAX_ACCOUNT_RECEIVER' OR
                TYPE = 'BUDGET_ACCOUNT'
        )
);

COMMENT ON TABLE BUDGET_MASK_DICTIONARY IS 'Справочник бюджетных масок';
COMMENT ON COLUMN BUDGET_MASK_DICTIONARY.UUID IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN BUDGET_MASK_DICTIONARY.MASK IS 'Маска';
COMMENT ON COLUMN BUDGET_MASK_DICTIONARY.CONDITION IS 'Условия';
COMMENT ON COLUMN BUDGET_MASK_DICTIONARY.TYPE IS 'Тип Маски';

CREATE INDEX I_BUDGET_MASK_DICTIONARY_TYPE ON BUDGET_MASK_DICTIONARY (TYPE);

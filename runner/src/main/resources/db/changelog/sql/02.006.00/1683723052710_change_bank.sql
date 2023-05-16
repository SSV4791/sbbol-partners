-- liquibase formatted sql
-- changeset 17888863:1683723052710_change_bank

ALTER TABLE bank
    ADD COLUMN bank_type VARCHAR(50) NOT NULL DEFAULT 'DEFAULT';

ALTER TABLE bank
    ADD CONSTRAINT cnt_bank_type CHECK
        (
            bank_type = 'DEFAULT' OR
            bank_type = 'BENEFICIARY' OR
            bank_type = 'AGENT'
        );

ALTER TABLE bank
    ADD COLUMN swift_code VARCHAR(8);

ALTER TABLE bank
    ADD COLUMN clearing_country_code VARCHAR(2);

ALTER TABLE bank
    ADD COLUMN clearing_bank_code VARCHAR(31);

ALTER TABLE bank
    ADD COLUMN clearing_bank_symbol_code VARCHAR(2);

ALTER TABLE bank
    ADD COLUMN clearing_bank_code_name VARCHAR(140);

ALTER TABLE bank
    ADD COLUMN filial VARCHAR(70);

ALTER TABLE bank
    ADD COLUMN bank_option VARCHAR(1);

COMMENT ON COLUMN bank.bank_type IS 'Тип Банка (RU - Банк РФ; BENEFICIARY - Банк Берефициара; AGENT - Банк-посредник)';
COMMENT ON COLUMN bank.swift_code IS 'SWIFT-код Банка';
COMMENT ON COLUMN bank.clearing_country_code IS 'Клиринговый код страны Банка';
COMMENT ON COLUMN bank.clearing_bank_code IS 'Клиринговый код Банка';
COMMENT ON COLUMN bank.clearing_bank_symbol_code IS 'Символьный клиринговый код Банка';
COMMENT ON COLUMN bank.clearing_bank_code_name IS 'Наименование клирингового кода Банка';
COMMENT ON COLUMN bank.filial IS 'Филиал Банка';
COMMENT ON COLUMN bank.bank_option IS 'Опция Банка';

--rollback alter table bank drop constraint cnt_bank_type;
--rollback alter table bank drop column bank_type;
--rollback alter table bank drop column swift_code;
--rollback alter table bank drop column clearing_country_code;
--rollback alter table bank drop column clearing_bank_code;
--rollback alter table bank drop column clearing_bank_symbol_code;
--rollback alter table bank drop column clearing_bank_code_name;
--rollback alter table bank drop column filial;
--rollback alter table bank drop column bank_option;

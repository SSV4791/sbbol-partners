-- liquibase formatted sql
-- changeset 17888863:1683723052712_change_address

ALTER TABLE address
    ADD COLUMN country_code VARCHAR(3) NOT NULL DEFAULT 'RUS';

ALTER TABLE address
    ADD COLUMN country_iso_code VARCHAR(3) NOT NULL DEFAULT 'RU';

ALTER TABLE address
    ADD COLUMN country VARCHAR(80) NOT NULL DEFAULT 'Россия Russian Federation';

ALTER TABLE address
    ADD COLUMN administration_unit_code VARCHAR(10);

ALTER TABLE address
    ADD COLUMN administration_unit VARCHAR(70);

ALTER TABLE address
    ADD COLUMN full_address VARCHAR(1024);

COMMENT ON COLUMN address.country_code IS 'Код страны';
COMMENT ON COLUMN address.country_iso_code IS 'ISO-код страны';
COMMENT ON COLUMN address.country IS 'Наименование страны';
COMMENT ON COLUMN address.administration_unit_code IS 'Код административной единицы';
COMMENT ON COLUMN address.administration_unit IS 'Наименование административной единицы ';
COMMENT ON COLUMN address.full_address IS 'Полная строка адреса';

--rollback alter table address drop column country_code;
--rollback alter table address drop column country_iso_code;
--rollback alter table address drop column country;
--rollback alter table address drop column administration_unit_code;
--rollback alter table address drop column administration_unit;
--rollback alter table address drop column full_address;

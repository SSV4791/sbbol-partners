-- liquibase formatted sql
-- changeset 17480332:1636544855074_create_partners_structure_address

CREATE TABLE ADDRESS
(
    uuid               UUID PRIMARY KEY,
    unified_uuid       UUID             NOT NULL,
    digital_id         VARCHAR(40)      NOT NULL,
    version            BIGINT default 0 NOT NULL,
    type               VARCHAR(20),
    zip_code           VARCHAR(6),
    region_code        VARCHAR(10),
    region             VARCHAR(50),
    city               VARCHAR(300),
    location           VARCHAR(300),
    street             VARCHAR(300),
    building           VARCHAR(100),
    building_block     VARCHAR(20),
    flat               VARCHAR(20),
    SYS_LASTCHANGEDATE TIMESTAMP        NOT NULL,
    CONSTRAINT CK_ADDRESS_TYPE CHECK
        (
                TYPE = 'LEGAL_ADDRESS' OR
                TYPE = 'PHYSICAL_ADDRESS' OR
                TYPE = 'REGISTRATION_ADDRESS' OR
                TYPE = 'RESIDENTIAL_ADDRESS'
        )
);

COMMENT ON TABLE ADDRESS IS 'Адреса';
COMMENT ON COLUMN ADDRESS.UUID IS 'Уникальный идентификатор адреса';
COMMENT ON COLUMN ADDRESS.UNIFIED_UUID IS 'Уникальный идентификатор связанной записи';
COMMENT ON COLUMN ADDRESS.DIGITAL_ID IS 'Уникальный идентификатор связанной записи';
COMMENT ON COLUMN ADDRESS.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN ADDRESS.SYS_LASTCHANGEDATE IS 'Время изменения записи системное поле для сверок в двух контурах с помощью ПЖ';
COMMENT ON COLUMN ADDRESS.TYPE IS 'Тип адреса';
COMMENT ON COLUMN ADDRESS.ZIP_CODE IS 'Индекс';
COMMENT ON COLUMN ADDRESS.REGION_CODE IS 'Код регион';
COMMENT ON COLUMN ADDRESS.REGION IS 'Регион';
COMMENT ON COLUMN ADDRESS.CITY IS 'Город';
COMMENT ON COLUMN ADDRESS.LOCATION IS 'Населённый пункт';
COMMENT ON COLUMN ADDRESS.STREET IS 'Улица';
COMMENT ON COLUMN ADDRESS.BUILDING IS 'Дом';
COMMENT ON COLUMN ADDRESS.BUILDING_BLOCK IS 'Строение';
COMMENT ON COLUMN ADDRESS.FLAT IS 'Квартира';

CREATE INDEX I_ADDRESS_UNIFIED_UUID ON ADDRESS (UNIFIED_UUID);
CREATE INDEX I_ADDRESS_DIGITAL_ID ON ADDRESS (DIGITAL_ID);

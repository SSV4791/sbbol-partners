--liquibase formatted sql
--changeset 17480332:1636545764303_create_partners_structure_document

CREATE TABLE DOCUMENT
(
    uuid           UUID PRIMARY KEY,
    partner_uuid   UUID             NOT NULL,
    version        BIGINT default 0 NOT NULL,
    type           VARCHAR(254)     NOT NULL,
    series         VARCHAR(50),
    number         VARCHAR(50),
    date_issue     DATE,
    division_issue VARCHAR(250),
    division_code  VARCHAR(50),
    CONSTRAINT CK_DOCUMENT_TYPE CHECK
        (TYPE = 'PASSPORT_OF_RUSSIA' OR
         TYPE = 'SEAMAN_PASSPORT' OR
         TYPE = 'SERVICEMAN_IDENTITY_CARD_OF_RUSSIA' OR
         TYPE = 'FOREIGN_PASSPORT' OR
         TYPE = 'SERVICE_PASSPORT_OF_RUSSIA' OR
         TYPE = 'RF_CITIZEN_DIPLOMATIC_PASSPORT' OR
         TYPE = 'PASSPORT_OF_RUSSIA_WITH_CHIP')
);

COMMENT ON TABLE DOCUMENT IS 'Документы';
COMMENT ON COLUMN DOCUMENT.UUID IS 'Уникальный идентификатор адреса';
COMMENT ON COLUMN DOCUMENT.PARTNER_UUID IS 'Уникальный идентификатор партнера';
COMMENT ON COLUMN DOCUMENT.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN DOCUMENT.TYPE IS 'Тип документа';
COMMENT ON COLUMN DOCUMENT.SERIES IS 'Серия документа';
COMMENT ON COLUMN DOCUMENT.NUMBER IS 'Номер документа';
COMMENT ON COLUMN DOCUMENT.DATE_ISSUE IS 'Дата выдачи документа';
COMMENT ON COLUMN DOCUMENT.DIVISION_ISSUE IS 'Место выдачи документа';
COMMENT ON COLUMN DOCUMENT.DIVISION_CODE IS 'Код документа';

CREATE INDEX I_DOCUMENT_PARTNER_UUID ON DOCUMENT (PARTNER_UUID);

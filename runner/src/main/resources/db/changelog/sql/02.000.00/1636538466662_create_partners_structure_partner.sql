-- liquibase formatted sql
-- changeset 17480332:1636538466662_create_partners_structure_partner

CREATE TABLE PARTNER
(
    uuid               UUID PRIMARY KEY,
    create_date        TIMESTAMP                      NOT NULL,
    digital_id         VARCHAR(40)                    NOT NULL,
    version            BIGINT       default 0         NOT NULL,
    type               VARCHAR(254) default 'PARTNER' NOT NULL,
    legal_type         VARCHAR(254)                   NOT NULL,
    org_name           VARCHAR(350),
    first_name         VARCHAR(50),
    second_name        VARCHAR(50),
    middle_name        VARCHAR(50),
    inn                VARCHAR(12),
    kpp                VARCHAR(9),
    ogrn               VARCHAR(15),
    okpo               VARCHAR(30),
    citizenship        VARCHAR(20),
    comment            VARCHAR(255),
    SYS_LASTCHANGEDATE TIMESTAMP                      NOT NULL,
    CONSTRAINT CK_PARTNER_CITIZENSHIP CHECK
        (
                CITIZENSHIP = 'UNKNOWN' OR
                CITIZENSHIP = 'RUSSIA' OR
                CITIZENSHIP = 'ANOTHER_COUNTRY'
        ),
    CONSTRAINT CK_PARTNER_TYPE CHECK
        (
                TYPE = 'PARTNER' OR
                TYPE = 'BENEFICIARY' OR
                TYPE = 'RENTER'
        ),
    CONSTRAINT CK_PARTNER_LEGAL_TYPE CHECK
        (
                LEGAL_TYPE = 'LEGAL_ENTITY' OR
                LEGAL_TYPE = 'ENTREPRENEUR' OR
                LEGAL_TYPE = 'PHYSICAL_PERSON'
        )
);

COMMENT ON TABLE PARTNER IS 'Партнеры';
COMMENT ON COLUMN PARTNER.UUID IS 'Уникальный идентификатор партнера';
COMMENT ON COLUMN PARTNER.CREATE_DATE IS 'Время создания записи';
COMMENT ON COLUMN PARTNER.SYS_LASTCHANGEDATE IS 'Время изменения записи системное поле для сверок в двух контурах с помощью ПЖ';
COMMENT ON COLUMN PARTNER.DIGITAL_ID IS 'Идентификатор личного кабинета клиента';
COMMENT ON COLUMN PARTNER.VERSION IS 'Версия (служебное поле Hibernate)';
COMMENT ON COLUMN PARTNER.TYPE IS 'Тип партнера';
COMMENT ON COLUMN PARTNER.LEGAL_TYPE IS 'Организационно-правовая форма партнера';
COMMENT ON COLUMN PARTNER.ORG_NAME IS 'Наименование организации партнера';
COMMENT ON COLUMN PARTNER.FIRST_NAME IS 'Имя партнера';
COMMENT ON COLUMN PARTNER.SECOND_NAME IS 'Фамилия партнера';
COMMENT ON COLUMN PARTNER.MIDDLE_NAME IS 'Отчество партнера';
COMMENT ON COLUMN PARTNER.INN IS 'ИНН';
COMMENT ON COLUMN PARTNER.KPP IS 'КПП';
COMMENT ON COLUMN PARTNER.OGRN IS 'ОГРН';
COMMENT ON COLUMN PARTNER.OKPO IS 'ОКПО';
COMMENT ON COLUMN PARTNER.COMMENT IS 'Комментарий пользователя';
COMMENT ON COLUMN PARTNER.CITIZENSHIP IS 'Признак /"Гражданин РФ/", заполняется для физ. лиц';

CREATE INDEX I_PARTNER_DIGITAL_ID ON PARTNER (DIGITAL_ID);
CREATE INDEX I_PARTNER_DIGITAL_ID_INN ON PARTNER (DIGITAL_ID, INN);
CREATE INDEX I_PARTNER_DIGITAL_ID_LEGAL_TYPE ON PARTNER (DIGITAL_ID, LEGAL_TYPE);

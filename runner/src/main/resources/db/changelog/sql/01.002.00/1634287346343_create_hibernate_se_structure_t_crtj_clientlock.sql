-- Служебные таблицы HibernateSE
create table T_CRTJ_CLIENTLOCK
(
    CLIENT_ID           varchar(255) not null,
    SYS_LASTCHANGEDATE  timestamp    not null,
    MIGRATIONSTATUS     integer      not null,
    SYS_ISDELETED       boolean      not null,
    SYS_PARTITIONID     integer      not null,
    SYS_OWNERID         varchar(255),
    SYS_RECMODELVERSION varchar(255),
    CHGCNT              bigint,
    SILOCK              integer      not null,
    primary key (CLIENT_ID)
);

COMMENT ON TABLE T_CRTJ_CLIENTLOCK IS 'Служебная таблица HibernateSE';

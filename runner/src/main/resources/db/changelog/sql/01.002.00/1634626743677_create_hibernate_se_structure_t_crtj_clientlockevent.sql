create table T_CRTJ_CLIENTLOCKEVENT
(
    EVENT_ID            varchar(255) not null,
    SYS_LASTCHANGEDATE  timestamp    not null,
    CLIENT_ID           varchar(255) not null,
    INFO                varchar(255),
    TIMESTAMP_          timestamp,
    SYS_RECMODELVERSION varchar(255),
    GOTDATA             boolean,
    GOTULCK             boolean,
    GOTLCK              boolean,
    primary key (EVENT_ID)
);

COMMENT ON TABLE T_CRTJ_CLIENTLOCKEVENT IS 'Служебная таблица HibernateSE';

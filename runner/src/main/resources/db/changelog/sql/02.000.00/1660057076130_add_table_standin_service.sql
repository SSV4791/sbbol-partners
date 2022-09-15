-- Служебные таблицы HibernateSE
create table T_CRTJ_STANDIN_SERVICE
(
    PARTITION_ID        varchar(255) not null,
    SYS_LASTCHANGEDATE  timestamp not null default current_timestamp,
    PREV_STATE varchar(37),
    CUR_STATE varchar(37),
    CONF_STATE varchar(37),
    LOCK_VER bigint not null,
    CUR_VER bigint not null,
    CONF_VER bigint not null,
    LAST_HKEY varchar(255),
    ERROR_TX varchar(37),
    primary key (PARTITION_ID)
);

COMMENT ON TABLE T_CRTJ_STANDIN_SERVICE IS 'Служебная таблица HibernateSE';


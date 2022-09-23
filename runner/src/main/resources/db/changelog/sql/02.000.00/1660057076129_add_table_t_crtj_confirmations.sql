-- Служебные таблицы HibernateSE
create table T_CRTJ_CONFIRMATIONS
(
    TX_ID           varchar(37) not null,
    SYS_LASTCHANGEDATE  timestamp not null default current_timestamp,
    primary key (TX_ID)
);

COMMENT ON TABLE T_CRTJ_CONFIRMATIONS IS 'Служебная таблица HibernateSE';


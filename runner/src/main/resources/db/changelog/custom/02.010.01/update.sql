alter table account set(parallel_workers = 16);
SET max_parallel_workers = 16;
SET max_parallel_maintenance_workers = 8;
create index ix_account_dgtl_id_prt_type_sys_lstchgedate on account (digital_id,partner_type,sys_lastchangedate);
commit;
set max_parallel_maintenance_workers = 2;
alter table account reset(parallel_workers);
commit;

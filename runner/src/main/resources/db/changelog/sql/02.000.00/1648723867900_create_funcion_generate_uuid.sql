-- liquibase formatted sql
-- changeset 19720953:1648723867900_create_funcion_generate_uuid

create or replace function generate_uuid() returns uuid as
'
begin
    return uuid_in(md5(random()::TEXT || clock_timestamp()::TEXT)::CSTRING);
end;
'
language 'plpgsql';

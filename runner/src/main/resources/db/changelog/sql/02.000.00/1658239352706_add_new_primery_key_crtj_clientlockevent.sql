-- liquibase formatted sql
-- changeset 19720953:1648723867914_drop_funcion_generate_uuid

ALTER TABLE T_CRTJ_CLIENTLOCKEVENT
    DROP CONSTRAINT t_crtj_clientlockevent_pkey;

ALTER TABLE T_CRTJ_CLIENTLOCKEVENT
    ADD PRIMARY KEY (event_id, client_id);

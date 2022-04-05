-- liquibase formatted sql
-- changeset 19720953:1640012868073_create_table_filling_script_subject_document_type

-- Создание записей для типов документов
with document_type_dict as (
    select uuid, system_name
    from document_type_dictionary),

my_constanct_1 (name) AS (values ('ENTREPRENEUR'), ('PHYSICAL_PERSON')),
my_constanct_2 (name) AS (values ('LEGAL_ENTITY'), ('ENTREPRENEUR'), ('PHYSICAL_PERSON')),
my_constanct_3 (name) AS (values ('LEGAL_ENTITY')),
my_constanct_4 (name) AS (values ('LEGAL_ENTITY'), ('ENTREPRENEUR')),

INSERT_INTO_FL_IP as (
    insert into legal_form_document_type (uuid, document_type_uuid,legal_form)
        select generate_uuid(), document_type_dict.uuid, my_constanct_1.name
        from document_type_dict, my_constanct_1
        where document_type_dict.uuid = '3422aec8-7f44-4089-9a43-f8e3c5b00722'
            or document_type_dict.uuid = '8a4d4464-64a1-4f3d-ab86-fd3be614f7a2'
            or document_type_dict.uuid = '86257be3-aa4d-4a98-a957-9c55c2d6c860'
            or document_type_dict.uuid = '282561c6-a218-46a4-b47a-320192bdba6e'
            or document_type_dict.uuid = '944ceae7-537b-402e-87b6-d5eec2c888b6'
            or document_type_dict.uuid = '9e345e55-12ed-42d9-ad6b-dd5c4ea31407'
            or document_type_dict.uuid = '7540214c-c2ef-451a-a870-8a1ab9b7d514'),

INSERT_INTO_ALL as (
    insert into legal_form_document_type (uuid, document_type_uuid, legal_form)
        select generate_uuid(), document_type_dict.uuid, my_constanct_2.name
        from document_type_dict, my_constanct_2
        where document_type_dict.uuid = '6328fc2c-730b-4af4-b904-26afa592ab12'),

INSERT_INTO_UL as (
    insert into legal_form_document_type (uuid, document_type_uuid, legal_form)
        select generate_uuid(), document_type_dict.uuid, my_constanct_3.name
        from document_type_dict, my_constanct_3
        where document_type_dict.uuid = '32102672-28d4-49fe-a862-bb37e3bc3243')

    insert into legal_form_document_type (uuid, document_type_uuid, legal_form)
        select generate_uuid(), document_type_dict.uuid, my_constanct_4.name
        from document_type_dict, my_constanct_4
        where document_type_dict.uuid = 'fe88685a-9ab0-4128-b352-ef1f39a6db3e'
            or document_type_dict.uuid = 'ec3b4a9d-b317-4d00-beae-a86fad8042b4';

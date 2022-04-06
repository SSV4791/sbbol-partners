-- liquibase formatted sql
-- changeset 17480332:1640012868071_create_partners_structure_fill_document_type

insert into DOCUMENT_TYPE_DICTIONARY(UUID, system_name, description, deleted)
values ('3422aec8-7f44-4089-9a43-f8e3c5b00722', 'PASSPORT_OF_RUSSIA', 'Паспорт РФ', false),
       ('8a4d4464-64a1-4f3d-ab86-fd3be614f7a2', 'SEAMAN_PASSPORT', 'Паспорт моряка (удостоверение личности моряка)', false),
       ('86257be3-aa4d-4a98-a957-9c55c2d6c860', 'SERVICEMAN_IDENTITY_CARD_OF_RUSSIA', 'Удостоверение личности военнослужащего', false),
       ('282561c6-a218-46a4-b47a-320192bdba6e', 'FOREIGN_PASSPORT', 'Паспорт иностранного гражданина', false),
       ('944ceae7-537b-402e-87b6-d5eec2c888b6', 'SERVICE_PASSPORT_OF_RUSSIA', 'Служебный паспорт гражданина РФ', false),
       ('9e345e55-12ed-42d9-ad6b-dd5c4ea31407', 'RF_CITIZEN_DIPLOMATIC_PASSPORT', 'Дипломатический паспорт', false),
       ('7540214c-c2ef-451a-a870-8a1ab9b7d514', 'PASSPORT_OF_RUSSIA_WITH_CHIP', 'Паспорт гражданина РФ, содержащий электронный носитель информации', false),
       ('6328fc2c-730b-4af4-b904-26afa592ab12', 'INN', 'ИНН', false),
       ('32102672-28d4-49fe-a862-bb37e3bc3243', 'KPP', 'КПП', false),
       ('fe88685a-9ab0-4128-b352-ef1f39a6db3e', 'OGRN', 'ОГРН', false),
       ('ec3b4a9d-b317-4d00-beae-a86fad8042b4', 'OKPO', 'ОКПО', false);
--rollback truncate table DOCUMENT_TYPE_DICTIONARY


-- liquibase formatted sql
--changeset 17480332:1642673545563_create_dictionary_structure_fill_budgen_mask.sql

insert into BUDGET_MASK_DICTIONARY(UUID, MASK, TYPE)
values ('9371a524-c643-44c2-a6d6-9e7c178ce282', '40101###############', 'GIS_GMP_ACCOUNT'),
       ('0aa290dc-8ae2-4576-b22d-efc588b3d243', '40302###############', 'GIS_GMP_ACCOUNT'),
       ('179ad804-b845-4b7c-867f-c44e484a258c', '40501########2######', 'GIS_GMP_ACCOUNT'),
       ('772f201f-7aef-40c5-82c7-9246c2d902f3', '40601########1######', 'GIS_GMP_ACCOUNT'),
       ('a5ab0154-2424-4d90-8501-46611a574ae1', '40601########3######', 'GIS_GMP_ACCOUNT'),
       ('83cd02b7-2541-4fde-a750-3d40c9784441', '40701########1######', 'GIS_GMP_ACCOUNT'),
       ('5c523980-a08d-49cb-8303-9e055189ba53', '40701########3######', 'GIS_GMP_ACCOUNT'),
       ('3bc90e13-9b08-4a6f-a772-50d2a8938694', '40503########4######', 'GIS_GMP_ACCOUNT'),
       ('0fa087b3-167d-4cd6-8f4d-2dee06655aae', '40603########4######', 'GIS_GMP_ACCOUNT'),
       ('d133509b-8193-4ca8-81ce-9f414ad0f606', '40703########4######', 'GIS_GMP_ACCOUNT'),
       ('5fab3ac1-69f5-4916-8b7a-3834cec1a53d', '######000', 'BIC'),
       ('ade4e7c5-6aca-4936-8345-2bf74e98f356', '######001', 'BIC'),
       ('698a7f2a-c641-43fe-8c75-d85dc6f20197', '######002', 'BIC'),
       ('5dc06fc1-591d-4097-9600-baa9ee330b43', '40102###############', 'BUDGET_CORR_ACCOUNT'),
       ('3464cdc0-f40f-42d2-8d92-af4860b6483e', '0####643############', 'BUDGET_ACCOUNT'),
       ('e11a06e5-f770-4fd3-8bcd-75b12a96e924', '40105###############', 'TAX_ACCOUNT_RECEIVER'),
       ('43316c4c-a0ee-4ec4-826b-9946958b2809', '40402###############', 'TAX_ACCOUNT_RECEIVER'),
       ('3adb407d-4b53-494c-9a13-541902ddc68a', '03271###############', 'TAX_ACCOUNT_RECEIVER'),
       ('a5e694a3-1524-4221-b7bd-bc388b299603', '03211###############', 'TAX_ACCOUNT_RECEIVER'),
       ('6c90fcde-6daf-4834-85ba-901c2b437f7d', '03217###############', 'TAX_ACCOUNT_RECEIVER'),
       ('0ff6569d-4300-4ab4-9e4e-d90fed955b1e', '40201###############', 'TAX_ACCOUNT_RECEIVER'),
       ('9c05da85-f924-4125-b780-f9634490f5aa', '03221###############', 'TAX_ACCOUNT_RECEIVER'),
       ('9d7d3131-f435-44cf-b705-8ce899162e9a', '40204###############', 'TAX_ACCOUNT_RECEIVER'),
       ('9ee97b71-4de3-4efe-ba28-c4bd54c0c5ae', '40205###############', 'TAX_ACCOUNT_RECEIVER'),
       ('8838a589-3e83-4644-b3f8-ffcc5dfcff59', '03241###############', 'TAX_ACCOUNT_RECEIVER');

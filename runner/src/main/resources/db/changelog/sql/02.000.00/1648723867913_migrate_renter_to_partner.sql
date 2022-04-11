-- liquibase formatted sql
-- changeset 19720953:1648723867913_migrate_renter_to_partner
-- T_RENTER -> PARTNER, ACCOUNT, BANK, BANK_ACCOUNT
with RENTER_ROWS as (
    select uuid,
           DIGITALID,
           RENTERTYPE,
           LEGALNAME,
           FIRSTNAME,
           LASTNAME,
           MIDDLENAME,
           INN,
           KPP,
           OGRN,
           OKPO,
           PHONENUMBERS,
           EMAILS,
           ACCOUNT,
           BANKNAME,
           BANKBIC,
           BANKACCOUNT,
           object_id,
           DULSERIE,
           DULNUMBER,
           DULDATEISSUE,
           DULDIVISIONISSUE,
           DULDIVISIONCODE,
           DULTYPE
    from T_RENTER
),
-- T_RENTER -> PARTNER
     INSERT_INTO_PARTNER as (
insert into partner (uuid,
              DIGITAL_ID,
              TYPE,
              LEGAL_TYPE,
              ORG_NAME,
              FIRST_NAME,
              SECOND_NAME,
              MIDDLE_NAME,
              INN,
              KPP,
              OGRN,
              OKPO,
              create_date,
              last_modified_date)
select UUID::UUID,
       DIGITALID,
       'RENTER',
       RENTERTYPE,
       LEGALNAME,
       FIRSTNAME,
       LASTNAME,
       MIDDLENAME,
       INN,
       KPP,
       OGRN,
       OKPO,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
from RENTER_ROWS ON CONFLICT (uuid) DO
UPDATE
SET digital_id = excluded.digital_id,
    type = excluded.type,
    legal_type = excluded.legal_type,
    org_name = excluded.org_name,
    first_name = excluded.first_name,
    SECOND_NAME = excluded.second_name,
    MIDDLE_NAME = excluded.middle_name,
    INN = excluded.inn,
    KPP = excluded.kpp,
    OGRN = excluded.kpp,
    OKPO = excluded.okpo,
    create_date = excluded.create_date,
    last_modified_date = excluded.last_modified_date
    returning partner.uuid PARTNER_UUID
    ),
-- T_RENTER -> ACCOUNT
    INSERT_INTO_ACCOUNT as (
insert into ACCOUNT (UUID,
              PARTNER_UUID,
              DIGITAL_ID,
              account,
              STATE,
              create_date,
              last_modified_date,
              priority_account)
select generate_uuid(),
    UUID::UUID,
    DIGITALID,
    ACCOUNT,
    'NOT_SIGNED',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'true'
from RENTER_ROWS
where bankbic is not null
   or account is not null
    returning ACCOUNT.UUID as ACCOUNT_UUID
    , account.partner_uuid as ACCOUNT_PARTNER_UUID
    )
    ,
-- T_RENTER -> BANK
    INSERT_INTO_BANK as (
insert
into BANK (uuid,
           ACCOUNT_UUID,
           NAME,
           BIC,
           intermediary)
select generate_uuid(),
    INSERT_INTO_ACCOUNT.ACCOUNT_UUID,
    BANKNAME,
    BANKBIC,
    'false'
from INSERT_INTO_ACCOUNT
    left join RENTER_ROWS
on INSERT_INTO_ACCOUNT.ACCOUNT_PARTNER_UUID = RENTER_ROWS.uuid::uuid
where bankbic is not null
  and ACCOUNT_UUID is not null
    returning BANK.UUID as BANK_UUID
    , bank.account_uuid as ACCOUNT_UUID
    )
    ,
-- T_RENTER -> BANK_ACCOUNT
    INSERT_INTO_BANK_ACCOUNT as (
insert
into BANK_ACCOUNT (uuid,
                   BANK_UUID,
                   ACCOUNT)
select generate_uuid(),
    INSERT_INTO_BANK.BANK_UUID,
    bankaccount
from INSERT_INTO_BANK
    left join INSERT_INTO_ACCOUNT
on INSERT_INTO_BANK.ACCOUNT_UUID = INSERT_INTO_ACCOUNT.ACCOUNT_UUID
    left join RENTER_ROWS on INSERT_INTO_ACCOUNT.ACCOUNT_PARTNER_UUID = RENTER_ROWS.uuid::uuid
where bankaccount is not null
  and BANK_UUID is not null
    returning bank_account.uuid BANK_ACCOUNT_UUID
    , bank_account.bank_uuid BANK_UUID
    )
    ,
-- T_LEGALADDRESS -> ADDRESS
    INSERT_INTO_LEGALADDRESS as (
insert
into ADDRESS (uuid,
              digital_id,
              unified_uuid,
              TYPE,
              ZIP_CODE,
              REGION,
              region_code,
              CITY,
              LOCATION,
              STREET,
              BUILDING,
              BUILDING_BLOCK,
              FLAT)
select generate_uuid(),
    digitalid,
    RENTER_ROWS.uuid::uuid,
    'LEGAL_ADDRESS',
    ZIPCODE,
    REGION,
    regioncode,
    CITY,
    LOCALITY,
    STREET,
    BUILDING,
    BUILDINGBLOCK,
    FLAT
from RENTER_ROWS
    left join T_LEGALADDRESS
on t_legaladdress.renter_id = RENTER_ROWS.object_id
    returning address.uuid LEGAL_ADDRESS_UUID, address.unified_uuid LEGAL_ADDRESS_PARTNER_UUID
    ),
-- T_PHYSICALADDRESS -> ADDRESS
    INSERT_INTO_PHUSICALADDRESS as (
insert
into ADDRESS (UUID,
              digital_id,
              unified_uuid,
              TYPE,
              ZIP_CODE,
              REGION,
              region_code,
              CITY,
              LOCATION,
              STREET,
              BUILDING,
              BUILDING_BLOCK,
              FLAT)
select generate_uuid(),
    digitalid,
    RENTER_ROWS.uuid::uuid,
    'PHYSICAL_ADDRESS',
    ZIPCODE,
    REGION,
    regioncode,
    CITY,
    LOCALITY,
    STREET,
    BUILDING,
    BUILDINGBLOCK,
    FLAT
from RENTER_ROWS
    left join T_PHYSICALADDRESS
on T_PHYSICALADDRESS.renter_id = RENTER_ROWS.object_id
    returning address.uuid PHYSICAL_ADDRESS_UUID, address.unified_uuid PHYSICAL_ADDRESS_PARTNER_UUID
    ),
-- T_PARTNER -> EMAIL
    INSERT_INTO_EMAIL as
    (
insert
into email (uuid,
            digital_id,
            unified_uuid,
            email)
select generate_uuid(),
    digitalid,
    uuid::uuid,
    emails
from RENTER_ROWS
where emails is not null
    returning email.uuid EMAIL_UUID
    , email.unified_uuid EMAIL_UNIFIED_UUID
    )
    ,
-- T_PARTNER -> PHONE
    INSERT_INTO_PHONE as
    (
insert
into phone (uuid,
            digital_id,
            unified_uuid,
            phone)
select generate_uuid(),
    digitalid,
    uuid::uuid,
    phonenumbers
from RENTER_ROWS
where phonenumbers is not null
    returning phone.uuid PHONE_UUID
    , phone.unified_uuid PHONE_UNIFIED_UUID
    )
    ,
-- T_RENTER -> DOCUMENT
    INSERT_INTO_DOCUMENT as
    (
insert
into DOCUMENT (UUID,
               digital_id,
               unified_uuid,
               type_uuid,
               SERIES,
               NUMBER,
               DATE_ISSUE,
               DIVISION_ISSUE,
               DIVISION_CODE)
select generate_uuid(),
    digitalid,
    UUID::UUID,
    case DULTYPE
    when 'PASSPORTOFRUSSIA' then cast ('3422aec8-7f44-4089-9a43-f8e3c5b00722' as uuid)
    when 'SEAMANPASSPORT' then cast ('8a4d4464-64a1-4f3d-ab86-fd3be614f7a2' as uuid)
    when 'SERVICEMANIDENTITYCARDOFRUSSIA'then cast ('86257be3-aa4d-4a98-a957-9c55c2d6c860' as uuid)
    when 'FOREIGNPASSPORT' then cast ('282561c6-a218-46a4-b47a-320192bdba6e' as uuid)
    when 'SERVICEPASSPORTOFRUSSIA' then cast ('944ceae7-537b-402e-87b6-d5eec2c888b6' as uuid)
    when 'RFCITIZENDIPLOMATICPASSPORT' then cast ('9e345e55-12ed-42d9-ad6b-dd5c4ea31407' as uuid)
    when 'PASSPORTOFRUSSIAWITHCHIP' then cast ('7540214c-c2ef-451a-a870-8a1ab9b7d514' as uuid)
    end,
    DULSERIE,
    DULNUMBER,
    DULDATEISSUE,
    DULDIVISIONISSUE,
    DULDIVISIONCODE
from RENTER_ROWS
where dultype is not null
   or dulserie is not null
   or dulnumber is not null
    returning document.uuid DOCUMENT_UUID
    , document.unified_uuid DOCUMENT_UNIFIED_UUID
    )
-- Ссылки на созданные объекты рендера в плоской структуре
insert
into flat_renter (uuid,
                  partner_uuid,
                  document_uuid,
                  account_uuid,
                  bank_uuid,
                  bank_account_uuid,
                  email_uuid,
                  legal_address_uuid,
                  physical_address_uuid,
                  phone_uuid)
select generate_uuid(),
       INSERT_INTO_PARTNER.PARTNER_UUID,
       INSERT_INTO_DOCUMENT.DOCUMENT_UUID,
       INSERT_INTO_ACCOUNT.ACCOUNT_UUID,
       INSERT_INTO_BANK.BANK_UUID,
       INSERT_INTO_BANK_ACCOUNT.BANK_ACCOUNT_UUID,
       INSERT_INTO_EMAIL.EMAIL_UUID,
       INSERT_INTO_LEGALADDRESS.LEGAL_ADDRESS_UUID,
       INSERT_INTO_PHUSICALADDRESS.PHYSICAL_ADDRESS_UUID,
       INSERT_INTO_PHONE.PHONE_UUID
from INSERT_INTO_PARTNER
         left join INSERT_INTO_ACCOUNT on INSERT_INTO_ACCOUNT.ACCOUNT_PARTNER_UUID = INSERT_INTO_PARTNER.PARTNER_UUID
         left join INSERT_INTO_BANK on INSERT_INTO_ACCOUNT.ACCOUNT_UUID = INSERT_INTO_BANK.ACCOUNT_UUID
         left join INSERT_INTO_BANK_ACCOUNT on INSERT_INTO_BANK.BANK_UUID = INSERT_INTO_BANK_ACCOUNT.BANK_UUID
         left join INSERT_INTO_EMAIL on INSERT_INTO_PARTNER.PARTNER_UUID = INSERT_INTO_EMAIL.EMAIL_UNIFIED_UUID
         left join INSERT_INTO_DOCUMENT on INSERT_INTO_PARTNER.PARTNER_UUID = INSERT_INTO_DOCUMENT.DOCUMENT_UNIFIED_UUID
         left join INSERT_INTO_PHONE on INSERT_INTO_PARTNER.PARTNER_UUID = INSERT_INTO_PHONE.PHONE_UNIFIED_UUID
         left join INSERT_INTO_LEGALADDRESS on INSERT_INTO_PARTNER.PARTNER_UUID = INSERT_INTO_LEGALADDRESS.LEGAL_ADDRESS_PARTNER_UUID
         left join INSERT_INTO_PHUSICALADDRESS on INSERT_INTO_PARTNER.PARTNER_UUID = INSERT_INTO_PHUSICALADDRESS.PHYSICAL_ADDRESS_PARTNER_UUID ON CONFLICT (partner_uuid) DO
UPDATE
    SET uuid = excluded.uuid,
    partner_uuid = excluded.partner_uuid,
    document_uuid = excluded.document_uuid,
    account_uuid = excluded.account_uuid,
    bank_uuid = excluded.bank_uuid,
    bank_account_uuid = excluded.bank_account_uuid,
    email_uuid = excluded.email_uuid,
    legal_address_uuid = excluded.legal_address_uuid,
    physical_address_uuid = excluded.physical_address_uuid,
    phone_uuid = excluded.phone_uuid;


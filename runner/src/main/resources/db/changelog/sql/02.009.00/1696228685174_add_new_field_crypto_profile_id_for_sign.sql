-- liquibase formatted sql
-- changeset 21293375:1696228685174_add_new_field_crypto_profile_id_for_sign

ALTER TABLE SIGN
    ADD COLUMN crypto_profile_id UUID;

COMMENT ON COLUMN SIGN.CRYPTO_PROFILE_ID IS 'Идентификатор криптопрофиля';

-- rollback alter table sign drop column crypto_profile_id;

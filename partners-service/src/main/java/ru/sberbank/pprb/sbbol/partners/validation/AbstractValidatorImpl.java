package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.exception.ModelValidationException;

abstract class AbstractValidatorImpl<T> implements Validator<T> {

    protected static final int ZIP_CODE_MAX_LENGTH_VALIDATION = 6;
    protected static final int REGION_MAX_LENGTH_VALIDATION = 50;
    protected static final int REGION_CODE_MAX_LENGTH_VALIDATION = 10;
    protected static final int CITY_MAX_LENGTH_VALIDATION = 300;
    protected static final int LOCATION_MAX_LENGTH_VALIDATION = 300;
    protected static final int STREET_MAX_LENGTH_VALIDATION = 300;
    protected static final int BUILDING_MAX_LENGTH_VALIDATION = 100;
    protected static final int BUILDING_BLOCK_MAX_LENGTH_VALIDATION = 20;
    protected static final int FLAT_MAX_LENGTH_VALIDATION = 20;

    protected static final int ORG_NAME_MAX_LENGTH_VALIDATION = 350;
    protected static final int FIRST_NAME_MAX_LENGTH_VALIDATION = 50;
    protected static final int SECOND_NAME_MAX_LENGTH_VALIDATION = 50;
    protected static final int MIDDLE_NAME_MAX_LENGTH_VALIDATION = 50;
    protected static final int POSITION_NAME_MAX_LENGTH_VALIDATION = 100;

    protected static final int SERIES_MAX_LENGTH_VALIDATION = 50;
    protected static final int NUMBER_MAX_LENGTH_VALIDATION = 50;
    protected static final int DIVISION_ISSUE_MAX_LENGTH_VALIDATION = 250;
    protected static final int DIVISION_CODE_MAX_LENGTH_VALIDATION = 50;
    protected static final int CERTIFIER_NAME_MAX_LENGTH_VALIDATION = 100;
    protected static final int POSITION_CERTIFIER_MAX_LENGTH_VALIDATION = 100;

    protected static final int COMMENT_MAX_LENGTH_VALIDATION = 50;
    protected static final int BANK_NAME_MAX_LENGTH_VALIDATION = 160;

    protected static final int COMMENT_PARTNER_MAX_LENGTH_VALIDATION = 255;
    protected static final int OKPO_PARTNER_MAX_LENGTH_VALIDATION = 14;
    protected static final int OKPO_PARTNER_MIN_LENGTH_VALIDATION = 8;
    protected static final int OGRN_PARTNER_MAX_LENGTH_VALIDATION = 15;
    protected static final int OGRN_PARTNER_MIN_LENGTH_VALIDATION = 13;

    private static final int ID_LENGTH = 36;
    private static final int DIGITAL_ID_MAX_LENGTH = 40;

    protected static final String DEFAULT_MESSAGE_FIELDS_IS_LENGTH = "default.fields.length";
    protected static final String DEFAULT_MESSAGE_FIELD_IS_NULL = "default.field.is_null";
    protected static final String DEFAULT_MESSAGE_FIELDS_IS_NULL = "default.fields.is_null";
    protected static final String DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER = "default.field.control_number";
    protected static final String DEFAULT_MESSAGE_INN_LENGTH = "partner.inn_length";
    protected static final String DEFAULT_MESSAGE_FIELDS_DUPLICATION = "default.fields.duplication";

    protected void commonValidationDigitalId(String digitalId) {
        if (StringUtils.isEmpty(digitalId)) {
            throw new ModelValidationException("Поля обязательны для заполнения digitalId");
        } else if (StringUtils.isNotEmpty(digitalId) && digitalId.length() > DIGITAL_ID_MAX_LENGTH) {
            throw new ModelValidationException("digitalId допустимая длина от 1 до 40 символов");
        }
    }

    protected void commonValidationUuid(String... uuid) {
        for (String id : uuid) {
            if (StringUtils.isEmpty(id)) {
                throw new ModelValidationException("Поля обязательны для заполнения id/partnerId/unifiedId/documentTypeId");
            } else if (StringUtils.isNotEmpty(id) && id.length() != ID_LENGTH) {
                throw new ModelValidationException("Ошибка заполнения одного из полей id/partnerId/unifiedId/documentTypeId длина значения не равна 36");
            }
        }
    }
}

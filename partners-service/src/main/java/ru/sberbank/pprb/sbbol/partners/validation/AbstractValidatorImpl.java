package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

import java.util.List;

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
    private static final String DOCUMENT = "partnerId/unifiedId/id";

    protected static final String DEFAULT_MESSAGE_FIELDS_IS_LENGTH = "default.fields.length";
    protected static final String DEFAULT_MESSAGE_FIELD_IS_NULL = "default.field.is_null";
    protected static final String DEFAULT_MESSAGE_FIELDS_IS_NULL = "default.fields.is_null";
    protected static final String DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER = "default.field.control_number";
    protected static final String DEFAULT_MESSAGE_FIELDS_DUPLICATION = "default.fields.duplication";
    protected static final String DEFAULT_MESSAGE_VERSION_ERROR = "default.fields.object_version_error";
    protected static final String DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR = "default.fields.object_not_found";


    protected void commonValidationDigitalId(List<String> errors, String digitalId) {
        if (StringUtils.isEmpty(digitalId)) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "digitalId"));
        } else if (StringUtils.isNotEmpty(digitalId) && digitalId.length() > DIGITAL_ID_MAX_LENGTH) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "digitalId", "1", "40"));
        }
    }

    protected void commonValidationUuid(List<String> errors, String... uuid) {
        commonValidationUuid(errors, List.of(uuid));
    }

    protected void commonValidationUuid(List<String> errors, List<String> uuid) {
        if (!CollectionUtils.isEmpty(uuid)) {
            for (String id : uuid) {
                if (StringUtils.isEmpty(id)) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, DOCUMENT));
                } else if (StringUtils.isNotEmpty(id) && id.length() != ID_LENGTH) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, DOCUMENT, "36", "36"));
                }
            }
        } else {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, DOCUMENT));
        }
    }
}

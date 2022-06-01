package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;

import java.util.List;

public class DocumentCreateValidationImpl extends AbstractValidatorImpl<DocumentCreate> {

    @Override
    public void validator(List<String> errors, DocumentCreate entity) {
        commonValidationDigitalId(entity.getDigitalId());
        commonValidationUuid(entity.getUnifiedId(), entity.getDocumentTypeId());
        if (StringUtils.isNotEmpty(entity.getSeries()) && entity.getSeries().length() > SERIES_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "series", "1", "50"));
        }
        if (StringUtils.isNotEmpty(entity.getNumber()) && entity.getNumber().length() > NUMBER_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, " number", "1", "50"));
        }
        if (StringUtils.isNotEmpty(entity.getDivisionIssue()) && entity.getDivisionIssue().length() > DIVISION_ISSUE_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, " divisionIssue", "1", "250"));
        }
        if (StringUtils.isNotEmpty(entity.getDivisionCode()) && entity.getDivisionCode().length() > DIVISION_CODE_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, " divisionCode", "1", "50"));
        }
        if (StringUtils.isNotEmpty(entity.getCertifierName()) && entity.getCertifierName().length() > CERTIFIER_NAME_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, " certifierName", "1", "100"));
        }
        if (StringUtils.isNotEmpty(entity.getPositionCertifier()) && entity.getPositionCertifier().length() > POSITION_CERTIFIER_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, " positionCertifier", "1", "100"));
        }
    }
}

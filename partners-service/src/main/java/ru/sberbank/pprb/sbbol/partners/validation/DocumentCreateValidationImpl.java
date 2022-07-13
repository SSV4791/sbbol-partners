package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class DocumentCreateValidationImpl extends AbstractValidatorImpl<DocumentCreate> {

    public static final String DEFAULT_MESSAGE_ERROR_DOCUMENT_TYPE = "document.documentTypeId_valid";

    private final DocumentDictionaryRepository documentDictionaryRepository;

    public DocumentCreateValidationImpl(DocumentDictionaryRepository documentDictionaryRepository) {
        this.documentDictionaryRepository = documentDictionaryRepository;
    }

    @Override
    public void validator(Map<String, List<String>> errors, DocumentCreate entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getUnifiedId(), entity.getDocumentTypeId());
        if (isNotEmpty(entity.getDocumentTypeId())) {
            var foundDocumentDictionary = documentDictionaryRepository.getByUuid(UUID.fromString(entity.getDocumentTypeId()));
            if (foundDocumentDictionary.isEmpty()) {
                setError(errors, "documentType", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ERROR_DOCUMENT_TYPE));
            }
        } else {
            setError(errors, "documentType", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "тип документа"));
        }
        if (StringUtils.isNotEmpty(entity.getSeries()) && entity.getSeries().length() > SERIES_MAX_LENGTH_VALIDATION) {
            setError(errors, "series", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getNumber()) && entity.getNumber().length() > NUMBER_MAX_LENGTH_VALIDATION) {
            setError(errors, "number", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getDivisionIssue()) && entity.getDivisionIssue().length() > DIVISION_ISSUE_MAX_LENGTH_VALIDATION) {
            setError(errors, "divisionIssue", MessagesTranslator.toLocale(DEFAULT_LENGTH, "250"));
        }
        if (StringUtils.isNotEmpty(entity.getDivisionCode()) && entity.getDivisionCode().length() > DIVISION_CODE_MAX_LENGTH_VALIDATION) {
            setError(errors, "divisionCode", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getCertifierName()) && entity.getCertifierName().length() > CERTIFIER_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "certifierName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "100"));
        }
        if (StringUtils.isNotEmpty(entity.getPositionCertifier()) && entity.getPositionCertifier().length() > POSITION_CERTIFIER_MAX_LENGTH_VALIDATION) {
            setError(errors, "positionCertifier", MessagesTranslator.toLocale(DEFAULT_LENGTH, "100"));
        }
    }
}

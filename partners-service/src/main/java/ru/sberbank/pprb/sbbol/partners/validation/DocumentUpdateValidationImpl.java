package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.ModelValidationException;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class DocumentUpdateValidationImpl extends AbstractValidatorImpl<DocumentChange> {
    private static final String DEFAULT_MESSAGE_ERROR_DOCUMENT_TYPE = "document.documentTypeId_valid";
    public static final String DOCUMENT_NAME = "document";
    private final DocumentRepository documentRepository;
    private final DocumentDictionaryRepository documentDictionaryRepository;

    public DocumentUpdateValidationImpl(DocumentRepository documentRepository, DocumentDictionaryRepository documentDictionaryRepository) {
        this.documentRepository = documentRepository;
        this.documentDictionaryRepository = documentDictionaryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, DocumentChange entity) {
        var foundDocument = documentRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new ModelValidationException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getUnifiedId(), entity.getId());
        if (isNotEmpty(entity.getDocumentTypeId())) {
            var foundDocumentDictionary = documentDictionaryRepository.getByUuid(UUID.fromString(entity.getDocumentTypeId()));
            if (foundDocumentDictionary.isEmpty()) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_ERROR_DOCUMENT_TYPE, entity.getDocumentTypeId()));
            }
        }
        if (!entity.getVersion().equals(foundDocument.getVersion())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundDocument.getVersion().toString(), entity.getVersion().toString()));
        }
        if (entity.getVersion() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "version"));
        }
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

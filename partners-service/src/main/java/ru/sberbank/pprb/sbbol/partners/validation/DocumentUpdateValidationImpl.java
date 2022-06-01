package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.ModelValidationException;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

import java.util.List;
import java.util.UUID;

public class DocumentUpdateValidationImpl extends AbstractValidatorImpl<DocumentChange> {
    public static final String DOCUMENT_NAME = "document";
    private final DocumentRepository documentRepository;

    public DocumentUpdateValidationImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, DocumentChange entity) {
        commonValidationDigitalId(entity.getDigitalId());
        commonValidationUuid(entity.getUnifiedId(), entity.getId());
        var foundDocument = documentRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new ModelValidationException("Не найден объект" + DOCUMENT_NAME + entity.getDigitalId() + entity.getId()));
        if (!entity.getVersion().equals(foundDocument.getVersion())) {
            throw new OptimisticLockingFailureException("Версия записи в базе данных " + foundDocument.getVersion() +
                " не равна версии записи в запросе version=" + entity.getVersion());
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

package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseEmailValidation.commonValidationChildEmail;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class EmailUpdateValidationImpl extends AbstractValidatorImpl<Email> {
    private static final String DOCUMENT_NAME = "document";
    private final EmailRepository emailRepository;

    public EmailUpdateValidationImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(Map<String, List<String>> errors, Email entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getUnifiedId(), entity.getId());
        var uuid = UUID.fromString(entity.getId());
        var foundEmail = emailRepository.getByDigitalIdAndUuid(entity.getDigitalId(), uuid)
            .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        if (!entity.getVersion().equals(foundEmail.getVersion())) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundEmail.getVersion().toString(), entity.getVersion().toString()));
        }
        if (entity.getEmail().equals(EMPTY)) {
            setError(errors, "email", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "email"));
        }
        if (StringUtils.isNotEmpty(entity.getEmail())) {
            commonValidationChildEmail(errors, entity.getEmail());
        }
        if (entity.getVersion() == null) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "version"));
        }
    }
}

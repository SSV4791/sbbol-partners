package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseEmailValidation.commonValidationChildEmail;


public class EmailUpdateValidationImpl extends AbstractValidatorImpl<Email> {
    private static final String DOCUMENT_NAME = "document";
    private final EmailRepository emailRepository;

    public EmailUpdateValidationImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, Email entity) {
        commonValidationDigitalId(entity.getDigitalId());
        commonValidationUuid(entity.getUnifiedId(), entity.getId());
        var uuid = UUID.fromString(entity.getId());
        var foundEmail = emailRepository.getByDigitalIdAndUuid(entity.getDigitalId(), uuid)
            .orElseThrow(() -> new MissingValueException("Не найден объект " + DOCUMENT_NAME + uuid));
        if (!entity.getVersion().equals(foundEmail.getVersion())) {
            throw new OptimisticLockingFailureException("Версия записи в базе данных " + foundEmail.getVersion() +
                " не равна версии записи в запросе version=" + entity.getVersion());
        }
        if (entity.getEmail().equals(EMPTY)) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "email"));
        }
        if (StringUtils.isNotEmpty(entity.getEmail())) {
            commonValidationChildEmail(errors, entity.getEmail());
        }
        if (entity.getVersion() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "version"));
        }
    }
}

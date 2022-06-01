package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePhoneValidation.commonValidationChildPhone;

public class PhoneUpdateValidationImpl extends AbstractValidatorImpl<Phone> {
    private static final String DOCUMENT_NAME = "phone";
    private final PhoneRepository phoneRepository;

    public PhoneUpdateValidationImpl(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, Phone entity) {
        commonValidationDigitalId(entity.getDigitalId());
        commonValidationUuid(entity.getUnifiedId(), entity.getId());
        var uuid = UUID.fromString(entity.getId());
        var foundPhone = phoneRepository.getByDigitalIdAndUuid(entity.getDigitalId(), uuid)
            .orElseThrow(() -> new MissingValueException("Не найден объект " + DOCUMENT_NAME + " " + uuid));
        if (!entity.getVersion().equals(foundPhone.getVersion())) {
            throw new OptimisticLockingFailureException("Версия записи в базе данных " + foundPhone.getVersion() +
                " не равна версии записи в запросе version=" + entity.getVersion());
        }
        if (entity.getPhone().equals(EMPTY)) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "phone"));
        }
        if (StringUtils.isNotEmpty(entity.getPhone())) {
            commonValidationChildPhone(errors, entity.getPhone());
        }
        if (entity.getVersion() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "version"));
        }
    }
}

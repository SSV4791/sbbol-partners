package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.StringUtils;
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
        var foundPhone = phoneRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getUnifiedId(), entity.getId());
        if (!entity.getVersion().equals(foundPhone.getVersion())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundPhone.getVersion().toString(), entity.getVersion().toString()));
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

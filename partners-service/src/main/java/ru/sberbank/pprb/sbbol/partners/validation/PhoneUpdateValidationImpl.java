package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;

import java.util.Map;

import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePhoneValidation.commonValidationChildPhone;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class PhoneUpdateValidationImpl extends AbstractValidatorImpl<Phone> {
    private static final String DOCUMENT_NAME = "phone";
    private final PhoneRepository phoneRepository;

    public PhoneUpdateValidationImpl(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(Map<String, List<String>> errors, Phone entity) {
        var foundPhone = phoneRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getUnifiedId(), entity.getId());
        if (!Objects.equals(entity.getVersion(), foundPhone.getVersion())) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundPhone.getVersion(), entity.getVersion()));
        }
        if (entity.getPhone().equals(EMPTY)) {
            setError(errors, "phone", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "номер телефона"));
        }
        if (StringUtils.isNotEmpty(entity.getPhone())) {
            commonValidationChildPhone(errors, entity.getPhone());
        }
        if (entity.getVersion() == null) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "version"));
        }
    }
}

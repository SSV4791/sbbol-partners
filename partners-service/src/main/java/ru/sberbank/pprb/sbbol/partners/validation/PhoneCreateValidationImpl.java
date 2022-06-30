package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import java.util.Map;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;

import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePhoneValidation.commonValidationChildPhone;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class PhoneCreateValidationImpl extends AbstractValidatorImpl<PhoneCreate> {

    @Override
    public void validator(Map<String, List<String>> errors, PhoneCreate entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getUnifiedId());
        if (isEmpty(entity.getPhone())) {
            setError(errors, "phone", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "номер телефона"));
        } else {
            commonValidationChildPhone(errors, entity.getPhone());
        }
    }
}

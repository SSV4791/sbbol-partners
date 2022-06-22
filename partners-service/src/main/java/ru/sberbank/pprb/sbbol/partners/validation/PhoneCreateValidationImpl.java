package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;

import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePhoneValidation.commonValidationChildPhone;

public class PhoneCreateValidationImpl extends AbstractValidatorImpl<PhoneCreate> {

    @Override
    public void validator(List<String> errors, PhoneCreate entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getUnifiedId());
        if (isEmpty(entity.getPhone())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "phone"));
        } else {
            commonValidationChildPhone(errors, entity.getPhone());
        }
    }
}

package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseEmailValidation.commonValidationChildEmail;

public class EmailCreateValidationImpl extends AbstractValidatorImpl<EmailCreate> {

    @Override
    public void validator(List<String> errors, EmailCreate entity) {
        commonValidationDigitalId(errors,entity.getDigitalId());
        commonValidationUuid(errors,entity.getUnifiedId());
        if (StringUtils.isEmpty(entity.getEmail())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "email"));
        } else {
            commonValidationChildEmail(errors, entity.getEmail());
        }
    }
}

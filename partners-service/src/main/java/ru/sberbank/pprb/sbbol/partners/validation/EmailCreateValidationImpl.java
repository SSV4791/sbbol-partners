package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import java.util.Map;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseEmailValidation.commonValidationChildEmail;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class EmailCreateValidationImpl extends AbstractValidatorImpl<EmailCreate> {

    @Override
    public void validator(Map<String, List<String>> errors, EmailCreate entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getUnifiedId());
        if (StringUtils.isEmpty(entity.getEmail())) {
            setError(errors, "email", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "Email"));
        } else {
            commonValidationChildEmail(errors, entity.getEmail());
        }
    }
}

package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;

import java.util.List;

public class EmailCreateValidation implements Validator<EmailCreate> {

    @Override
    public void validation(List<String> errors, EmailCreate entity) {
        if (entity.getUnifiedId() == null) {
            errors.add(MessagesTranslator.toLocale("default.field.is_null", "unifiedId"));
        }
    }
}

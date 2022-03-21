package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Email;

import java.util.List;

public class EmailValidation implements Validator<Email> {

    @Override
    public void validation(List<String> errors, Email entity) {
        if (entity.getId() == null) {
            errors.add(MessagesTranslator.toLocale("default.field.is_null", "id"));
        }
        if (entity.getUnifiedId() == null) {
            errors.add(MessagesTranslator.toLocale("default.field.is_null", "unifiedId"));
        }
    }
}

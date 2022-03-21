package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import java.util.List;

public class PhoneValidation implements Validator<Phone> {

    @Override
    public void validation(List<String> errors, Phone entity) {
        if (entity.getId() == null) {
            errors.add(MessagesTranslator.toLocale("default.field.is_null", "id"));
        }
        if (entity.getUnifiedId() == null) {
            errors.add(MessagesTranslator.toLocale("default.field.is_null", "unifiedId"));
        }
    }
}

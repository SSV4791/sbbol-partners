package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Contact;

import java.util.List;

public class ContactValidation implements Validator<Contact> {

    @Override
    public void validation(List<String> errors, Contact entity) {
        if (entity.getId() == null) {
            errors.add(MessagesTranslator.toLocale("default.field.is_null", "id"));
        }
        if (entity.getPartnerId() == null) {
            errors.add(MessagesTranslator.toLocale("default.field.is_null", "partnerId"));
        }
    }
}

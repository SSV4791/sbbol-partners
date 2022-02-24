package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Contact;

import java.util.Collections;
import java.util.List;

public class ContactValidation implements Validator<Contact> {

    @Override
    public List<String> validation(Contact entity) {
        if (entity.getPartnerId() == null) {
            return List.of(MessagesTranslator.toLocale("default.field.is_null", "partnerId"));
        }
        return Collections.emptyList();
    }
}

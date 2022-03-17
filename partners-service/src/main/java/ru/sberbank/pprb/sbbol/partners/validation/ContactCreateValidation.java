package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;

import java.util.Collections;
import java.util.List;

public class ContactCreateValidation implements Validator<ContactCreate> {

    @Override
    public List<String> validation(ContactCreate entity) {
        if (entity.getPartnerId() == null) {
            return List.of(MessagesTranslator.toLocale("default.field.is_null", "partnerId"));
        }
        return Collections.emptyList();
    }
}

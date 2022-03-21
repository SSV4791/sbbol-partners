package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;

import java.util.List;

public class ContactCreateValidation implements Validator<ContactCreate> {

    @Override
    public void validation(List<String> errors, ContactCreate entity) {
        if (entity.getPartnerId() == null) {
            errors.add(MessagesTranslator.toLocale("default.field.is_null", "partnerId"));
        }
    }
}

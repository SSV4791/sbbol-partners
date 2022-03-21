package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;

import java.util.List;

public class AddressCreateValidation implements Validator<AddressCreate> {

    @Override
    public void validation(List<String> errors, AddressCreate entity) {
        if (entity.getUnifiedId() == null) {
            errors.add(MessagesTranslator.toLocale("default.field.is_null", "unifiedId"));
        }
    }
}

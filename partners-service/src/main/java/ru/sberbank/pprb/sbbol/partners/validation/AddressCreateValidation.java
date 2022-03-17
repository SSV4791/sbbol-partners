package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;

import java.util.Collections;
import java.util.List;

public class AddressCreateValidation implements Validator<AddressCreate> {

    @Override
    public List<String> validation(AddressCreate entity) {
        if (entity.getUnifiedId() == null) {
            return List.of(MessagesTranslator.toLocale("default.field.is_null", "unifiedId"));
        }
        return Collections.emptyList();
    }
}

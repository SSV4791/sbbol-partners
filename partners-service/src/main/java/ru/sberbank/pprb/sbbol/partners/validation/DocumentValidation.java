package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;

import java.util.List;

public class DocumentValidation implements Validator<DocumentChange> {

    public static final String DEFAULT_FIELD_IS_NULL = "default.field.is_null";

    @Override
    public void validation(List<String> errors, DocumentChange entity) {
        if (entity.getId() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "id"));
        }
        if (entity.getVersion() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "verion"));
        }
        if (entity.getUnifiedId() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "unifiedId"));
        }
    }
}

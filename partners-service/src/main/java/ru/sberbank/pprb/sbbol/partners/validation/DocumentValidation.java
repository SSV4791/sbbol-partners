package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Document;

import java.util.Collections;
import java.util.List;

public class DocumentValidation implements Validator<Document> {

    @Override
    public List<String> validation(Document entity) {
        if (entity.getUnifiedId() == null) {
            return List.of(MessagesTranslator.toLocale("default.field.is_null", "unifiedId"));
        }
        return Collections.emptyList();
    }
}

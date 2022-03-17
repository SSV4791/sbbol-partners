package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;

import java.util.Collections;
import java.util.List;

public class DocumentCreateValidation implements Validator<DocumentCreate> {

    @Override
    public List<String> validation(DocumentCreate entity) {
        if (entity.getUnifiedId() == null) {
            return List.of(MessagesTranslator.toLocale("default.field.is_null", "unifiedId"));
        }
        return Collections.emptyList();
    }
}

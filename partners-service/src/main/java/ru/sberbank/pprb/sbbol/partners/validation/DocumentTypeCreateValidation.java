package ru.sberbank.pprb.sbbol.partners.validation;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeCreate;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DocumentTypeCreateValidation implements Validator<DocumentTypeCreate> {

    @Override
    public void validation(List<String> errors, DocumentTypeCreate entity) {
        if (CollectionUtils.isEmpty(entity.getLegalForms())) {
            errors.add(MessagesTranslator.toLocale("default.field.is_null", "legalForms"));
        }
        if (!CollectionUtils.isEmpty(entity.getLegalForms())) {
            var legalFormMap = entity.getLegalForms().stream()
                .collect(Collectors.toMap(Function.identity(), value -> 1, Integer::sum));
            legalFormMap.forEach((key, value) -> {
                if (value > 1) {
                    errors.add(MessagesTranslator.toLocale("default.fields.duplication", "legalForms", key.getValue()));
                }
            });
        }
    }
}

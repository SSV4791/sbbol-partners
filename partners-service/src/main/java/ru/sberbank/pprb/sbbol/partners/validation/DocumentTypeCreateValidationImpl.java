package ru.sberbank.pprb.sbbol.partners.validation;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeCreate;
import java.util.Map;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class DocumentTypeCreateValidationImpl extends AbstractValidatorImpl<DocumentTypeCreate> {

    @Override
    public void validator(Map<String, List<String>> errors, DocumentTypeCreate entity) {
        if (CollectionUtils.isEmpty(entity.getLegalForms())) {
            setError(errors, "legalForms", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "правовую форму к которой принадлежит документ"));
        }
        if (!CollectionUtils.isEmpty(entity.getLegalForms())) {
            var legalFormMap = entity.getLegalForms().stream()
                .collect(Collectors.toMap(Function.identity(), value -> 1, Integer::sum));
            legalFormMap.forEach((key, value) -> {
                if (value > 1) {
                    setError(errors, "legalForms", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_DUPLICATION, "legalForms", key.getValue()));
                }
            });
        }
    }
}

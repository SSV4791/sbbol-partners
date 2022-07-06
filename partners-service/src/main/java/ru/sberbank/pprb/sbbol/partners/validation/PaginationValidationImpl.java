package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import java.util.Map;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class PaginationValidationImpl extends AbstractValidatorImpl<Pagination> {
    public void validator(Map<String, List<String>> errors, Pagination pagination) {
        if (pagination.getCount() == null) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "pagination.count"));
        }
        if (pagination.getOffset() == null) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "pagination.offset"));
        }
    }
}

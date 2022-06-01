package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

public class PaginationValidationImpl extends AbstractValidatorImpl<Pagination> {
    public void validator(List<String> errors, Pagination pagination) {
        if (pagination.getCount() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "pagination.count"));
        }
        if (pagination.getOffset() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "pagination.offset"));
        }
    }
}

package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.ModelValidationException;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

public class EmailsFilterValidationImpl extends AbstractValidatorImpl<EmailsFilter> {
    private final Validator<Pagination> paginationValidator;

    public EmailsFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(List<String> errors, EmailsFilter entity) {
        commonValidationDigitalId(errors,entity.getDigitalId());
        if (!isEmpty(entity.getUnifiedIds())) {
            for (var unifiedId : entity.getUnifiedIds()) {
                commonValidationUuid(errors,unifiedId);
            }
        } else {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "unifiedIds"));
        }
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "pagination"));
        }
    }
}

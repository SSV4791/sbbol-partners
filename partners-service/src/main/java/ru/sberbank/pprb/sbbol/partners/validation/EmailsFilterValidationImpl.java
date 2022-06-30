package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import java.util.Map;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class EmailsFilterValidationImpl extends AbstractValidatorImpl<EmailsFilter> {
    private final Validator<Pagination> paginationValidator;

    public EmailsFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(Map<String, List<String>> errors, EmailsFilter entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getUnifiedIds());
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "pagination"));
        }
    }
}

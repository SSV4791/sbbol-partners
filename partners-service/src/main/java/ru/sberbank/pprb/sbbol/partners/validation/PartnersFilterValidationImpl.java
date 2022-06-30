package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import java.util.Map;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class PartnersFilterValidationImpl extends AbstractValidatorImpl<PartnersFilter> {
    private final Validator<Pagination> paginationValidator;

    public PartnersFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(Map<String, List<String>> errors, PartnersFilter entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "pagination"));
        }
    }
}

package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import java.util.Map;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class PhonesFilterValidationImpl extends AbstractValidatorImpl<PhonesFilter> {
    private final Validator<Pagination> paginationValidator;

    public PhonesFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(Map<String, List<String>> errors, PhonesFilter entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getUnifiedIds());
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "pagination"));
        }
    }
}

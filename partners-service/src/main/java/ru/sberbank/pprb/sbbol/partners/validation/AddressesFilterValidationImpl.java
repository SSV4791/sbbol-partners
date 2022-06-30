package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class AddressesFilterValidationImpl extends AbstractValidatorImpl<AddressesFilter> {
    private final Validator<Pagination> paginationValidator;

    public AddressesFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(Map<String, List<String>> errors, AddressesFilter entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (!isEmpty(entity.getUnifiedIds())) {
            for (var unifiedId : entity.getUnifiedIds()) {
                commonValidationUuid(errors, unifiedId);
            }
        } else {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "unifiedIds"));
        }
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "pagination"));
        }
    }
}

package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

public class AddressesFilterValidationImpl extends AbstractValidatorImpl<AddressesFilter> {
    private final Validator<Pagination> paginationValidator;

    public AddressesFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(List<String> errors, AddressesFilter entity) {
        commonValidationDigitalId(entity.getDigitalId());
        for (var unifiedId : entity.getUnifiedIds()) {
            commonValidationUuid(unifiedId);
        }
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "pagination"));
        }
    }
}

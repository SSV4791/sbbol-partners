package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;
import java.util.Map;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class ContactsFilterValidationImpl extends AbstractValidatorImpl<ContactsFilter> {
    private final Validator<Pagination> paginationValidator;

    public ContactsFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(Map<String, List<String>> errors, ContactsFilter entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getPartnerId());
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "pagination"));
        }
    }
}

package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

public class ContactsFilterValidationImpl extends AbstractValidatorImpl<ContactsFilter> {
    private final Validator<Pagination> paginationValidator;

    public ContactsFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(List<String> errors, ContactsFilter entity) {
        commonValidationDigitalId(entity.getDigitalId());
        commonValidationUuid(entity.getPartnerId());
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "pagination"));
        }
    }
}

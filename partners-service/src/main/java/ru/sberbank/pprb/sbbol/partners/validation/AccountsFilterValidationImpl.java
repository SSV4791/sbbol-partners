package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

public class AccountsFilterValidationImpl extends AbstractValidatorImpl<AccountsFilter> {
    private final Validator<Pagination> paginationValidator;

    public AccountsFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(List<String> errors, AccountsFilter entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (isEmpty(entity.getAccountIds()) && isEmpty(entity.getPartnerIds())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "getPartnerIds"));
        }
        if (!isEmpty(entity.getPartnerIds())) {
            for (var getPartnerId : entity.getPartnerIds()) {
                commonValidationUuid(errors,getPartnerId);
            }
        }
        if (!isEmpty(entity.getAccountIds())) {
            for (var getAccountIds : entity.getAccountIds()) {
                commonValidationUuid(errors,getAccountIds);
            }
        }
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "pagination"));
        }
    }
}
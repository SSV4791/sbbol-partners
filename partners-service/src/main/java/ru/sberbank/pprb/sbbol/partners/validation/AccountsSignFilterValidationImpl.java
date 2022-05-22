package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

public class AccountsSignFilterValidationImpl extends AbstractValidatorImpl<AccountsSignFilter> {
    private final Validator<Pagination> paginationValidator;

    public AccountsSignFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(List<String> errors, AccountsSignFilter entity) {
        commonValidationUuid(entity.getPartnerId());
        commonValidationDigitalId(entity.getDigitalId());
        for(var accountId : entity.getAccountsId()) {
            commonValidationUuid(accountId);
        }
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "pagination"));
        }
    }
}

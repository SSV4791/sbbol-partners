package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class AccountsSignFilterValidationImpl extends AbstractValidatorImpl<AccountsSignFilter> {
    private final Validator<Pagination> paginationValidator;

    public AccountsSignFilterValidationImpl(Validator<Pagination> paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public void validator(Map<String, List<String>> errors, AccountsSignFilter entity) {
        commonValidationDigitalId(errors,entity.getDigitalId());
        if (!isEmpty(entity.getAccountsId())) {
            for (var accountId : entity.getAccountsId()) {
                commonValidationUuid(errors,accountId);
            }
        }
        if(isNotEmpty(entity.getPartnerId())) {
            commonValidationUuid(errors,entity.getPartnerId());
        }
        if (!isNotEmpty(entity.getPartnerId()) && isEmpty(entity.getAccountsId())) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "partnerId/accountId"));
        }
        if (entity.getPagination() != null) {
            paginationValidator.validator(errors, entity.getPagination());
        } else {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "pagination"));
        }
    }
}

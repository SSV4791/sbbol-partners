package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;

import java.util.List;

public class AccountSignValidatorImpl extends AbstractValidatorImpl<AccountsSignInfo> {

    @Override
    public void validator(List<String> errors, AccountsSignInfo entity) {
        commonValidationDigitalId(entity.getDigitalId());
        for (var accountSign : entity.getAccountsSignDetail()) {
            commonValidationUuid(accountSign.getAccountId());
        }
    }
}

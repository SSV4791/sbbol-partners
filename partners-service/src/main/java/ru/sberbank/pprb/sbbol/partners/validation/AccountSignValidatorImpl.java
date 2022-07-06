package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.AccountSignViewRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class AccountSignValidatorImpl extends AbstractValidatorImpl<AccountsSignInfo> {
    private static final String DEFAULT_MESSAGE_ACCOUNT_SIGN_IS_TRUE = "account.account.sign.is_true";
    private final AccountSignViewRepository accountRepository;

    public AccountSignValidatorImpl(AccountSignViewRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void validator(Map<String, List<String>> errors, AccountsSignInfo entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (!isEmpty(entity.getAccountsSignDetail())) {
            var accountsId = entity.getAccountsSignDetail().stream()
                .map(AccountSignDetail::getAccountId).
                collect(Collectors.toList());
            commonValidationUuid(errors, accountsId);
            var accountsFilter = new AccountsSignFilter()
                .digitalId(entity.getDigitalId())
                .accountsId(accountsId);
            var viewAccount = accountRepository.findByFilter(accountsFilter);
            for (var account : viewAccount) {
                if (account.getUuid() != null) {
                    setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_SIGN_IS_TRUE, account.getUuid().toString()));
                }
            }
        }
    }
}

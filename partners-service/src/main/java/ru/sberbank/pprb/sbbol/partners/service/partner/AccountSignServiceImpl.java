package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSign;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignStatus;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class AccountSignServiceImpl implements AccountSignService {

    private final AccountRepository accountRepository;
    private final AccountSingMapper accountSingMapper;

    public AccountSignServiceImpl(AccountRepository accountRepository, AccountSingMapper accountSingMapper) {
        this.accountRepository = accountRepository;
        this.accountSingMapper = accountSingMapper;
    }

    @Override
    public AccountsSignResponse getAccountsSign(AccountsSignFilter filter) {
        var foundSignedAccounts = accountRepository.findByFilter(filter);
        var accountsSignResponse = new AccountsSignResponse();
        for (AccountEntity account : foundSignedAccounts) {
            accountsSignResponse.addAccountsSignItem(accountSingMapper.toSignAccount(account));
        }
        accountsSignResponse.setPagination(
            new Pagination()
                .offset(filter.getPagination().getOffset())
                .count(filter.getPagination().getCount())
        );
        return accountsSignResponse;
    }

    @Override
    public AccountsSignResponse updateAccountSign(AccountsSignStatus accountsSignStatus) {
        var errors = new ArrayList<Error>();
        var response = new AccountsSignResponse();
        for (AccountSign accountSign : accountsSignStatus.getAccountsSign()) {
            var account = accountRepository.getByDigitalIdAndUuid(accountSign.getDigitalId(), UUID.fromString(accountSign.getAccountId()));
            if (account.getState().name().equals(accountSign.getState().name())) {
                errors.add
                    (new Error()
                        .code("PPRB:PARTNER:SIGN_ACCOUNT_EXCEPTION")
                        .text(Collections.singletonList("Account " + account.getUuid() + "уже имеет статус " + account.getState()))
                    );
            }
            accountSingMapper.updateSignAccount(accountSign, account);
            var savedAccount = accountRepository.save(account);
            response.addAccountsSignItem(accountSingMapper.toSignAccount(savedAccount));
        }
        response.errors(errors);
        return response;
    }
}

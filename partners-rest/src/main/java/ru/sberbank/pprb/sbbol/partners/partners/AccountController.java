package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerAccountsApi;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountService;
import ru.sberbank.pprb.sbbol.partners.validation.AccountChangePriorityValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AccountCreateValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AccountUpdateValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AccountsFilterValidationImpl;

@RestController
public class AccountController implements PartnerAccountsApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<Account> change(@Validation(type = AccountChangePriorityValidationImpl.class)  AccountPriority accountPriority) {
        return ResponseEntity.ok(accountService.changePriority(accountPriority));
    }

    @Override
    public ResponseEntity<Account> create(@Validation(type = AccountCreateValidatorImpl.class) AccountCreate account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.saveAccount(account));
    }

    @Override
    public ResponseEntity<Void> delete(String id, String digitalId) {
        accountService.deleteAccount(id, digitalId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Account> getById(String id, String digitalId) {
        return ResponseEntity.ok(accountService.getAccount(id, digitalId));
    }

    @Override
    public ResponseEntity<AccountsResponse> list(@Validation(type = AccountsFilterValidationImpl.class) AccountsFilter accountsFilter) {
        return ResponseEntity.ok(accountService.getAccounts(accountsFilter));
    }

    @Override
    public ResponseEntity<Account> update(@Validation(type = AccountUpdateValidatorImpl.class) AccountChange account) {
        return ResponseEntity.ok(accountService.updateAccount(account));
    }
}

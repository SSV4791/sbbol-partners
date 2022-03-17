package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerAccountsApi;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountService;
import ru.sberbank.pprb.sbbol.partners.validation.PartnerAccountCreateValidator;
import ru.sberbank.pprb.sbbol.partners.validation.PartnerAccountValidator;

@RestController
public class AccountController implements PartnerAccountsApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<AccountResponse> change(AccountPriority accountPriority) {
        return ResponseEntity.ok(accountService.changePriority(accountPriority));
    }

    @Override
    public ResponseEntity<AccountResponse> create(@Validation(type = PartnerAccountCreateValidator.class) AccountCreate account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.saveAccount(account));
    }

    @Override
    public ResponseEntity<Void> delete(String id, String digitalId) {
        accountService.deleteAccount(id, digitalId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AccountResponse> getById(String id, String digitalId) {
        return ResponseEntity.ok(accountService.getAccount(id, digitalId));
    }

    @Override
    public ResponseEntity<AccountsResponse> list(AccountsFilter accountsFilter) {
        return ResponseEntity.ok(accountService.getAccounts(accountsFilter));
    }

    @Override
    public ResponseEntity<AccountResponse> update(@Validation(type = PartnerAccountValidator.class) AccountChange account) {
        return ResponseEntity.ok(accountService.updateAccount(account));
    }
}

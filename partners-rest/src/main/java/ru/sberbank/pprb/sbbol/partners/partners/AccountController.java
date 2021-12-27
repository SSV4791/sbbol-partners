package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerAccountsApi;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountService;

@RestController
public class AccountController implements PartnerAccountsApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<Error> delete(String id, String digitalId) {
        return ResponseEntity.ok(accountService.deleteAccount(id, digitalId));

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
    public ResponseEntity<AccountResponse> create(Account account) {
        return ResponseEntity.ok(accountService.saveAccount(account));
    }

    @Override
    public ResponseEntity<AccountResponse> update(Account account) {
        return ResponseEntity.ok(accountService.updateAccount(account));
    }
}

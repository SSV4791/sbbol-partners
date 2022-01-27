package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.AccountsSignApi;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignStatus;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountSignService;

@RestController
public class AccountSignController implements AccountsSignApi {

    private final AccountSignService accountSignService;

    public AccountSignController(AccountSignService accountSignService) {
        this.accountSignService = accountSignService;
    }

    @Override
    public ResponseEntity<AccountsSignResponse> list(AccountsSignFilter accountsSignFilter) {
        return ResponseEntity.ok(accountSignService.getAccountsSign(accountsSignFilter));
    }

    @Override
    public ResponseEntity<AccountsSignResponse> update(AccountsSignStatus accountsSignStatus) {
        return ResponseEntity.ok(accountSignService.updateAccountSign(accountsSignStatus));
    }
}

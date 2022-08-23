package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.AccountsSignApi;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountSignService;

import java.util.List;

@RestController
public class AccountSignController implements AccountsSignApi {

    private final AccountSignService accountSignService;

    public AccountSignController(AccountSignService accountSignService) {
        this.accountSignService = accountSignService;
    }

    @Override
    public ResponseEntity<AccountsSignInfoResponse> create(AccountsSignInfo accountsSignInfo) {
        return ResponseEntity.ok(accountSignService.createAccountsSign(accountsSignInfo));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, List<String> accountIds) {
        accountSignService.deleteAccountsSign(digitalId, accountIds);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AccountSignInfo> getById(String digitalId, String accountId) {
        return ResponseEntity.ok(accountSignService.getAccountSign(digitalId, accountId));
    }
}

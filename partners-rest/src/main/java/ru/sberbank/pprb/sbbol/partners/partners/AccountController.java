package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerAccountsApi;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountAndPartnerRequest;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountService;

import java.util.List;
import java.util.UUID;

@Loggable
@RestController
public class AccountController implements PartnerAccountsApi {

    private final AccountService accountService;

    private final IdsHistoryService idsHistoryService;

    public AccountController(AccountService accountService, IdsHistoryService idsHistoryService) {
        this.accountService = accountService;
        this.idsHistoryService = idsHistoryService;
    }

    @Override
    public ResponseEntity<Account> change(AccountPriority accountPriority) {
        return ResponseEntity.ok(accountService.changePriority(accountPriority));
    }

    @Override
    public ResponseEntity<Account> create(AccountCreate account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.saveAccount(account));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, List<UUID> ids) {
        accountService.deleteAccounts(digitalId, ids);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ExternalInternalIdLinksResponse> getAccountIdsByExternalIds(String digitalId, List<UUID> externalIds) {
        return ResponseEntity.ok(idsHistoryService.getAccountInternalIds(digitalId, externalIds));
    }

    @Override
    public ResponseEntity<List<AccountWithPartnerResponse>> getAtRequisites(AccountAndPartnerRequest request) {
        return ResponseEntity.ok(accountService.getAtRequisites(request));
    }

    @Override
    public ResponseEntity<AccountWithPartnerResponse> getAtAllRequisites(AccountAndPartnerRequest request) {
        return ResponseEntity.ok(accountService.getAtAllRequisites(request));
    }

    @Override
    public ResponseEntity<Account> getById(String digitalId, UUID id) {
        return ResponseEntity.ok(accountService.getAccount(digitalId, id));
    }

    @Override
    public ResponseEntity<AccountsResponse> list(AccountsFilter accountsFilter) {
        return ResponseEntity.ok(accountService.getAccounts(accountsFilter));
    }

    @Override
    public ResponseEntity<Account> update(AccountChange account) {
        return ResponseEntity.ok(accountService.updateAccount(account));
    }
}

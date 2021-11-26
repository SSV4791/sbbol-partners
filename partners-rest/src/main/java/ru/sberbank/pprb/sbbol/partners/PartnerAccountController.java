package ru.sberbank.pprb.sbbol.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccount;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccountsResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerAccountService;

@RestController
public class PartnerAccountController implements PartnerAccountsApi {

    private final PartnerAccountService partnerAccountService;

    public PartnerAccountController(PartnerAccountService partnerAccountService) {
        this.partnerAccountService = partnerAccountService;
    }

    @Override
    public ResponseEntity<PartnerAccountResponse> delete(String id, String digitalId) {
        return ResponseEntity.ok(partnerAccountService.deleteAccount(id, digitalId));
    }

    @Override
    public ResponseEntity<PartnerAccountResponse> getById(String id, String digitalId) {
        return ResponseEntity.ok(partnerAccountService.getAccount(id, digitalId));
    }

    @Override
    public ResponseEntity<PartnerAccountsResponse> list(PartnerAccountsFilter partnerAccountsFilter) {
        return ResponseEntity.ok(partnerAccountService.getAccounts(partnerAccountsFilter));
    }

    @Override
    public ResponseEntity<PartnerAccountResponse> save(PartnerAccount account) {
        return ResponseEntity.ok(partnerAccountService.saveAccount(account));
    }

    @Override
    public ResponseEntity<PartnerAccountResponse> update(PartnerAccount account) {
        return ResponseEntity.ok(partnerAccountService.updateAccount(account));
    }
}

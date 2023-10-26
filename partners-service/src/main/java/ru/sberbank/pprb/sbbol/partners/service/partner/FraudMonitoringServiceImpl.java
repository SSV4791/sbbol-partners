package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.exception.AccountAlreadySignedException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudServiceManager;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;

public class FraudMonitoringServiceImpl implements FraudMonitoringService {

    private static final String DOCUMENT_NAME = "account";

    private final AccountRepository accountRepository;

    private final PartnerRepository partnerRepository;

    private final FraudServiceManager fraudServiceManager;

    public FraudMonitoringServiceImpl(
        AccountRepository accountRepository,
        PartnerRepository partnerRepository,
        FraudServiceManager fraudServiceManager
    ) {
        this.accountRepository = accountRepository;
        this.partnerRepository = partnerRepository;
        this.fraudServiceManager = fraudServiceManager;
    }

    @Override
    public void createAccountsSign(AccountsSignInfo accountsSign, FraudMetaData fraudMetaData) {
        var digitalId = accountsSign.getDigitalId();
        for (var accountSign : accountsSign.getAccountsSignDetail()) {
            var account = accountRepository.getByDigitalIdAndUuid(digitalId, accountSign.getAccountId())
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountSign.getAccountId()));
            if (account.getState() == AccountStateType.SIGNED) {
                throw new AccountAlreadySignedException(account.getAccount());
            }
            if (!Objects.equals(account.getVersion(), accountSign.getAccountVersion())) {
                throw new OptimisticLockException(account.getVersion(), accountSign.getAccountVersion());
            }
            fraudServiceManager
                .getService(FraudEventType.SIGN_ACCOUNT)
                .sendEvent(fraudMetaData, account);
        }
    }

    @Override
    public void deletePartners(String digitalId, List<UUID> ids, FraudMetaData fraudMetaData) {
        for (UUID partnerId : ids) {
            var foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerId)
                .filter(partnerEntity -> PartnerType.PARTNER == partnerEntity.getType())
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, partnerId));
            if (nonNull(fraudMetaData)) {
                fraudServiceManager
                    .getService(FraudEventType.DELETE_PARTNER)
                    .sendEvent(fraudMetaData, foundPartner);
            }
        }
    }
}

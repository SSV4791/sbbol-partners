package ru.sberbank.pprb.sbbol.partners.service.replication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.legacy.exception.SbbolException;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class AbstractReplicationService implements ReplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractReplicationService.class);

    private static final String PARTNER_ENTRY = "partner";

    private static final String ACCOUNT_ENTRY = "account";

    private static final String ACCOUNT_SIGN_ENTRY = "account_sign";

    private static final String ERROR_MESSAGE_FOR_SBBOL_EXCEPTION = "Ошибка репликации в СББОЛ Legacy. {}";

    private final PartnerRepository partnerRepository;

    private final AccountRepository accountRepository;

    private final AccountSignRepository accountSignRepository;

    private final AccountSingMapper accountSingMapper;

    private final CounterpartyMapper counterpartyMapper;

    protected final ReplicationProperties replicationProperties;

    public AbstractReplicationService(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        AccountSingMapper accountSingMapper,
        CounterpartyMapper counterpartyMapper,
        ReplicationProperties replicationProperties
    ) {
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
        this.accountSingMapper = accountSingMapper;
        this.counterpartyMapper = counterpartyMapper;
        this.replicationProperties = replicationProperties;
    }

    @Override
    public void createCounterparty(List<Account> accounts) {
        for (Account account : accounts) {
            createCounterparty(account);
        }
    }

    @Override
    public void createCounterparty(Account account) {
        var partnerUuid = toUUID(account.getPartnerId());
        var accountUuid = toUUID(account.getId());
        var digitalId = account.getDigitalId();
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerUuid)
            .orElseThrow(() -> new EntryNotFoundException(PARTNER_ENTRY, partnerUuid));
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountUuid)
            .orElseThrow(() -> new EntryNotFoundException(ACCOUNT_ENTRY, accountUuid));
        Counterparty counterparty = counterpartyMapper.toCounterparty(foundPartner, foundAccount);
        try {
            handleCreatingCounterparty(digitalId, counterparty);
        } catch (SbbolException e) {
            if (!replicationProperties.isEnable()) {
                throw e;
            }
            LOG.error(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void updateCounterparty(List<Account> accounts) {
        for (Account account : accounts) {
            updateCounterparty(account);
        }
    }

    @Override
    public void updateCounterparty(Account account) {
        var partnerUuid = toUUID(account.getPartnerId());
        var accountUuid = toUUID(account.getId());
        var digitalId = account.getDigitalId();
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerUuid)
            .orElseThrow(() -> new EntryNotFoundException(PARTNER_ENTRY, partnerUuid));
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountUuid)
            .orElseThrow(() -> new EntryNotFoundException(ACCOUNT_ENTRY, accountUuid));
        Counterparty counterparty = counterpartyMapper.toCounterparty(foundPartner, foundAccount);
        try {
            handleUpdatingCounterparty(digitalId, counterparty);
        } catch (SbbolException e) {
            if (!replicationProperties.isEnable()) {
                throw e;
            }
            LOG.error(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void deleteCounterparties(String digitalId, List<String> accountIds) {
        for (String accountId : accountIds) {
            try {
                handleDeletingCounterparty(digitalId, accountId);
            } catch (SbbolException e) {
                if (!replicationProperties.isEnable()) {
                    throw e;
                }
                LOG.error(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
            }
        }
    }

    @Override
    public void deleteCounterparty(String digitalId, String accountId) {
        deleteCounterparties(digitalId, Collections.singletonList(accountId));
    }

    @Override
    public void deleteCounterparties(List<AccountEntity> accounts) {
        for (var account : accounts) {
            deleteCounterparty(account.getDigitalId(), account.getUuid().toString());
        }
    }

    @Override
    public void saveSign(String digitalId, String digitalUserId, UUID accountUuid) {
        var sign = accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, accountUuid)
            .orElseThrow(() -> new EntryNotFoundException(ACCOUNT_SIGN_ENTRY, accountUuid));
        var counterpartySignData = accountSingMapper.toCounterpartySignData(sign);
        try {
            handleCreatingSign(digitalUserId, counterpartySignData);
        } catch (SbbolException e) {
            if (!replicationProperties.isEnable()) {
                throw e;
            }
            LOG.error(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void deleteSign(String digitalId, UUID accountUuid) {
        var accountId = accountUuid.toString();
        try {
            handleDeletingSign(digitalId, accountId);
        } catch (SbbolException e) {
            if (!replicationProperties.isEnable()) {
                throw e;
            }
            LOG.error(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public UUID toUUID (String id) {
        return counterpartyMapper.mapUuid(id);
    }

    protected abstract void handleCreatingCounterparty(String digitalId, Counterparty counterparty);

    protected abstract void handleUpdatingCounterparty(String digitalId, Counterparty counterparty);

    protected abstract void handleDeletingCounterparty(String digitalId, String counterpartyId);

    protected abstract void handleCreatingSign(String digitalId, CounterpartySignData signData);

    protected abstract void  handleDeletingSign(String digitalId, String counterpartyId);
}

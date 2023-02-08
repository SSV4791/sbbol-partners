package ru.sberbank.pprb.sbbol.partners.service.replication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.legacy.exception.SbbolException;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class AbstractReplicationService implements ReplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractReplicationService.class);

    private static final String ERROR_MESSAGE_FOR_SBBOL_EXCEPTION = "Ошибка репликации в СББОЛ Legacy. ";

    private final PartnerRepository partnerRepository;

    private final AccountRepository accountRepository;

    private final AccountSignRepository accountSignRepository;

    private final AccountSingMapper accountSingMapper;

    private final CounterpartyMapper counterpartyMapper;

    public AbstractReplicationService(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        AccountSingMapper accountSingMapper,
        CounterpartyMapper counterpartyMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
        this.accountSingMapper = accountSingMapper;
        this.counterpartyMapper = counterpartyMapper;
    }

    @Override
    public void createCounterparty(List<Account> accounts) {
        for (Account account : accounts) {
            createCounterparty(account);
        }
    }

    @Override
    public void createCounterparty(Account account) {
        var partnerUuid = UUID.fromString(account.getPartnerId());
        var accountUuid = UUID.fromString(account.getId());
        var digitalId = account.getDigitalId();
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerUuid)
            .orElseThrow();
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountUuid)
            .orElseThrow();
        Counterparty counterparty = counterpartyMapper.toCounterparty(foundPartner, foundAccount);
        try {
            handleCreatingCounterparty(digitalId, counterparty);
        } catch (SbbolException e) {
            LOG.warn(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
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
        var partnerUuid = UUID.fromString(account.getPartnerId());
        var accountUuid = UUID.fromString(account.getId());
        var digitalId = account.getDigitalId();
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerUuid)
            .orElseThrow();
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountUuid)
            .orElseThrow();
        Counterparty counterparty = counterpartyMapper.toCounterparty(foundPartner, foundAccount);
        try {
            handleUpdatingCounterparty(digitalId, counterparty);
        } catch (SbbolException e) {
            LOG.warn(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void deleteCounterparties(String digitalId, List<String> accountIds) {
        for (String accountId : accountIds) {
            try {
                handleDeletingCounterparty(digitalId, accountId);
            } catch (SbbolException e) {
                LOG.warn(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
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
    public void saveSign(String digitalId, UUID accountUuid) {
        var sign = accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, accountUuid)
            .orElse(null);
        if (sign != null) {
            var counterpartySignData = accountSingMapper.toCounterpartySignData(sign);
            try {
                handleCreatingSign(digitalId, counterpartySignData);
            } catch (SbbolException e) {
                LOG.warn(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
            }
        }
    }

    @Override
    public void deleteSign(String digitalId, UUID accountUuid) {
        var accountId = accountUuid.toString();
        try {
            handleDeletingSign(digitalId, accountId);
        } catch (SbbolException e) {
            LOG.warn(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
        }
    }

    protected abstract void handleCreatingCounterparty(String digitalId, Counterparty counterparty);

    protected abstract void handleUpdatingCounterparty(String digitalId, Counterparty counterparty);

    protected abstract void handleDeletingCounterparty(String digitalId, String counterpartyId);

    protected abstract void handleCreatingSign(String digitalId, CounterpartySignData signData);

    protected abstract void  handleDeletingSign(String digitalId, String counterpartyId);
}

package ru.sberbank.pprb.sbbol.partners.service.replication.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.exception.SbbolException;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.exception.NotFoundReplicationEntityMapperException;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityMapperRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;
import ru.sberbank.pprb.sbbol.partners.replication.resolver.ReplicationRaceConditionResolver;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.List;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.UPDATING_COUNTERPARTY;

public class ReplicationServiceImpl implements ReplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(ReplicationServiceImpl.class);

    private static final String PARTNER_ENTRY = "partner";

    private static final String ACCOUNT_ENTRY = "account";

    private static final String ACCOUNT_SIGN_ENTRY = "account_sign";

    private static final String ERROR_MESSAGE_FOR_SBBOL_EXCEPTION = "Ошибка репликации в СББОЛ Legacy. {}";

    private final PartnerRepository partnerRepository;

    private final AccountRepository accountRepository;

    private final AccountSignRepository accountSignRepository;

    private final AccountSingMapper accountSingMapper;

    private final CounterpartyMapper counterpartyMapper;

    private final ReplicationProperties replicationProperties;

    private final LegacySbbolAdapter legacySbbolAdapter;

    private final ReplicationRaceConditionResolver raceConditionResolver;

    private final ReplicationEntityMapperRegistry mapperRegistry;

    private final ReplicationRepository replicationRepository;

    public ReplicationServiceImpl(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        AccountSingMapper accountSingMapper,
        CounterpartyMapper counterpartyMapper,
        ReplicationProperties replicationProperties,
        LegacySbbolAdapter legacySbbolAdapter,
        ReplicationRaceConditionResolver raceConditionResolver,
        ReplicationEntityMapperRegistry mapperRegistry,
        ReplicationRepository replicationRepository
    ) {
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
        this.accountSingMapper = accountSingMapper;
        this.counterpartyMapper = counterpartyMapper;
        this.replicationProperties = replicationProperties;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.raceConditionResolver = raceConditionResolver;
        this.mapperRegistry = mapperRegistry;
        this.replicationRepository = replicationRepository;
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
            deleteCounterparty(digitalId, accountId);
        }
    }

    @Override
    public void deleteCounterparties(List<AccountEntity> accounts) {
        for (var account : accounts) {
            deleteCounterparty(account.getDigitalId(), account.getUuid().toString());
        }
    }

    @Override
    public void deleteCounterparty(String digitalId, String accountId) {
        try {
            handleDeletingCounterparty(digitalId, accountId);
        } catch (SbbolException e) {
            if (!replicationProperties.isEnable()) {
                throw e;
            }
            LOG.error(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void saveSign(String digitalId, String digitalUserId, UUID accountUuid) {
        var sign = accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, accountUuid)
            .orElseThrow(() -> new EntryNotFoundException(ACCOUNT_SIGN_ENTRY, accountUuid));
        var counterpartySignData = accountSingMapper.toCounterpartySignData(sign);
        try {
            handleCreatingSign(digitalId, digitalUserId, counterpartySignData);
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

    private UUID toUUID (String id) {
        return counterpartyMapper.mapUuid(id);
    }

    private void handleCreatingCounterparty(String digitalId, Counterparty counterparty) {
        LOG.debug("Сохраняем в очередь реплику по созданию контрагента в СББОЛ Legacy. digitalId={}. counterparty={}", digitalId, counterparty);
        saveReplicationEntityToMessageQueue(
            digitalId,
            toUUID(counterparty.getPprbGuid()),
            CREATING_COUNTERPARTY,
            counterparty
        );
        LOG.debug("Отправляем реплику по созданию контрагента в СББОЛ Legacy. digitalId={}. counterparty={}", digitalId, counterparty);
        legacySbbolAdapter.create(digitalId, counterparty);
        LOG.debug("Запускаем задание ReplicationRaceConditionResolver для counterparty.getPprbGuid={}", toUUID(counterparty.getPprbGuid()));
        raceConditionResolver.resolve(CREATING_COUNTERPARTY, toUUID(counterparty.getPprbGuid()), digitalId);
    }

    private void handleUpdatingCounterparty(String digitalId, Counterparty counterparty) {
        LOG.debug("Сохраняем в очередь реплику по изменению контрагента в СББОЛ Legacy. digitalId={}. counterparty={}", digitalId, counterparty);
        saveReplicationEntityToMessageQueue(
            digitalId,
            toUUID(counterparty.getPprbGuid()),
            UPDATING_COUNTERPARTY,
            counterparty
        );
        LOG.debug("Отправляем реплику по изменению контрагента в СББОЛ Legacy. digitalId={}. counterparty={}", digitalId, counterparty);
        legacySbbolAdapter.update(digitalId, counterparty);
        LOG.debug("Запускаем задание ReplicationRaceConditionResolver для counterparty.getPprbGuid={}", toUUID(counterparty.getPprbGuid()));
        raceConditionResolver.resolve(UPDATING_COUNTERPARTY, toUUID(counterparty.getPprbGuid()), digitalId);

    }

    private void handleDeletingCounterparty(String digitalId, String counterpartyId) {
        LOG.debug("Сохраняем в очередь реплику по удалению контрагента в СББОЛ Legacy. digitalId={}, сounterpartyId={}", digitalId, counterpartyId);
        saveReplicationEntityToMessageQueue(
            digitalId,
            toUUID(counterpartyId),
            DELETING_COUNTERPARTY,
            counterpartyId
        );
        LOG.debug("Отправляем реплику по удалению контрагента в СББОЛ Legacy. digitalId={}, сounterpartyId={}", digitalId, counterpartyId);
        legacySbbolAdapter.delete(digitalId, counterpartyId);
        LOG.debug("Запускаем задание ReplicationRaceConditionResolver для counterpartyId={}", toUUID(counterpartyId));
        raceConditionResolver.resolve(DELETING_COUNTERPARTY, toUUID(counterpartyId), digitalId);
    }

    private void handleCreatingSign(String digitalId, String digitalUserId, CounterpartySignData signData) {
        LOG.debug("Сохраняем в очередь реплику по созданию подписи в СББОЛ Legacy. digitalId={}, signData={}", digitalId, signData);
        saveReplicationEntityToMessageQueue(
            digitalId,
            digitalUserId,
            signData.getPprbGuid(),
            CREATING_SIGN,
            signData
        );
        var counterpartyId = signData.getPprbGuid();
        LOG.debug("Отправляем реплику по созданию подписи в СББОЛ Legacy. digitalId={}, signData={}", digitalId, signData);
        legacySbbolAdapter.saveSign(digitalUserId, signData);
        LOG.debug("Запускаем задание ReplicationRaceConditionResolver для counterpartyId={}", counterpartyId);
        raceConditionResolver.resolve(CREATING_SIGN, counterpartyId, digitalId);
    }

    private void  handleDeletingSign(String digitalId, String counterpartyId) {
        LOG.debug("Сохраняем в очередь реплику по удалению подписи в СББОЛ Legacy. digitalId={}, signData={}", digitalId, counterpartyId);
        saveReplicationEntityToMessageQueue(
            digitalId,
            toUUID(counterpartyId),
            DELETING_SIGN,
            counterpartyId
        );
        LOG.debug("Отправляем реплику по удалению подписи в СББОЛ Legacy. digitalId={}, signData={}", digitalId, counterpartyId);
        legacySbbolAdapter.removeSign(digitalId, counterpartyId);
        LOG.debug("Запускаем задание ReplicationRaceConditionResolver для counterpartyId={}", counterpartyId);
        raceConditionResolver.resolve(DELETING_SIGN, toUUID(counterpartyId), digitalId);
    }

    private <T> void saveReplicationEntityToMessageQueue(
        String digitalId,
        UUID entityId,
        ReplicationEntityType replicationEntityType,
        T replicationEntity
    ) {
        saveReplicationEntityToMessageQueue(
            digitalId,
            null,
            entityId,
            replicationEntityType,
            replicationEntity
        );
    }

    private <T> void saveReplicationEntityToMessageQueue(
        String digitalId,
        String digitalUserId,
        UUID entityId,
        ReplicationEntityType replicationEntityType,
        T replicationEntity
    ) {
        if (replicationProperties.isEnable()) {
            var mapper = mapperRegistry.findMapper(replicationEntityType)
                .orElseThrow(() -> new NotFoundReplicationEntityMapperException(replicationEntityType));
            var entity = mapper.map(
                digitalId,
                digitalUserId,
                entityId,
                replicationEntityType,
                replicationEntity);
            replicationRepository.save(entity);
        }
    }
}

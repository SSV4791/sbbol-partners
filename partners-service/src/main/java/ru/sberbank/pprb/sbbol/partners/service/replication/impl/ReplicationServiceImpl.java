package ru.sberbank.pprb.sbbol.partners.service.replication.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.exception.SbbolException;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.exception.NotFoundReplicationEntityMapperException;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityMapperRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.List;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.mapUuid;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.UPDATING_COUNTERPARTY;

@Loggable
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
        var partnerUuid = account.getPartnerId();
        var accountUuid = account.getId();
        var digitalId = account.getDigitalId();
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerUuid)
            .orElseThrow(() -> new EntryNotFoundException(PARTNER_ENTRY, partnerUuid));
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountUuid)
            .orElseThrow(() -> new EntryNotFoundException(ACCOUNT_ENTRY, accountUuid));
        Counterparty counterparty = counterpartyMapper.toCounterparty(foundPartner, foundAccount);
        try {
            LOG.debug("Отправляем реплику по созданию контрагента в СББОЛ Legacy. digitalId={}. counterparty={}", digitalId, counterparty);
            legacySbbolAdapter.create(digitalId, counterparty, getXRequestIdFromMDC());
        } catch (SbbolException e) {
            if (!isAsyncReplication()) {
                throw e;
            }
            LOG.warn(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
            saveReplicationEntityToMessageQueue(
                digitalId,
                mapUuid(counterparty.getPprbGuid()),
                CREATING_COUNTERPARTY,
                counterparty
            );
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
        var partnerUuid = account.getPartnerId();
        var accountUuid = account.getId();
        var digitalId = account.getDigitalId();
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerUuid)
            .orElseThrow(() -> new EntryNotFoundException(PARTNER_ENTRY, partnerUuid));
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountUuid)
            .orElseThrow(() -> new EntryNotFoundException(ACCOUNT_ENTRY, accountUuid));
        Counterparty counterparty = counterpartyMapper.toCounterparty(foundPartner, foundAccount);
        try {
            LOG.debug("Отправляем реплику по изменению контрагента в СББОЛ Legacy. digitalId={}. counterparty={}", digitalId, counterparty);
            legacySbbolAdapter.update(digitalId, counterparty, getXRequestIdFromMDC());
        } catch (SbbolException e) {
            if (!isAsyncReplication()) {
                throw e;
            }
            LOG.warn(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
            saveReplicationEntityToMessageQueue(
                digitalId,
                mapUuid(counterparty.getPprbGuid()),
                UPDATING_COUNTERPARTY,
                counterparty
            );
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
            LOG.debug("Отправляем реплику по удалению контрагента в СББОЛ Legacy. digitalId={}, сounterpartyId={}", digitalId, accountId);
            legacySbbolAdapter.delete(digitalId, accountId, getXRequestIdFromMDC());
        } catch (SbbolException e) {
            if (!isAsyncReplication()) {
                throw e;
            }
            LOG.warn(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
            saveReplicationEntityToMessageQueue(
                digitalId,
                mapUuid(accountId),
                DELETING_COUNTERPARTY,
                accountId
            );
        }
    }

    @Override
    public void saveSign(String digitalId, String digitalUserId, UUID accountUuid) {
        var sign = accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, accountUuid)
            .orElseThrow(() -> new EntryNotFoundException(ACCOUNT_SIGN_ENTRY, accountUuid));
        var counterpartySignData = accountSingMapper.toCounterpartySignData(sign);
        try {
            LOG.debug("Отправляем реплику по созданию подписи в СББОЛ Legacy. digitalId={}, signData={}", digitalId, counterpartySignData);
            legacySbbolAdapter.saveSign(digitalUserId, counterpartySignData, getXRequestIdFromMDC());
        } catch (SbbolException e) {
            if (!isAsyncReplication()) {
                throw e;
            }
            LOG.warn(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
            saveReplicationEntityToMessageQueue(
                digitalId,
                digitalUserId,
                counterpartySignData.getPprbGuid(),
                CREATING_SIGN,
                counterpartySignData
            );
        }
    }

    @Override
    public void deleteSign(String digitalId, UUID accountUuid) {
        var accountId = accountUuid.toString();
        try {
            LOG.debug("Отправляем реплику по удалению подписи в СББОЛ Legacy. digitalId={}, signData={}", digitalId, accountId);
            legacySbbolAdapter.removeSign(digitalId, accountId, getXRequestIdFromMDC());
        } catch (SbbolException e) {
            if (!isAsyncReplication()) {
                throw e;
            }
            LOG.warn(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
            saveReplicationEntityToMessageQueue(
                digitalId,
                mapUuid(accountId),
                DELETING_SIGN,
                accountId
            );
        }
    }

    private <T> ReplicationEntity saveReplicationEntityToMessageQueue(
        String digitalId,
        UUID entityId,
        ReplicationEntityType replicationEntityType,
        T replicationEntity
    ) {
        return saveReplicationEntityToMessageQueue(
            digitalId,
            null,
            entityId,
            replicationEntityType,
            replicationEntity
        );
    }

    private <T> ReplicationEntity saveReplicationEntityToMessageQueue(
        String digitalId,
        String digitalUserId,
        UUID entityId,
        ReplicationEntityType replicationEntityType,
        T replicationEntity
    ) {
        if (replicationProperties.isEnable()) {
            var mapper = mapperRegistry.findMapper(replicationEntityType)
                .orElseThrow(() -> new NotFoundReplicationEntityMapperException(replicationEntityType));
            ReplicationEntity replicaEntity = mapper.map(
                digitalId,
                digitalUserId,
                entityId,
                replicationEntityType,
                replicationEntity
            );
            var foundedReplicaEntity = replicationRepository.getByDigitalIdAndEntityIdAndEntityType(
                digitalId,
                entityId,
                replicationEntityType
            );
            foundedReplicaEntity.ifPresent(foundEntity -> {
                replicaEntity.setUuid(foundEntity.getUuid());
                replicaEntity.setCreateDate(foundEntity.getCreateDate());
                replicaEntity.requestId(getXRequestIdFromMDC());
            });
            return replicationRepository.save(replicaEntity);
        }
        return null;
    }

    private String getXRequestIdFromMDC() {
        return MDC.get("requestUid");
    }

    private boolean isAsyncReplication() {
        return replicationProperties.isEnable();
    }
}

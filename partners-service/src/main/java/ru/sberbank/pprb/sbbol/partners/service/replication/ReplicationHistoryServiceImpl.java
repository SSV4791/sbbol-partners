package ru.sberbank.pprb.sbbol.partners.service.replication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ReplicationHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.Counterparty;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ReplicationHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.utils.PartnerUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Logged(printRequestResponse = true)
public class ReplicationHistoryServiceImpl implements ReplicationHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerServiceImpl.class);

    private final AccountRepository accountRepository;
    private final ReplicationHistoryRepository replicationHistoryRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final PartnerUtils partnerUtils;
    private final CounterpartyMapper counterpartyMapper;
    private final AccountMapper accountMapper;

    public ReplicationHistoryServiceImpl(
        AccountRepository accountRepository,
        ReplicationHistoryRepository replicationHistoryRepository,
        LegacySbbolAdapter legacySbbolAdapter,
        PartnerUtils partnerUtils,
        CounterpartyMapper counterpartyMapper,
        AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.replicationHistoryRepository = replicationHistoryRepository;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.partnerUtils = partnerUtils;
        this.counterpartyMapper = counterpartyMapper;
        this.accountMapper = accountMapper;
    }

    @Override
    public void updateCounterparty(Partner partner) {
        Set<String> sbbolGuids = getSbbolGuidsByPartnerUuid(UUID.fromString(partner.getId()));
        if (CollectionUtils.isEmpty(sbbolGuids)) {
            return;
        }
        for (String sbbolGuid : sbbolGuids) {
            partner.setId(sbbolGuid);
            try {
                partnerUtils.updateCounterpartyByPartner(partner);
            } catch (Exception e) {
                LOG.error("Возникла ошибка при обновлении контрагента в СББОЛ: {}", partner);
            }
        }
    }

    @Override
    public void updatePartner(Partner partner) {
        Set<UUID> partnerGuids = getPartnerUuidsBySbbolGuid(partner.getId());
        if (CollectionUtils.isEmpty(partnerGuids)) {
            return;
        }
        for (UUID partnerUuid : partnerGuids) {
            partner.setId(partnerUuid.toString());
            try {
                partnerUtils.updatePartnerByPartner(partner);
            } catch (Exception e) {
                LOG.error("Возникла ошибка при обновлении партнёра в фабрике: {}", partner);
            }
        }
    }

    @Override
    public void deleteCounterparty(String digitalId, String id) {
        Set<String> sbbolGuids = getSbbolGuidsByPartnerUuid(UUID.fromString(id));
        if (CollectionUtils.isEmpty(sbbolGuids)) {
            return;
        }
        for (String sbbolGuid : sbbolGuids) {
            try {
                legacySbbolAdapter.delete(digitalId, sbbolGuid);
                replicationHistoryRepository.deleteBySbbolGuid(sbbolGuid);
            } catch (Exception e) {
                LOG.error("Возникла ошибка при удалении контрагента в СББОЛ по ППРБ гуиду: {}", sbbolGuid);
            }
        }
    }

    @Override
    public void deletePartner(String digitalId, String id) {
        Set<UUID> partnerGuids = getPartnerUuidsBySbbolGuid(id);
        if (CollectionUtils.isEmpty(partnerGuids)) {
            return;
        }
        for (UUID partnerGuid : partnerGuids) {
            try {
                partnerUtils.deletePartnerByDigitalIdAndId(digitalId, partnerGuid.toString());
                replicationHistoryRepository.deleteByPartnerUuid(partnerGuid);
            } catch (Exception e) {
                LOG.error("Возникла ошибка при удалении партнёра в фабрике по гуиду: {}", partnerGuid);
            }
        }
    }

    @Override
    public void saveCounterparty(PartnerEntity partner, Account account, AccountEntity savedAccount) {
        Counterparty counterparty = counterpartyMapper.toCounterparty(partner, account);
        try {
            Counterparty sbbolUpdatedCounterparty = partnerUtils.createOrUpdateCounterparty(counterparty, account);
            updateReplicationHistory(savedAccount, sbbolUpdatedCounterparty);
        } catch (Exception e) {
            LOG.error("Возникла ошибка при сохранении контрагента в СББОЛ: {}", counterparty.toString());
        }
    }

    @Override
    public UUID saveAccount(Account account, Counterparty sbbolUpdatedCounterparty) {
        var requestAccount = accountMapper.toAccount(account);
        UUID accountUuid = null;
        try {
            var savedAccount = accountRepository.save(requestAccount);
            accountUuid = savedAccount.getUuid();
            updateReplicationHistory(savedAccount, sbbolUpdatedCounterparty);
        } catch (Exception e) {
            LOG.error("Возникла ошибка при сохранении счёта: {}", requestAccount);
        }
        return accountUuid;
    }

    @Override
    public void updateCounterparty(Account account) {
        List<ReplicationHistoryEntity> replicationHistoryEntityList = replicationHistoryRepository.findByAccountUuid(UUID.fromString(account.getId()));
        if (CollectionUtils.isEmpty(replicationHistoryEntityList)) {
            return;
        }
        List<String> sbbolGuids = replicationHistoryEntityList.stream().map(ReplicationHistoryEntity::getSbbolGuid).filter(Objects::nonNull).collect(Collectors.toList());
        for (String sbbolGuid : sbbolGuids) {
            try {
                Counterparty sbbolCounterparty = legacySbbolAdapter.getByPprbGuid(account.getDigitalId(), sbbolGuid);
                partnerUtils.createOrUpdateCounterparty(sbbolCounterparty, account);
            } catch (Exception e) {
                LOG.error("Возникла ошибка при обновлении счёта в СББОЛ: {}", account);
            }
        }
    }

    @Override
    public UUID updateAccount(Account account, Counterparty sbbolUpdatedCounterparty) {
        UUID accountUuid = null;
        if (account.getId() == null) {
            return accountUuid;
        }
        try {
            AccountEntity accountEntity = partnerUtils.updatePartnerAccount(account);
            updateReplicationHistory(accountEntity, sbbolUpdatedCounterparty);
            accountUuid = accountEntity.getUuid();
        } catch (Exception e) {
            LOG.error("Возникла ошибка при обновлении счёта: {}", account);
        }
        return accountUuid;
    }

    @Override
    public void deleteAccount(String digitalId, String id) {
        try {
            var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
            if (foundAccount != null) {
                accountRepository.delete(foundAccount);
            }
        } catch (Exception e) {
            LOG.error("Возникла ошибка при удалении счёта из фабрики");
        }
    }

    /**
     * Получить список идентификаторов партнёров по идентификатору контрагентов в СББОЛе
     *
     * @param id Идентификатор партнёра
     * @return Список идентификатор контрагентов в СББОЛе
     */
    private Set<String> getSbbolGuidsByPartnerUuid(UUID id) {
        List<ReplicationHistoryEntity> replicationHistories = replicationHistoryRepository.findByPartnerUuid(id);
        if (CollectionUtils.isEmpty(replicationHistories)) {
            return null;
        }
        return replicationHistories.stream().map(ReplicationHistoryEntity::getSbbolGuid).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * Получить список идентификатор контрагентов в СББОЛе по идентификатору партнёра
     *
     * @param id Идентификатор контрагента в СББОЛ
     * @return Список идентификаторов партнёров
     */
    private Set<UUID> getPartnerUuidsBySbbolGuid(String id) {
        List<ReplicationHistoryEntity> replicationHistories = replicationHistoryRepository.findBySbbolGuid(id);
        if (CollectionUtils.isEmpty(replicationHistories)) {
            return null;
        }
        return replicationHistories.stream().map(ReplicationHistoryEntity::getPartnerUuid).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * Обновить запись в replication_history
     *
     * @param account                  Счёт
     * @param sbbolUpdatedCounterparty Контрагент из СББОЛа
     */
    private void updateReplicationHistory(AccountEntity account, Counterparty sbbolUpdatedCounterparty) {
        if (CollectionUtils.isEmpty(account.getBanks())) {
            updateReplicationHistory(account.getPartnerUuid(), account.getUuid(), null, null, null);
        } else {
            for (BankEntity bank : account.getBanks()) {
                if (CollectionUtils.isEmpty(bank.getBankAccounts())) {
                    updateReplicationHistory(
                        account.getPartnerUuid(),
                        account.getUuid(),
                        bank.getUuid(),
                        null,
                        sbbolUpdatedCounterparty != null ? sbbolUpdatedCounterparty.getPprbGuid() : null
                    );
                } else {
                    for (BankAccountEntity bankAccount : bank.getBankAccounts()) {
                        updateReplicationHistory(
                            account.getPartnerUuid(),
                            account.getUuid(),
                            bank.getUuid(),
                            bankAccount.getUuid(),
                            sbbolUpdatedCounterparty != null ? sbbolUpdatedCounterparty.getPprbGuid() : null
                        );
                    }
                }
            }
        }
    }

    /**
     * Обновить запись в replication_history
     *
     * @param partnerUuid     Идентификатор партнёра
     * @param accountUuid     Идентификатор счёта
     * @param bankUuid        Идентификатор банка
     * @param bankAccountUuid Идентификатор корр счёта
     * @param sbbolGuid       Идентификатор в СББОЛ
     */
    private void updateReplicationHistory(UUID partnerUuid, UUID accountUuid, UUID bankUuid, UUID bankAccountUuid, String sbbolGuid) {
        List<ReplicationHistoryEntity> replicationHistoryEntityList = replicationHistoryRepository.findByPartnerUuid(partnerUuid);
        var replicationHistory = new ReplicationHistoryEntity();
        if (!CollectionUtils.isEmpty(replicationHistoryEntityList) && replicationHistoryEntityList.size() == 1 && replicationHistoryEntityList.get(0).getAccountUuid() == null) {
            replicationHistory = replicationHistoryEntityList.get(0);
        } else {
            replicationHistory.setPartnerUuid(partnerUuid);
        }
        replicationHistory.setAccountUuid(accountUuid);
        replicationHistory.setBankAccountUuid(bankAccountUuid);
        replicationHistory.setBankUuid(bankUuid);
        replicationHistory.setSbbolGuid(sbbolGuid);
        replicationHistoryRepository.save(replicationHistory);
    }
}

package ru.sberbank.pprb.sbbol.partners.service.replication.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.replication.resolver.ReplicationRaceConditionResolver;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.AbstractReplicationService;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.UPDATING_COUNTERPARTY;

@Loggable
public class SendingReplicationServiceImpl extends AbstractReplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(SendingReplicationServiceImpl.class);

    private final LegacySbbolAdapter legacySbbolAdapter;

    private final ReplicationRaceConditionResolver raceConditionResolver;

    public SendingReplicationServiceImpl(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        AccountSingMapper accountSingMapper,
        CounterpartyMapper counterpartyMapper,
        LegacySbbolAdapter legacySbbolAdapter,
        ReplicationRaceConditionResolver raceConditionResolver
    ) {
        super(partnerRepository, accountRepository, accountSignRepository, accountSingMapper, counterpartyMapper);
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.raceConditionResolver = raceConditionResolver;
    }

    @Override
    protected void handleCreatingCounterparty(String digitalId, Counterparty counterparty) {
        LOG.debug("Отправляем реплику по созданию контрагента в СББОЛ Legacy. digitalId={}. counterparty={}", digitalId, counterparty);
        legacySbbolAdapter.create(digitalId, counterparty);
        LOG.debug("Запускаем задание ReplicationRaceConditionResolver для counterparty.getPprbGuid={}", toUUID(counterparty.getPprbGuid()));
        raceConditionResolver.resolve(CREATING_COUNTERPARTY, toUUID(counterparty.getPprbGuid()), digitalId);
    }

    @Override
    protected void handleUpdatingCounterparty(String digitalId, Counterparty counterparty) {
        LOG.debug("Отправляем реплику по изменению контрагента в СББОЛ Legacy. digitalId={}. counterparty={}", digitalId, counterparty);
        legacySbbolAdapter.update(digitalId, counterparty);
        LOG.debug("Запускаем задание ReplicationRaceConditionResolver для counterparty.getPprbGuid={}", toUUID(counterparty.getPprbGuid()));
        raceConditionResolver.resolve(UPDATING_COUNTERPARTY, toUUID(counterparty.getPprbGuid()), digitalId);
    }

    @Override
    protected void handleDeletingCounterparty(String digitalId, String counterpartyId) {
        LOG.debug("Отправляем реплику по удалению контрагента в СББОЛ Legacy. digitalId={}, сounterpartyId={}", digitalId, counterpartyId);
        legacySbbolAdapter.delete(digitalId, counterpartyId);
        LOG.debug("Запускаем задание ReplicationRaceConditionResolver для counterpartyId={}", toUUID(counterpartyId));
        raceConditionResolver.resolve(DELETING_COUNTERPARTY, toUUID(counterpartyId), digitalId);
    }

    @Override
    protected void handleCreatingSign(String digitalId, CounterpartySignData signData) {
        var counterpartyId = signData.getPprbGuid();
        LOG.debug("Отправляем реплику по созданию подписи в СББОЛ Legacy. digitalId={}, signData={}", digitalId, signData);
        legacySbbolAdapter.saveSign(digitalId, signData);
        LOG.debug("Запускаем задание ReplicationRaceConditionResolver для counterpartyId={}", counterpartyId);
        raceConditionResolver.resolve(CREATING_SIGN, counterpartyId, digitalId);
    }

    @Override
    protected void handleDeletingSign(String digitalId, String counterpartyId) {
        LOG.debug("Отправляем реплику по удалению подписи в СББОЛ Legacy. digitalId={}, signData={}", digitalId, counterpartyId);
        legacySbbolAdapter.removeSign(digitalId, counterpartyId);
        LOG.debug("Запускаем задание ReplicationRaceConditionResolver для counterpartyId={}", counterpartyId);
        raceConditionResolver.resolve(DELETING_SIGN, toUUID(counterpartyId), digitalId);
    }
}

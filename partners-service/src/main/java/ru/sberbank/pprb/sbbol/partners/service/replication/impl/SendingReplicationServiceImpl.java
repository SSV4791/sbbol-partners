package ru.sberbank.pprb.sbbol.partners.service.replication.impl;

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
        legacySbbolAdapter.create(digitalId, counterparty);
        raceConditionResolver.resolve(CREATING_COUNTERPARTY, toUUID(counterparty.getPprbGuid()), digitalId);
    }

    @Override
    protected void handleUpdatingCounterparty(String digitalId, Counterparty counterparty) {
        legacySbbolAdapter.update(digitalId, counterparty);
        raceConditionResolver.resolve(UPDATING_COUNTERPARTY, toUUID(counterparty.getPprbGuid()), digitalId);
    }

    @Override
    protected void handleDeletingCounterparty(String digitalId, String counterpartyId) {
        legacySbbolAdapter.delete(digitalId, counterpartyId);
        raceConditionResolver.resolve(DELETING_COUNTERPARTY, toUUID(counterpartyId), digitalId);
    }

    @Override
    protected void handleCreatingSign(String digitalId, CounterpartySignData signData) {
        var counterpartyId = signData.getPprbGuid();
        legacySbbolAdapter.saveSign(digitalId, signData);
        raceConditionResolver.resolve(CREATING_SIGN, counterpartyId, digitalId);
    }

    @Override
    protected void handleDeletingSign(String digitalId, String counterpartyId) {
        legacySbbolAdapter.removeSign(digitalId, counterpartyId);
        raceConditionResolver.resolve(DELETING_SIGN, toUUID(counterpartyId), digitalId);
    }
}

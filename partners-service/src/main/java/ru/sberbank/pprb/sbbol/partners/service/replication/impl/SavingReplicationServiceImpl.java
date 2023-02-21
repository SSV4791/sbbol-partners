package ru.sberbank.pprb.sbbol.partners.service.replication.impl;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.exception.NotFoundReplicationEntityMapperException;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityMapperRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.AbstractReplicationService;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationServiceType;

import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.UPDATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationServiceType.SAVING_MESSAGE;

@Loggable
public class SavingReplicationServiceImpl extends AbstractReplicationService {

    private final ReplicationEntityMapperRegistry mapperRegistry;

    private final ReplicationRepository replicationRepository;

    public SavingReplicationServiceImpl(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        AccountSingMapper accountSingMapper,
        CounterpartyMapper counterpartyMapper,
        ReplicationProperties replicationProperties,
        ReplicationEntityMapperRegistry mapperRegistry,
        ReplicationRepository replicationRepository
    ) {
        super(
            partnerRepository,
            accountRepository,
            accountSignRepository,
            accountSingMapper,
            counterpartyMapper,
            replicationProperties
        );
        this.mapperRegistry = mapperRegistry;
        this.replicationRepository = replicationRepository;
    }

    @Override
    public ReplicationServiceType getServiceType() {
        return SAVING_MESSAGE;
    }

    @Override
    protected void handleCreatingCounterparty(String digitalId, Counterparty counterparty) {
        saveReplicationEntityToMessageQueue(
            digitalId,
            toUUID(counterparty.getPprbGuid()),
            CREATING_COUNTERPARTY,
            counterparty
        );
    }

    @Override
    protected void handleUpdatingCounterparty(String digitalId, Counterparty counterparty) {
        saveReplicationEntityToMessageQueue(
            digitalId,
            toUUID(counterparty.getPprbGuid()),
            UPDATING_COUNTERPARTY,
            counterparty
        );
    }

    @Override
    protected void handleDeletingCounterparty(String digitalId, String counterpartyId) {
        saveReplicationEntityToMessageQueue(
            digitalId,
            toUUID(counterpartyId),
            DELETING_COUNTERPARTY,
            counterpartyId
        );
    }

    @Override
    protected void handleCreatingSign(String digitalId, CounterpartySignData signData) {
        saveReplicationEntityToMessageQueue(
            digitalId,
            signData.getPprbGuid(),
            CREATING_SIGN,
            signData
        );
    }

    @Override
    protected void handleDeletingSign(String digitalId, String counterpartyId) {
        saveReplicationEntityToMessageQueue(
            digitalId,
            toUUID(counterpartyId),
            DELETING_SIGN,
            counterpartyId
        );
    }

    private <T> void saveReplicationEntityToMessageQueue(
        String digitalId,
        UUID entityId,
        ReplicationEntityType replicationEntityType,
        T replicationEntity
    ) {
        if (replicationProperties.isEnable()) {
            var mapper = mapperRegistry.findMapper(replicationEntityType)
                .orElseThrow(() -> new NotFoundReplicationEntityMapperException(replicationEntityType));
            var entity = mapper.map(
                digitalId,
                entityId,
                replicationEntityType,
                replicationEntity);
            replicationRepository.save(entity);
        }
    }
}

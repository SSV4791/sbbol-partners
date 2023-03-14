package ru.sberbank.pprb.sbbol.partners.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;
import ru.sberbank.pprb.sbbol.partners.replication.resolver.ReplicationRaceConditionResolver;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.CANCEL;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.INIT;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.ERROR;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.UPDATING_COUNTERPARTY;

class ReplicationJobTest extends AbstractReplicationJobTest {

    @Autowired
    private ReplicationRaceConditionResolver raceConditionResolver;

    @Autowired
    private ReplicationJob replicationJob;

    @Test
    void testProcessingInitReplicationQueue() {
        var count = 30;
        var retry = 0;
        var digitalId = podamFactory.manufacturePojo(String.class);
        var entityId = UUID.randomUUID();
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_COUNTERPARTY, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, UPDATING_COUNTERPARTY, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_COUNTERPARTY, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_SIGN, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_SIGN, INIT, retry);
        replicationJob.run();
        var actualEntities = repository.findByEntityId(entityId);
        checkReplicationEntityList(actualEntities, count * 5, ReplicationEntityStatus.SUCCESS, retry + 1);
    }

    @Test
    void testProcessingErrorReplicationQueue() {
        var count = 30;
        var retry = 2;
        var digitalId = podamFactory.manufacturePojo(String.class);
        var entityId = UUID.randomUUID();
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_COUNTERPARTY, ERROR, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, UPDATING_COUNTERPARTY, ERROR, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_COUNTERPARTY, ERROR, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_SIGN, ERROR, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_SIGN, ERROR, retry);
        replicationJob.run();
        var actualEntities = repository.findByEntityId(entityId);
        checkReplicationEntityList(actualEntities, count * 5, ReplicationEntityStatus.SUCCESS, retry + 1);
    }

    @Test
    void testProcessingAnyReplicationQueue() {
        var count = 30;
        var retry = 2;
        var digitalId = podamFactory.manufacturePojo(String.class);
        var entityId = UUID.randomUUID();
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_COUNTERPARTY, INIT, 0);
        prepareTestingReplicationEntities(count, digitalId, entityId, UPDATING_COUNTERPARTY, INIT, 0);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_COUNTERPARTY, INIT, 0);
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_SIGN, INIT, 0);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_SIGN, INIT, 0);

        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_COUNTERPARTY, ERROR, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, UPDATING_COUNTERPARTY, ERROR, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_COUNTERPARTY, ERROR, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_SIGN, ERROR, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_SIGN, ERROR, retry);
        replicationJob.run();
        var actualEntities = repository.findByEntityId(entityId);
        checkReplicationEntityList(actualEntities, count * 5 * 2, ReplicationEntityStatus.SUCCESS);
    }

    @Test
    void testReplicationRaceConditionResolver_whenFirstSyncReplicationIsSuccess() {
        var count = 30;
        var retry = 2;
        var digitalId = podamFactory.manufacturePojo(String.class);
        var entityId = UUID.randomUUID();
        prepareTestingReplicationEntities(count, digitalId, entityId, UPDATING_COUNTERPARTY, INIT, 0);
        prepareTestingReplicationEntities(count, digitalId, entityId, UPDATING_COUNTERPARTY, ERROR, retry);
        raceConditionResolver.resolve(UPDATING_COUNTERPARTY, entityId, digitalId);
        var actualEntities = repository.findByEntityId(entityId);
        assertThat(actualEntities.stream().noneMatch(it -> it.getEntityStatus() == INIT))
            .isTrue();
        assertThat(actualEntities.stream().noneMatch(it -> it.getEntityStatus() == ERROR))
            .isTrue();
    }

    @Test
    void testReplicationRaceConditionResolver_whenProcessInitQueueAndIsExistEntityInErrorQueue() {
        var count = 30;
        var retry = 2;
        var digitalId = podamFactory.manufacturePojo(String.class);
        var entityId = UUID.randomUUID();
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_SIGN, ERROR, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, UPDATING_COUNTERPARTY, ERROR, retry);
        var processingEntity = new ReplicationEntity()
            .digitalId(digitalId)
            .entityId(entityId)
            .entityType(UPDATING_COUNTERPARTY)
            .entityStatus(INIT);
        raceConditionResolver.resolve(processingEntity);
        var actualEntities = repository.findByEntityId(entityId);
        var actualEntitiesWithUpdatingCounterpartyType = actualEntities.stream()
            .filter(it -> it.getEntityType() == UPDATING_COUNTERPARTY)
            .collect(Collectors.toList());
        checkReplicationEntityList(actualEntitiesWithUpdatingCounterpartyType, count, CANCEL);
        var actualEntitiesWithCreatingSignType = actualEntities.stream()
            .filter(it -> it.getEntityType() == CREATING_SIGN)
            .collect(Collectors.toList());
        checkReplicationEntityList(actualEntitiesWithUpdatingCounterpartyType, count, CANCEL, retry);
        checkReplicationEntityList(actualEntitiesWithCreatingSignType, count, ERROR, retry);
    }
}

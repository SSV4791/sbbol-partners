package ru.sberbank.pprb.sbbol.partners.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;

import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.INIT;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.ERROR;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.UPDATING_COUNTERPARTY;

class ReplicationJobTest extends AbstractReplicationJobTest {

    @Autowired
    private ReplicationJob replicationJob;

    @Test
    void testProcessingInitReplicationQueue() {
        var count = 2;
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
        var count = 2;
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
        var count = 2;
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
}

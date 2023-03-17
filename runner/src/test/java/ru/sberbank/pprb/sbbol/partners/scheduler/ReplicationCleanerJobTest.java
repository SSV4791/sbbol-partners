package ru.sberbank.pprb.sbbol.partners.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.INIT;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.UPDATING_COUNTERPARTY;

class ReplicationCleanerJobTest extends AbstractReplicationJobTest {

    @Autowired
    private ReplicationProperties replicationProperties;

    @Autowired
    private ReplicationJob replicationCleanerJob;

    @Test
    void testCleaningReplicationQueue_whenExpiredPeriodHasNotCome() {
        var count = 3;
        var retry = 0;
        var digitalId = podamFactory.manufacturePojo(String.class);
        var entityId = UUID.randomUUID();
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_COUNTERPARTY, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, UPDATING_COUNTERPARTY, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_COUNTERPARTY, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_SIGN, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_SIGN, INIT, retry);
        var actualEntities = repository.findByEntityId(entityId);
        assertThat(actualEntities).asList()
                .hasSize(count * 5);
        replicationCleanerJob.run();
        actualEntities = repository.findByEntityId(entityId);
        assertThat(actualEntities).asList()
            .hasSize(count * 5);
    }

    @Test
    void testCleaningReplicationQueue_whenExpiredPeriodHasCome() {
        var count = 3;
        var retry = 0;
        var digitalId = podamFactory.manufacturePojo(String.class);
        var entityId = UUID.randomUUID();
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_COUNTERPARTY, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, UPDATING_COUNTERPARTY, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_COUNTERPARTY, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, CREATING_SIGN, INIT, retry);
        prepareTestingReplicationEntities(count, digitalId, entityId, DELETING_SIGN, INIT, retry);
        var actualEntities = repository.findByEntityId(entityId);
        assertThat(actualEntities).asList()
            .hasSize(count * 5);
        int actualExpiredPeriod = replicationProperties.getCleaner().getExpiredPeriod();
        replicationProperties.getCleaner().setExpiredPeriod(-2);
        replicationCleanerJob.run();
        replicationProperties.getCleaner().setExpiredPeriod(actualExpiredPeriod);
        actualEntities = repository.findByEntityId(entityId);
        assertThat(actualEntities).asList()
            .isEmpty();
    }
}

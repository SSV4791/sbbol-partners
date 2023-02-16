package ru.sberbank.pprb.sbbol.partners.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;
import ru.sberbank.pprb.sbbol.partners.replication.resolver.ReplicationRaceConditionResolver;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.CANCEL;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.INIT;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.ERROR;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_SIGN;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.UPDATING_COUNTERPARTY;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class ReplicationJobTest extends AbstractIntegrationTest {

    @Autowired
    private ReplicationRepository repository;

    @Autowired
    private ReplicationRaceConditionResolver raceConditionResolver;

    @Autowired
    private ReplicationJob job;

    @Autowired
    private ObjectMapper objectMapper;

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
        job.execute();
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
        job.execute();
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
        job.execute();
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

    private void checkReplicationEntityList(
        List<ReplicationEntity> entities,
        Integer expectedCount,
        ReplicationEntityStatus expectedStatus
    ) {
        checkReplicationEntityList(entities, expectedCount, expectedStatus, null);
    }

    private void checkReplicationEntityList(
        List<ReplicationEntity> entities,
        Integer expectedCount,
        ReplicationEntityStatus expectedStatus,
        Integer expectedRetry
    ) {
        assertThat(entities).asList()
            .hasSize(expectedCount);
        for (var entity : entities) {
            if (expectedCount != null) {
                assertThat(entity.getEntityStatus())
                    .isEqualTo(expectedStatus);
            }
            if (expectedRetry != null) {
                assertThat(entity.getRetry())
                    .isEqualTo(expectedRetry);
            }
        }
    }

    private List<ReplicationEntity> prepareTestingReplicationEntities(
        int count,
        String digitalId,
        UUID entityId,
        ReplicationEntityType entityType,
        ReplicationEntityStatus entityStatus,
        int retry
    ) {
        var entities = generateTestingReplicationEntities(count, digitalId, entityId, entityType, entityStatus, retry);
        putReplicationEntitiesToRepository(entities);
        return entities;
    }

    private List<ReplicationEntity> generateTestingReplicationEntities(
        int count,
        String digitalId,
        UUID entityId,
        ReplicationEntityType entityType,
        ReplicationEntityStatus entityStatus,
        int retry
    ) {
        return IntStream.range(0, count)
            .mapToObj(it -> new ReplicationEntity()
                .digitalId(digitalId)
                .entityId(entityId)
                .entityType(entityType)
                .entityStatus(entityStatus)
                .entityData(generateEntityData(entityType))
                .retry(retry))
            .collect(Collectors.toList());
    }

    @Transactional
    void putReplicationEntitiesToRepository(List<ReplicationEntity> entities) {
        entities.forEach(entity -> repository.save(entity));
    }

    private String generateEntityData(ReplicationEntityType entityType) {
        try {
            return switch (entityType) {
                case CREATING_COUNTERPARTY, UPDATING_COUNTERPARTY -> objectMapper.writeValueAsString(podamFactory.manufacturePojo(Counterparty.class));
                case DELETING_COUNTERPARTY, DELETING_SIGN -> podamFactory.manufacturePojo(String.class);
                case CREATING_SIGN -> objectMapper.writeValueAsString(podamFactory.manufacturePojo(CounterpartySignData.class));
            };
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}

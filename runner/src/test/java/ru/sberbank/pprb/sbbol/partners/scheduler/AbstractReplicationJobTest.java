package ru.sberbank.pprb.sbbol.partners.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class AbstractReplicationJobTest extends AbstractIntegrationTest {

    @Autowired
    protected ReplicationRepository repository;

    protected void checkReplicationEntityList(
        List<ReplicationEntity> entities,
        Integer expectedCount,
        ReplicationEntityStatus expectedStatus
    ) {
        checkReplicationEntityList(entities, expectedCount, expectedStatus, null);
    }

    protected void checkReplicationEntityList(
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

    protected List<ReplicationEntity> prepareTestingReplicationEntities(
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

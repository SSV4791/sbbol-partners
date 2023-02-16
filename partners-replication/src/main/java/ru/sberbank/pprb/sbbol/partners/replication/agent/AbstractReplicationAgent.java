package ru.sberbank.pprb.sbbol.partners.replication.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.ERROR;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.SUCCESS;

public abstract class AbstractReplicationAgent implements ReplicationAgent {

    protected final LegacySbbolAdapter sbbolAdapter;

    protected final ReplicationRepository replicationRepository;

    protected final ObjectMapper objectMapper;

    protected AbstractReplicationAgent(
        LegacySbbolAdapter sbbolAdapter,
        ReplicationRepository replicationRepository,
        ObjectMapper objectMapper
    ) {
        this.sbbolAdapter = sbbolAdapter;
        this.replicationRepository = replicationRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void replicate(ReplicationEntity entity) {
        try {
            replicateToSbbol(entity);
            updateReplicationEntity(entity, SUCCESS);
        } catch (Exception e) {
            updateReplicationEntity(entity, ERROR);
        }
    }

    private void updateReplicationEntity(ReplicationEntity entity, ReplicationEntityStatus newEntityStatus) {
        entity.setEntityStatus(newEntityStatus);
        entity.setRetry(entity.getRetry() + 1);
        replicationRepository.save(entity);
    }

    protected abstract void replicateToSbbol(ReplicationEntity entity) throws JsonProcessingException;
}

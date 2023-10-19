package ru.sberbank.pprb.sbbol.partners.replication.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.ERROR;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.SUCCESS;

public abstract class AbstractReplicationAgent implements ReplicationAgent {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractReplicationAgent.class);

    private static final String ERROR_MESSAGE_FOR_SBBOL_EXCEPTION = "Ошибка репликации в СББОЛ Legacy. {}";

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
            LOG.error(ERROR_MESSAGE_FOR_SBBOL_EXCEPTION, e.getMessage());
            updateReplicationEntity(entity, ERROR, e.getMessage());
        }
    }

    private void updateReplicationEntity(ReplicationEntity entity, ReplicationEntityStatus newEntityStatus) {
        updateReplicationEntity(entity, newEntityStatus, null);
    }

    private void updateReplicationEntity(
        ReplicationEntity entity, ReplicationEntityStatus newEntityStatus, String errorMessage) {
        entity.setEntityStatus(newEntityStatus);
        entity.setRetry(entity.getRetry() + 1);
        entity.setErrorMessage(errorMessage);
        replicationRepository.save(entity);
    }

    protected abstract void replicateToSbbol(ReplicationEntity entity) throws JsonProcessingException;
}

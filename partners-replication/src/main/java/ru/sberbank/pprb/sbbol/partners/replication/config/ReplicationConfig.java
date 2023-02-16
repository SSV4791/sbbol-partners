package ru.sberbank.pprb.sbbol.partners.replication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.replication.agent.ReplicationAgentRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.agent.impl.CreatingCounterpartyReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.agent.impl.CreatingSignReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.agent.impl.DeletingSignReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.agent.impl.ReplicationAgentRegistryImpl;
import ru.sberbank.pprb.sbbol.partners.replication.handler.ReplicationEntityHandler;
import ru.sberbank.pprb.sbbol.partners.replication.handler.ReplicationQueueHandler;
import ru.sberbank.pprb.sbbol.partners.replication.handler.impl.ReplicationEntityHandlerImpl;
import ru.sberbank.pprb.sbbol.partners.replication.handler.impl.ReplicationQueueHandlerImpl;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;
import ru.sberbank.pprb.sbbol.partners.replication.job.impl.ReplicationJobImpl;
import ru.sberbank.pprb.sbbol.partners.replication.agent.ReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.agent.impl.DeletingCounterpartyReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.agent.impl.UpdatingCounterpartyReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityMapper;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityMapperRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.impl.CreatingCounterpartyReplicationMapper;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.impl.CreatingSignReplicationMapper;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.impl.DeletingCounterpartyReplicationMapper;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.impl.DeletingSignReplicationMapper;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.impl.ReplicationEntityMapperRegistryImpl;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.impl.UpdatingCounterpartyReplicationMapper;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;
import ru.sberbank.pprb.sbbol.partners.replication.resolver.ReplicationRaceConditionResolver;
import ru.sberbank.pprb.sbbol.partners.replication.resolver.impl.ReplicationRaceConditionResolverImpl;

import java.util.List;

@Configuration
public class ReplicationConfig {

    private final ReplicationProperties replicationProperty;

    public ReplicationConfig(ReplicationProperties replicationProperty) {
        this.replicationProperty = replicationProperty;
    }

    @Bean
    public ReplicationJob replicationJob(ReplicationQueueHandler replicationQueueHandler) {
        return new ReplicationJobImpl(
            replicationProperty,
            replicationQueueHandler
        );
    }

    @Bean
    public ReplicationQueueHandler replicationQueueHandler(
        ReplicationRepository replicationRepository,
        ReplicationEntityHandler replicationEntityHandler
    ) {
        return new ReplicationQueueHandlerImpl(
            replicationProperty,
            replicationRepository,
            replicationEntityHandler
        );
    }

    @Bean
    ReplicationRaceConditionResolver replicationRaceConditionResolver(
        ReplicationProperties replicationProperties,
        ReplicationRepository replicationRepository
    ) {
        return new ReplicationRaceConditionResolverImpl(replicationProperties, replicationRepository);
    }

    @Bean
    public ReplicationEntityHandler replicationEntityHandler(
        ReplicationAgentRegistry replicationAgentRegistry,
        ReplicationRaceConditionResolver raceConditionResolver
        ) {
        return new ReplicationEntityHandlerImpl(replicationAgentRegistry, raceConditionResolver);
    }

    @Bean
    public ReplicationAgentRegistry replicationAgentRegistry(List<ReplicationAgent> agents) {
        return new ReplicationAgentRegistryImpl(agents);
    }

    @Bean
    public ReplicationAgent creatingCounterpartyReplicationAgent(
        LegacySbbolAdapter sbbolAdapter,
        ReplicationRepository replicationRepository,
        ObjectMapper objectMapper
    ) {
        return new CreatingCounterpartyReplicationAgent(
            sbbolAdapter,
            replicationRepository,
            objectMapper
            );
    }

    @Bean
    public ReplicationAgent updatingCounterpartyReplicationAgent(
        LegacySbbolAdapter sbbolAdapter,
        ReplicationRepository replicationRepository,
        ObjectMapper objectMapper
    ) {
        return new UpdatingCounterpartyReplicationAgent(
            sbbolAdapter,
            replicationRepository,
            objectMapper
        );
    }

    @Bean
    public ReplicationAgent deletingCounterpartyReplicationAgent(
        LegacySbbolAdapter sbbolAdapter,
        ReplicationRepository replicationRepository,
        ObjectMapper objectMapper
    ) {
        return new DeletingCounterpartyReplicationAgent(
            sbbolAdapter,
            replicationRepository,
            objectMapper
        );
    }

    @Bean
    public ReplicationAgent creatingSignReplicationAgent(
        LegacySbbolAdapter sbbolAdapter,
        ReplicationRepository replicationRepository,
        ObjectMapper objectMapper
    ) {
        return new CreatingSignReplicationAgent(
            sbbolAdapter,
            replicationRepository,
            objectMapper
        );
    }

    @Bean
    public ReplicationAgent deletingSignReplicationAgent(
        LegacySbbolAdapter sbbolAdapter,
        ReplicationRepository replicationRepository,
        ObjectMapper objectMapper
    ) {
        return new DeletingSignReplicationAgent(
            sbbolAdapter,
            replicationRepository,
            objectMapper
        );
    }

    @Bean
    ReplicationEntityMapperRegistry replicationEntityMapperRegistry(List<ReplicationEntityMapper> mappers) {
        return new ReplicationEntityMapperRegistryImpl(mappers);
    }

    @Bean
    ReplicationEntityMapper creatingCounterpartyReplicationMapper(ObjectMapper objectMapper) {
        return new CreatingCounterpartyReplicationMapper(objectMapper);
    }

    @Bean
    ReplicationEntityMapper updatingCounterpartyReplicationMapper(ObjectMapper objectMapper) {
        return new UpdatingCounterpartyReplicationMapper(objectMapper);
    }

    @Bean
    ReplicationEntityMapper deletingCounterpartyReplicationMapper(ObjectMapper objectMapper) {
        return new DeletingCounterpartyReplicationMapper(objectMapper);
    }

    @Bean
    ReplicationEntityMapper creatingSignReplicationMapper(ObjectMapper objectMapper) {
        return new CreatingSignReplicationMapper(objectMapper);
    }

    @Bean
    ReplicationEntityMapper deletingSignReplicationMapper(ObjectMapper objectMapper) {
        return new DeletingSignReplicationMapper(objectMapper);
    }
}

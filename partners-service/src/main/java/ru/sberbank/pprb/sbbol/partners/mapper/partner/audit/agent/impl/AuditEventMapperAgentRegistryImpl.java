package ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgent;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgentRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuditEventMapperAgentRegistryImpl implements AuditEventMapperAgentRegistry {
    private final Map<String, AuditEventMapperAgent> registry;

    public AuditEventMapperAgentRegistryImpl(List<AuditEventMapperAgent> agents) {
        this.registry = CollectionUtils.isEmpty(agents)
            ? Collections.emptyMap()
            : agents.stream().collect(Collectors.toMap(AuditEventMapperAgent::getEventName, it -> it));
    }

    @Override
    public Optional<AuditEventMapperAgent> findAgent(String eventName) {
        return Optional.ofNullable(registry.get(eventName));
    }
}

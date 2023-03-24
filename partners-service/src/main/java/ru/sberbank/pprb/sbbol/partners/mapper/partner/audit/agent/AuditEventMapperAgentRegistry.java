package ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent;

import java.util.Optional;

public interface AuditEventMapperAgentRegistry {

    Optional<AuditEventMapperAgent> findAgent(String eventName);
}

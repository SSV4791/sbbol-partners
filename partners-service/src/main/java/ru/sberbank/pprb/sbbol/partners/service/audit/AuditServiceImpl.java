package ru.sberbank.pprb.sbbol.partners.service.audit;

import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.audit.model.Event;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgentRegistry;

import java.util.Map;

public class AuditServiceImpl implements AuditService {
    private final AuditAdapter auditAdapter;
    private final AuditEventMapperAgentRegistry auditEventMapperAgentRegistry;

    public AuditServiceImpl(AuditAdapter auditAdapter, AuditEventMapperAgentRegistry auditEventMapperAgentRegistry) {
        this.auditAdapter = auditAdapter;
        this.auditEventMapperAgentRegistry = auditEventMapperAgentRegistry;
    }

    @Override
    public <T> void send(String eventName, T value) {
        var mapper = auditEventMapperAgentRegistry
            .findAgent(eventName)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Отсутствует зарегистрированный AuditEventMapperAgent для типа события: %s", eventName))
            )
            .getAuditEventMapper();
        send(eventName, mapper.toEventParam(value));
    }

    @Override
    public void send(String eventName, Map<String, String> eventParams) {
        var event = new Event()
            .eventName(eventName)
            .eventParams(eventParams);
        auditAdapter.send(event);
    }
}

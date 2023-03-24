package ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl;

import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.EventParamMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgent;

import java.util.HashMap;

import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.ACCOUNTS_DELETE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.DIGITAL_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.IDS;

public class AccountsDeleteSuccessAuditMapperAgent implements AuditEventMapperAgent {

    private final EventParamMapper<Object[]> mapper = args -> {
        var params = new HashMap<String, String>();
        putEventParam(DIGITAL_ID, args[0], params);
        putEventParam(IDS, args[1], params);
        return params;
    };

    @Override
    public String getEventName() {
        return ACCOUNTS_DELETE.getSuccessEventName();
    }

    @Override
    public EventParamMapper<Object[]> getAuditEventMapper() {
        return mapper;
    }
}

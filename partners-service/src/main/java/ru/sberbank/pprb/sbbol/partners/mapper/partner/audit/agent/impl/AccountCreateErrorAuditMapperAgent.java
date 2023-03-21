package ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl;

import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.EventParamMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgent;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;

import java.util.HashMap;

import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.ACCOUNT_CREATE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.ACCOUNT;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.BANK;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.COMMENT;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.DIGITAL_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.PARTNER_ID;

public class AccountCreateErrorAuditMapperAgent implements AuditEventMapperAgent {

    private final EventParamMapper<Object[]> mapper = args -> {
        var params = new HashMap<String, String>();
        var account = (AccountCreate) args[0];
        putEventParam(PARTNER_ID, account.getPartnerId(), params);
        putEventParam(DIGITAL_ID, account.getDigitalId(), params);
        putEventParam(ACCOUNT, account.getAccount(), params);
        putEventParam(BANK, account.getBank(), params);
        putEventParam(COMMENT, account.getComment(), params);
        return params;
    };

    @Override
    public String getEventName() {
        return ACCOUNT_CREATE.getErrorEventName();
    }

    @Override
    public EventParamMapper<Object[]> getAuditEventMapper() {
        return mapper;
    }
}

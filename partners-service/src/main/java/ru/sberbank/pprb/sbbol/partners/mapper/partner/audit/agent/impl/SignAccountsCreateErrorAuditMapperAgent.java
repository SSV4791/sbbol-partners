package ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl;

import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.EventParamMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgent;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;

import java.util.HashMap;

import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.SIGN_ACCOUNTS_CREATE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.ACCOUNTS_SIGN_DETAIL;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.DIGITAL_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.DIGITAL_USER_ID;

public class SignAccountsCreateErrorAuditMapperAgent implements AuditEventMapperAgent {

    private final EventParamMapper<Object[]> mapper = args -> {
        var params = new HashMap<String, String>();
        var accountsSignInfo = (AccountsSignInfo) args[0];
        putEventParam(DIGITAL_ID, accountsSignInfo.getDigitalId(), params);
        putEventParam(DIGITAL_USER_ID, accountsSignInfo.getDigitalUserId(), params);
        putEventParam(ACCOUNTS_SIGN_DETAIL, accountsSignInfo.getAccountsSignDetail(), params);
        return params;
    };

    @Override
    public String getEventName() {
        return SIGN_ACCOUNTS_CREATE.getErrorEventName();
    }

    @Override
    public EventParamMapper<Object[]> getAuditEventMapper() {
        return mapper;
    }
}

package ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl;

import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.EventParamMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgent;

import java.util.HashMap;

import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.SIGN_ACCOUNTS_CREATE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.ACCOUNTS_SIGN_DETAIL;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.DIGITAL_ID;

public class SignAccountsCreateSuccessAuditMapperAgent implements AuditEventMapperAgent {

    private final EventParamMapper<AccountsSignInfoResponse> mapper = accountsSignInfo -> {
        var params = new HashMap<String, String>();
        putEventParam(DIGITAL_ID, accountsSignInfo.getDigitalId(), params);
        putEventParam(ACCOUNTS_SIGN_DETAIL, accountsSignInfo.getAccountsSignDetail(), params);
        return params;
    };

    @Override
    public String getEventName() {
        return SIGN_ACCOUNTS_CREATE.getSuccessEventName();
    }

    @Override
    public EventParamMapper<AccountsSignInfoResponse> getAuditEventMapper() {
        return mapper;
    }
}

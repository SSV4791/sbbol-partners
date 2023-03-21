package ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl;

import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.EventParamMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgent;
import ru.sberbank.pprb.sbbol.partners.model.Account;

import java.util.HashMap;

import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.ACCOUNT_CREATE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.ACCOUNT;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.BANK;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.BUDGET;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.COMMENT;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.DIGITAL_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.PARTNER_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.PRIORITY_ACCOUNT;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.STATE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.VERSION;

public class AccountCreateSuccessAuditMapperAgent implements AuditEventMapperAgent {

    private final EventParamMapper<Account> mapper = account -> {
        var params = new HashMap<String, String>();
        putEventParam(ID, account.getId(), params);
        putEventParam(PARTNER_ID, account.getPartnerId(), params);
        putEventParam(DIGITAL_ID, account.getDigitalId(), params);
        putEventParam(VERSION, account.getVersion(), params);
        putEventParam(BUDGET, account.getBudget(), params);
        putEventParam(ACCOUNT, account.getAccount(), params);
        putEventParam(PRIORITY_ACCOUNT, account.getPriorityAccount(), params);
        putEventParam(BANK, account.getBank(), params);
        putEventParam(STATE, account.getState(), params);
        putEventParam(COMMENT, account.getComment(), params);
        return params;
    };

    @Override
    public String getEventName() {
        return ACCOUNT_CREATE.getSuccessEventName();
    }

    @Override
    public EventParamMapper<Account> getAuditEventMapper() {
        return mapper;
    }
}

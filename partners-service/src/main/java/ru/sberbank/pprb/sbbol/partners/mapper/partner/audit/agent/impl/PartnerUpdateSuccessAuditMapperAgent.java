package ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.impl;

import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.EventParamMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgent;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import java.util.HashMap;

import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_UPDATE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.CITIZENSHIP;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.COMMENT;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.DIGITAL_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.EMAILS;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.FIRST_NAME;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.GKU;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.INN;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.KPP;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.LEGAL_FORM;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.MIDDLE_NAME;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.OGRN;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.OKPO;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.ORG_NAME;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.PHONES;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.SECOND_NAME;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.EventParamNames.VERSION;

public class PartnerUpdateSuccessAuditMapperAgent implements AuditEventMapperAgent {

    private final EventParamMapper<Partner> mapper = partner -> {
        var params = new HashMap<String, String>();
        putEventParam(ID, partner.getId(), params);
        putEventParam(DIGITAL_ID, partner.getDigitalId(), params);
        putEventParam(VERSION, partner.getVersion(), params);
        putEventParam(LEGAL_FORM, partner.getLegalForm(), params);
        putEventParam(ORG_NAME, partner.getOrgName(), params);
        putEventParam(FIRST_NAME, partner.getFirstName(), params);
        putEventParam(SECOND_NAME, partner.getSecondName(), params);
        putEventParam(MIDDLE_NAME, partner.getMiddleName(), params);
        putEventParam(INN, partner.getInn(), params);
        putEventParam(KPP, partner.getKpp(), params);
        putEventParam(OGRN, partner.getOgrn(), params);
        putEventParam(OKPO, partner.getOkpo(), params);
        putEventParam(PHONES, partner.getPhones(), params);
        putEventParam(EMAILS, partner.getEmails(), params);
        putEventParam(COMMENT, partner.getComment(), params);
        putEventParam(GKU, partner.getGku(), params);
        putEventParam(CITIZENSHIP, partner.getCitizenship(), params);
        return params;
    };

    @Override
    public String getEventName() {
        return PARTNER_UPDATE.getSuccessEventName();
    }

    @Override
    public EventParamMapper<Partner> getAuditEventMapper() {
        return mapper;
    }
}

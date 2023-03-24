package ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent;

import org.springframework.util.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.EventParamMapper;

import java.util.Map;

public interface AuditEventMapperAgent {
    /**
     * @return Имя события
     */
    String getEventName();

    /**
     * @return Маппер события
     */
    EventParamMapper getAuditEventMapper();

    default void putEventParam(String field, Object value, Map<String, String> map) {
        if (!ObjectUtils.isEmpty(value)) {
            map.put(field, value.toString());
        }
    }
}

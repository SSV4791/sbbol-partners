package ru.sberbank.pprb.sbbol.partners.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import ru.sberbank.pprb.sbbol.audit.api.DefaultApi;
import ru.sberbank.pprb.sbbol.partners.audit.exception.AuditSendException;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapper;
import ru.sberbank.pprb.sbbol.partners.audit.model.Event;

import java.util.Map;

public class AuditAdapterImpl implements AuditAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditAdapterImpl.class);

    private final DefaultApi auditApi;
    private final AuditMapper auditMapper;
    private final boolean auditEnabled;
    private final String defaultXNodeId;

    public AuditAdapterImpl(
        boolean auditEnabled,
        String defaultXNodeId,
        DefaultApi auditApi,
        AuditMapper auditMapper
    ) {
        this.auditEnabled = auditEnabled;
        this.defaultXNodeId = defaultXNodeId;
        this.auditApi = auditApi;
        this.auditMapper = auditMapper;
    }

    @Override
    public void send(Event auditEvent) {
        if (!auditEnabled) {
            LOGGER.warn("Интеграция c сервисом Audit отключена");
            return;
        }
        var event = auditMapper.toAuditEvent(auditEvent, Map.of(
            "userNode", getXNodeId()
        ));
        try {
            var response = auditApi.uploadEventWithHttpInfo(getXNodeId(), event, null);
            var statusCode = response.getStatusCode();
            if (statusCode.isError()) {
                throw new AuditSendException(statusCode, response);
            }
        } catch (RestClientException e) {
            LOGGER.error("Ошибка при отправке события в сервисе Audit", e);
        }
    }

    private String getXNodeId() {
        var xNodeId = System.getenv("NODE_NAME");
        return StringUtils.hasText(xNodeId) ? xNodeId : defaultXNodeId;
    }
}

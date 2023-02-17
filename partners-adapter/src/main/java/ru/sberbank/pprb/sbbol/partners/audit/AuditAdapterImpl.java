package ru.sberbank.pprb.sbbol.partners.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import ru.sberbank.pprb.sbbol.audit.api.DefaultApi;
import ru.sberbank.pprb.sbbol.audit.model.AuditMetamodel;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.audit.exception.AuditSendException;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapper;
import ru.sberbank.pprb.sbbol.partners.audit.model.Event;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@Loggable
public class AuditAdapterImpl implements AuditAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditAdapterImpl.class);

    private final DefaultApi auditApi;
    private final AuditMapper auditMapper;
    private final boolean auditEnabled;
    private final Resource metaModel;
    private final String defaultXNodeId;

    private String moduleName;
    private String metamodelVersion;

    public AuditAdapterImpl(
        boolean auditEnabled,
        Resource metaModel,
        String defaultXNodeId,
        DefaultApi auditApi,
        AuditMapper auditMapper
    ) {
        this.auditEnabled = auditEnabled;
        this.metaModel = metaModel;
        this.defaultXNodeId = defaultXNodeId;
        this.auditApi = auditApi;
        this.auditMapper = auditMapper;
    }

    @PostConstruct
    private void registerMetaModel() {
        if (!auditEnabled) {
            LOGGER.warn("Интеграция c сервисом Audit отключена");
            return;
        }
        try {
            var auditMetamodel =
                new ObjectMapper().readValue(metaModel.getInputStream(), AuditMetamodel.class);
            moduleName = auditMetamodel.getModule();
            metamodelVersion = auditMetamodel.getMetamodelVersion();
        } catch (FileNotFoundException e) {
            LOGGER.error("Ошибка получения файла auditMetamodel, не найден в classpath", e);
        } catch (IOException e) {
            LOGGER.error("Ошибка обработки файла auditMetamodel", e);
        }
    }

    @Override
    public void send(Event auditEvent) {
        if (!auditEnabled) {
            LOGGER.warn("Интеграция c сервисом Audit отключена");
            return;
        }
        var event = auditMapper.toAuditEvent(auditEvent, Map.of(
            "moduleMame", moduleName,
            "metamodelVersion", metamodelVersion,
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

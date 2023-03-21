package ru.sberbank.pprb.sbbol.partners.service.audit;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.audit.model.EventType;
import ru.sberbank.pprb.sbbol.partners.config.AuditConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.audit.agent.AuditEventMapperAgentRegistry;

@ContextConfiguration(
    classes = {
        AuditConfiguration.class
    }
)
public class AuditEventMapperAgentRegistryTest extends BaseUnitConfiguration {

    @Autowired
    private AuditEventMapperAgentRegistry auditEventMapperAgentRegistry;

    @MockBean
    AuditAdapter auditAdapter;

    @Test
    @DisplayName("Проверка реализованы ли агенты для каждого типа события")
    void checkAllAgents() {
        var eventTypes = Allure.step("Получение всех EventTypes", EventType::values);
        for (var eventType : eventTypes) {
            Allure.step("Проверка наличия AuditEventMapperAgent для успешного события", () ->
                Assertions.assertDoesNotThrow(() -> {
                    auditEventMapperAgentRegistry
                        .findAgent(eventType.getSuccessEventName())
                        .orElseThrow(() -> new IllegalArgumentException(
                            String.format("Отсутствует зарегистрированный AuditEventMapperAgent для типа события: %s", eventType.getSuccessEventName()))
                        );
                })
            );
            Allure.step("Проверка наличия AuditEventMapperAgent для неуспешного события", () ->
                Assertions.assertDoesNotThrow(() -> {
                    auditEventMapperAgentRegistry
                        .findAgent(eventType.getErrorEventName())
                        .orElseThrow(() -> new IllegalArgumentException(
                            String.format("Отсутствует зарегистрированный AuditEventMapperAgent для типа события: %s", eventType.getErrorEventName()))
                        );
                })
            );
        }
    }
}

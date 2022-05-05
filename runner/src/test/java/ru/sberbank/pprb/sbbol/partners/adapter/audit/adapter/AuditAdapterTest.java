package ru.sberbank.pprb.sbbol.partners.adapter.audit.adapter;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import ru.sberbank.pprb.sbbol.partners.adapter.audit.config.AuditAdapterConfiguration;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.audit.model.Event;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@ContextConfiguration(classes = {
    AuditAdapterConfiguration.class,
})
@TestPropertySource(value = "classpath:audit/auditMetamodel.json")
class AuditAdapterTest extends BaseUnitConfiguration {

    @Autowired
    private AuditAdapter adapter;

    /**
     * {@link ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter#send(Event)}
     */
    @Test
    @AllureId("34036")
    @DisplayName("Адаптер Аудит. Проверка отправки Event")
    void checkMigrationTest() {
        var event = factory.manufacturePojo(Event.class);
        assertDoesNotThrow(() -> adapter.send(event));
    }
}

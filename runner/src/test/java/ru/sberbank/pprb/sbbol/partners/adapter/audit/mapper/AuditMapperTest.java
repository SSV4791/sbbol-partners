package ru.sberbank.pprb.sbbol.partners.adapter.audit.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.audit.model.AuditEventParams;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapper;
import ru.sberbank.pprb.sbbol.partners.audit.model.Event;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;

import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

class AuditMapperTest extends BaseUnitConfiguration {

    private static final AuditMapper mapper = Mappers.getMapper(AuditMapper.class);

    @Test
    void toAuditEvent() {
        var moduleName = randomAlphabetic(20);
        var metamodelVersion = randomAlphabetic(20);
        var xNodeId = randomAlphabetic(20);
        Map<String, String> expectedParams = Map.of(
            "moduleMame", moduleName,
            "metamodelVersion", metamodelVersion,
            "userNode", xNodeId
        );
        var expectedEvent = factory.manufacturePojo(Event.class);
        var event = mapper.toAuditEvent(expectedEvent, expectedParams);
        assertThat(event.getName()).isEqualTo(AuditMapper.toName(expectedEvent.getEventType()));
        assertThat(event.getMetamodelVersion()).isEqualTo(AuditMapper.toMetamodelVersion(expectedParams));
        assertThat(event.getModule()).isEqualTo(AuditMapper.toModelName(expectedParams));
        assertThat(event.getUserLogin()).isEqualTo(AuditMapper.toModelName(expectedParams));
        assertThat(event.getUserNode()).isEqualTo(AuditMapper.toUserNode(expectedParams));
        assertThat(event.getParams()).isNotNull();
        for (AuditEventParams param : event.getParams()) {
            assertThat(expectedEvent.getEventParams().keySet())
                .contains(param.getName());
            assertThat(expectedEvent.getEventParams().values())
                .contains(param.getValue());
        }
    }
}

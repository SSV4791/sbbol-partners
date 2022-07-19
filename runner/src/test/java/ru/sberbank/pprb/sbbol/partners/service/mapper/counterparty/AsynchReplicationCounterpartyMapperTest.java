package ru.sberbank.pprb.sbbol.partners.service.mapper.counterparty;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.AsynchReplicationCounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.AsynchReplicationCounterpartyMapperImpl;
import ru.sberbank.pprb.sbbol.partners.service.replication.dto.AsynchReplicationCounterparty;

import static org.assertj.core.api.Assertions.assertThat;

class AsynchReplicationCounterpartyMapperTest extends BaseUnitConfiguration {

    private static final AsynchReplicationCounterpartyMapper mapper = new AsynchReplicationCounterpartyMapperImpl();

    @Test
    void testMapToAsynchReplicationCounterparty_whenCounterpartyIsDefined() {
        var expectedOperation = factory.manufacturePojo(AsynchReplicationCounterparty.Operation.class);
        var expectedDigitalId = factory.manufacturePojo(String.class);
        var expectedCounterparty = factory.manufacturePojo(Counterparty.class);
        var expectedCounterpartySignData = factory.manufacturePojo(CounterpartySignData.class);
        var actualAsynchReplicationCounterparty = mapper.mapToAsynchReplicationCounterparty(
            expectedOperation,
            expectedDigitalId,
            expectedCounterparty,
            expectedCounterpartySignData);
        assertThat(actualAsynchReplicationCounterparty)
            .isNotNull();
        assertThat(actualAsynchReplicationCounterparty.getOperation())
            .isEqualTo(expectedOperation);
        assertThat(actualAsynchReplicationCounterparty.getDigitalId())
            .isEqualTo(expectedDigitalId);
        assertThat(actualAsynchReplicationCounterparty.getCounterparty())
            .isEqualTo(expectedCounterparty);
        assertThat(actualAsynchReplicationCounterparty.getSignData())
            .isEqualTo(expectedCounterpartySignData);
    }

    @Test
    void testMapToAsynchReplicationCounterparty_whenCounterpartyIdIsDefined() {
        var expectedOperation = factory.manufacturePojo(AsynchReplicationCounterparty.Operation.class);
        var expectedDigitalId = factory.manufacturePojo(String.class);
        var expectedCounterpartyId = factory.manufacturePojo(String.class);
        var actualAsynchReplicationCounterparty = mapper.mapToAsynchReplicationCounterparty(
            expectedOperation,
            expectedDigitalId,
            expectedCounterpartyId);
        assertThat(actualAsynchReplicationCounterparty)
            .isNotNull();
        assertThat(actualAsynchReplicationCounterparty.getOperation())
            .isEqualTo(expectedOperation);
        assertThat(actualAsynchReplicationCounterparty.getDigitalId())
            .isEqualTo(expectedDigitalId);
        assertThat(actualAsynchReplicationCounterparty.getCounterparty().getPprbGuid())
            .isEqualTo(expectedCounterpartyId);
    }
}

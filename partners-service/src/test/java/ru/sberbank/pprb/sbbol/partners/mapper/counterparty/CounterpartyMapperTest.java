package ru.sberbank.pprb.sbbol.partners.mapper.counterparty;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.counterparties.model.CounterpartySearchRequest;
import ru.sberbank.pprb.sbbol.partners.model.CounterpartyCheckRequisites;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CounterpartyMapperTest {

    private static final CounterpartyMapper mapper = Mappers.getMapper(CounterpartyMapper.class);
    private static final PodamFactory factory = new PodamFactoryImpl();

    @Test
    void toCounterpartyCheckRequisites() {
        var searchRequest = factory.manufacturePojo(CounterpartySearchRequest.class);
        CounterpartyCheckRequisites response = mapper.toCounterpartyCheckRequisites(searchRequest);
        assertEquals(searchRequest.getAccountNumber(), response.getAccountNumber());
        assertEquals(searchRequest.getBankAccount(), response.getBankAccount());
        assertEquals(searchRequest.getBankBic(), response.getBankBic());
        assertEquals(searchRequest.getDigitalId(), response.getDigitalId());
        assertEquals(searchRequest.getKpp(), response.getKpp());
        assertEquals(searchRequest.getName(), response.getName());
        assertEquals(searchRequest.getTaxNumber(), response.getTaxNumber());
    }
}

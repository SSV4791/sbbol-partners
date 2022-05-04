package ru.sberbank.pprb.sbbol.partners.config;

import org.apache.commons.lang.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyView;
import ru.sberbank.pprb.sbbol.partners.legacy.model.ListResponse;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Pagination;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractIntegrationWithSbbolTest extends AbstractIntegrationTest {

    @MockBean
    protected LegacySbbolAdapter legacySbbolAdapter;

    @Mock
    protected CounterpartyMapper counterpartyMapper;

    @Autowired
    private PodamFactory podamFactory;

    protected static final String newAcc = "40702840876545678702";
    protected static final String newKpp = "999999999";
    protected Counterparty counterparty;
    protected Counterparty updatedCounterparty;
    protected CounterpartyView counterpartyView;

    @BeforeEach
    void init() {
        counterparty = podamFactory.manufacturePojo(Counterparty.class);
        counterpartyView = podamFactory.manufacturePojo(CounterpartyView.class);
        counterparty.setPprbGuid(UUID.randomUUID().toString());
        counterparty.setAccount("40802810500490014206");
        counterparty.setBankBic("044525411");
        counterparty.setCorrAccount("30101810145250000411");
        counterpartyView.setPprbGuid(UUID.randomUUID().toString());
        counterpartyMapper = Mappers.getMapper(CounterpartyMapper.class);
        when(legacySbbolAdapter.checkNotMigration(any())).thenReturn(true);
        when(legacySbbolAdapter.create(any(), any())).thenReturn(counterparty);
        when(legacySbbolAdapter.getByPprbGuid(any(), any()))
            .thenReturn((Counterparty) SerializationUtils.clone(counterparty))
            .thenReturn((Counterparty) SerializationUtils.clone(counterparty));
        ListResponse<CounterpartyView> viewResponse = new ListResponse<>();
        viewResponse.setItems(Collections.singletonList(counterpartyView));
        viewResponse.setPagination(podamFactory.manufacturePojo(Pagination.class));
        when(legacySbbolAdapter.viewRequest(any(), any())).thenReturn(viewResponse);
        updatedCounterparty = (Counterparty) SerializationUtils.clone(counterparty);
        updatedCounterparty.setAccount(newAcc);
        updatedCounterparty.setKpp(newKpp);
        when(legacySbbolAdapter.update(any(), any())).thenReturn(updatedCounterparty);
    }
}

package ru.sberbank.pprb.sbbol.partners.pact.producer;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import com.sbt.pprb.integration.hibernate.standin.StandinPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.dcbqa.allureee.annotations.layers.CdcProviderTestLayer;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.pact.PactData;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.service.partner.PhoneService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@CdcProviderTestLayer
@IgnoreNoPactsToVerify
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PactBroker
@Provider("sbbol-partners--CIBPPRB")
public class SbbolCounterpartiesProducerTest {
    private static final String HOST = "localhost";
    @LocalServerPort
    protected int port;
    private static final String BASE_STATE = "base_state";
    private final PactData pactData;

    public SbbolCounterpartiesProducerTest() {
        this.pactData = new PactData();
    }

    @MockBean
    private LegacySbbolAdapter legacySbbolAdapter;

    @MockBean
    private StandinPlugin standinPlugin;

    @MockBean
    private PartnerService partnerService;

    @MockBean(name = "partnerPhoneService")
    private PhoneService phoneService;

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget(HOST, port));

        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());

        when(standinPlugin.getConfiguration())
            .thenReturn(Mockito.mock(StandinPlugin.Configuration.class));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State(BASE_STATE)
    void baseState() {
        when(partnerService.getPartner(PactData.DIGITAL_ID, PactData.UUID_ID))
            .thenReturn(pactData.partner());
        when(partnerService.savePartner(pactData.partnerCreateFullModel()))
            .thenReturn(pactData.partnerCreateFullModelResponse());
        when(partnerService.savePartner(pactData.partnerCreate()))
            .thenReturn(pactData.partner());
        when(partnerService.updatePartner(pactData.partner()))
            .thenReturn(pactData.partner());
        when(phoneService.savePhone(pactData.phoneCreate()))
            .thenReturn(pactData.phone());
    }
}

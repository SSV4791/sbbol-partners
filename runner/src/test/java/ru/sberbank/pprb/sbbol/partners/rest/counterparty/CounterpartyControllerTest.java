package ru.sberbank.pprb.sbbol.partners.rest.counterparty;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.sberbank.pprb.sbbol.counterparties.model.CheckPayeeRequisitesResult;
import ru.sberbank.pprb.sbbol.counterparties.model.CounterpartySearchRequest;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyCheckRequisitesResult;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CounterpartyControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/counterparty";

    @MockBean
    private LegacySbbolAdapter legacySbbolAdapter;

    @Test
    void getNotFoundCheckRequisites() {
        var request = new CounterpartySearchRequest();
        request.setBankBic("123456789");
        request.setTaxNumber("111");

        when(legacySbbolAdapter.checkRequisites(any())).thenReturn(new CounterpartyCheckRequisitesResult(null, null));

        var response = given()
            .spec(requestSpec)
            .body(request)
            .when()
            .post(baseRoutePath + "/check-requisites")
            .then()
            .spec(responseSpec)
            .extract()
            .as(CheckPayeeRequisitesResult.class);

        assertNull(response.getPprbGuid());
        assertEquals(CheckPayeeRequisitesResult.StatusEnum.NOTFOUND, response.getStatus());
    }

    @Test
    void getNotSignedCheckRequisites() {
        String guid = UUID.randomUUID().toString();
        var request = new CounterpartySearchRequest();
        request.setBankBic("123456789");
        request.setTaxNumber("111");

        when(legacySbbolAdapter.checkRequisites(any())).thenReturn(new CounterpartyCheckRequisitesResult(guid, false));

        var response = given()
            .spec(requestSpec)
            .body(request)
            .when()
            .post(baseRoutePath + "/check-requisites")
            .then()
            .spec(responseSpec)
            .extract()
            .as(CheckPayeeRequisitesResult.class);

        assertNotNull(response.getPprbGuid());
        assertEquals(CheckPayeeRequisitesResult.StatusEnum.NOTSIGNED, response.getStatus());
    }

    @Test
    void getSignedCheckRequisites() {
        String guid = UUID.randomUUID().toString();
        var request = new CounterpartySearchRequest();
        request.setBankBic("123456789");
        request.setTaxNumber("111");

        when(legacySbbolAdapter.checkRequisites(any())).thenReturn(new CounterpartyCheckRequisitesResult(guid, true));

        var response = given()
            .spec(requestSpec)
            .body(request)
            .when()
            .post(baseRoutePath + "/check-requisites")
            .then()
            .spec(responseSpec)
            .extract()
            .as(CheckPayeeRequisitesResult.class);

        assertNotNull(response.getPprbGuid());
        assertEquals(CheckPayeeRequisitesResult.StatusEnum.SIGNED, response.getStatus());
    }
}

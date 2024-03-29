package ru.sberbank.pprb.sbbol.partners.rest.renter;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.renter.model.RenterIdentifier;
import ru.sberbank.pprb.sbbol.renter.model.RenterListResponse;
import ru.sberbank.pprb.sbbol.renter.model.Version;

import static io.restassured.RestAssured.given;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.renter.RenterUtils.getValidRenter;

class RenterControllerTest extends AbstractIntegrationTest {

    private static final String baseRoutePath = "/sbbol-partners/renter";

    @Test
    void version() {
        Version version = given()
            .spec(requestSpec)
            .when()
            .get(baseRoutePath + "/version")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Version.class);

        assertThat(version)
            .isNotNull()
            .isEqualTo(new Version().ver("1.0.0"));
    }

    @Test
    void testCreateValidRenter() {
        Renter renter = getValidRenter();
        Renter result = given()
            .spec(requestSpec)
            .body(renter)
            .when()
            .post(baseRoutePath + "/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);

        assertThat(result)
            .isNotNull()
            .usingRecursiveComparison()
            .ignoringFields("uuid")
            .isEqualTo(renter);
        assertThat(result.getCheckResults()).isNull();
    }

    @Test
    void testCreateInvalidRenter() {
        Renter renter = getValidRenter();
        renter.setOgrn("1");
        Renter result = given()
            .spec(requestSpec)
            .body(renter)
            .when()
            .post(baseRoutePath + "/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);

        assertThat(result).isNotNull();
        assertThat(result.getCheckResults()).isNotNull();
        assertThat(result.getUuid()).isNull();
    }

    @Test
    void testUpdateValidRenter() {
        Renter renter = getValidRenter();
        Renter createdRenter = given()
            .spec(requestSpec)
            .body(renter)
            .when()
            .post(baseRoutePath + "/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);
        String newKpp = "999999999";
        String newBic = "045004641";
        String newAccount = "40817810788460000076";
        createdRenter.setKpp(newKpp);
        createdRenter.setBankBic(newBic);
        createdRenter.setAccount(newAccount);
        Renter updated = given()
            .spec(requestSpec)
            .body(createdRenter)
            .when()
            .post(baseRoutePath + "/update")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);

        assertThat(updated).isNotNull();
        assertThat(updated.getCheckResults()).isNull();
        assertThat(updated.getKpp()).isEqualTo(newKpp);
        assertThat(updated.getBankBic()).isEqualTo(newBic);
        assertThat(updated.getAccount()).isEqualTo(newAccount);
    }

    @Test
    void testUpdateValidRenter_withoutPhoneNumbers() {
        Renter renter = getValidRenter()
            .phoneNumbers(null);
        Renter createdRenter = given()
            .spec(requestSpec)
            .body(renter)
            .when()
            .post(baseRoutePath + "/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);
        String newKpp = "999999999";
        String newBic = "045004641";
        String newAccount = "40817810788460000076";
        createdRenter.setKpp(newKpp);
        createdRenter.setBankBic(newBic);
        createdRenter.setAccount(newAccount);
        createdRenter.setPhoneNumbers("+79111111111");
        Renter updated = given()
            .spec(requestSpec)
            .body(createdRenter)
            .when()
            .post(baseRoutePath + "/update")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);

        assertThat(updated).isNotNull();
        assertThat(updated.getCheckResults()).isNull();
        assertThat(updated.getKpp()).isEqualTo(newKpp);
        assertThat(updated.getBankBic()).isEqualTo(newBic);
        assertThat(updated.getAccount()).isEqualTo(newAccount);
    }

    @Test
    void testUpdateValidRenter_withoutBankAccount() {
        Renter renter = getValidRenter();
        renter.bankAccount(null);
        Renter createdRenter = given()
            .spec(requestSpec)
            .body(renter)
            .when()
            .post(baseRoutePath + "/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);
        String newKpp = "999999999";
        createdRenter.setKpp(newKpp);
        createdRenter.setBankAccount("30101810400000000225");
        Renter updated = given()
            .spec(requestSpec)
            .body(createdRenter)
            .when()
            .post(baseRoutePath + "/update")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);

        assertThat(updated).isNotNull();
        assertThat(updated.getCheckResults()).isNull();
        assertThat(updated.getKpp()).isEqualTo(newKpp);
    }

    @Test
    void testUpdateInvalidRenter() {
        Renter renter = getValidRenter();
        Renter createdRenter = given()
            .spec(requestSpec)
            .body(renter)
            .when()
            .post(baseRoutePath + "/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);

        createdRenter.setOgrn("1");
        Renter updated = given()
            .spec(requestSpec)
            .body(createdRenter)
            .when()
            .post(baseRoutePath + "/update")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);

        assertThat(updated).isNotNull();
        assertThat(updated.getCheckResults()).isNotNull();
        assertThat(updated.getUuid()).isNull();
    }

    @Test
    void testGetRenter() {
        Renter renter = getValidRenter();
        Renter createdRenter = given()
            .spec(requestSpec)
            .body(renter)
            .when()
            .post(baseRoutePath + "/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);
        Renter testRenter = given()
            .spec(requestSpec)
            .body(new RenterIdentifier().digitalId(createdRenter.getDigitalId()).uuid(createdRenter.getUuid()))
            .when()
            .post(baseRoutePath + "/get")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);

        assertThat(testRenter)
            .isNotNull()
            .isEqualTo(createdRenter);
    }

    @Test
    void getGetRentersList() {
        RenterFilter filter = new RenterFilter().digitalId(randomAlphabetic(10));
        RenterListResponse response = given()
            .spec(requestSpec)
            .body(filter)
            .when()
            .post(baseRoutePath + "/view")
            .then()
            .spec(responseSpec)
            .extract()
            .as(RenterListResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();

        Renter renter = getValidRenter();
        renter.setDigitalId(filter.getDigitalId());
        given()
            .spec(requestSpec)
            .body(renter)
            .when()
            .post(baseRoutePath + "/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);

        response = given()
            .spec(requestSpec)
            .body(filter)
            .when()
            .post(baseRoutePath + "/view")
            .then()
            .spec(responseSpec)
            .extract()
            .as(RenterListResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);

        Renter renter2 = getValidRenter();
        renter2.setDigitalId(filter.getDigitalId());
        given()
            .spec(requestSpec)
            .body(renter2)
            .when()
            .post(baseRoutePath + "/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);
        response = given()
            .spec(requestSpec)
            .body(filter)
            .when()
            .post(baseRoutePath + "/view")
            .then()
            .spec(responseSpec)
            .extract()
            .as(RenterListResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(2);
    }
}

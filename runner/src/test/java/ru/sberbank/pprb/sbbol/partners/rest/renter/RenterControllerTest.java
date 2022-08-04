package ru.sberbank.pprb.sbbol.partners.rest.renter;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.entity.renter.DulType;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterAddress;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.renter.model.RenterIdentifier;
import ru.sberbank.pprb.sbbol.renter.model.RenterListResponse;
import ru.sberbank.pprb.sbbol.renter.model.Version;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

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
        createdRenter.setKpp(newKpp);
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
        assertThat(response.getItems().size()).isZero();

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
        assertThat(response.getItems().size()).isEqualTo(1);

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
        assertThat(response.getItems().size()).isEqualTo(2);
    }

    private Renter getValidRenter() {
        RenterAddress address = new RenterAddress().zipCode("655511")
            .regionCode("42")
            .region("Кемеровская область")
            .city("Кемерово")
            .locality("Кемерово")
            .street("Ленина")
            .building("162")
            .buildingBlock("1")
            .flat("55");

        return new Renter()
            .digitalId(randomAlphabetic(10))
            .type(Renter.TypeEnum.LEGAL_ENTITY)
            .legalName("ОАО Рога и копыта")
            .inn("132456789132")
            .kpp("0")
            .ogrn("123456789012345")
            .okpo("1234567890")
            .lastName("Фамилия")
            .firstName("Имя")
            .middleName("Отчество")
            .dulType(Renter.DulTypeEnum.PASSPORTOFRUSSIA)
            .dulName(DulType.PASSPORTOFRUSSIA.getDesc())
            .dulSerie("Серия")
            .dulNumber("Номер")
            .dulDivisionIssue("Место")
            .dulDateIssue(LocalDate.now())
            .dulDivisionCode("Код")
            .account("40702810538261023926")
            .bankBic("044525225")
            .bankName("ПАО СБЕРБАНК")
            .bankAccount("30101810400000000225")
            .phoneNumbers("+79991112233")
            .emails("roga@mail.ru")
            .legalAddress(address)
            .physicalAddress(address);
    }
}

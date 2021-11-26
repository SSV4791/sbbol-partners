package ru.sberbank.pprb.sbbol.partners.rest;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class PartnerControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partners";

    @Test
    void testGetPartner() {
        Partner partner = getValidPartner();
        PartnerResponse createdPartner = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);

        assertThat(createdPartner)
            .isNotNull();
        PartnerResponse actualPartner = given()
            .spec(requestSpec)
            .when()
            .get(baseRoutePath + "/{digitalId}" + "/{id}", createdPartner.getPartner().getDigitalId(), createdPartner.getPartner().getUuid())
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);

        assertThat(actualPartner)
            .isNotNull();

        assertThat(actualPartner.getPartner())
            .isNotNull()
            .isEqualTo(createdPartner.getPartner());
    }

    @Test
    void testGetOnePartners() {
        PartnerResponse createdPartner1 = given()
            .spec(requestSpec)
            .body(getValidPartner())
            .when()
            .post(baseRoutePath)
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        PartnerResponse createdPartner2 = given()
            .spec(requestSpec)
            .body(getValidPartner())
            .when()
            .post(baseRoutePath)
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);
        assertThat(createdPartner2)
            .isNotNull();

        PartnersFilter filter = new PartnersFilter();
        filter.setDigitalId("111111");
        filter.setPagination(
            new Pagination()
                .offset(1)
                .count(1)
        );

        PartnersResponse response = given()
            .spec(requestSpec)
            .body(filter)
            .when()
            .post(baseRoutePath + "/view")
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnersResponse.class);

        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetAllPartners() {

        PartnerResponse createdPartner1 = given()
            .spec(requestSpec)
            .body(getValidPartner())
            .when()
            .post(baseRoutePath)
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        PartnerResponse createdPartner2 = given()
            .spec(requestSpec)
            .body(getValidPartner())
            .when()
            .post(baseRoutePath)
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);
        assertThat(createdPartner2)
            .isNotNull();

        PartnersFilter filter = new PartnersFilter();
        filter.setDigitalId("111111");
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(2)
        );

        PartnersResponse response = given()
            .spec(requestSpec)
            .body(filter)
            .when()
            .post(baseRoutePath + "/view")
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnersResponse.class);

        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isEqualTo(2);
    }

    @Test
    void testCreatePartner() {
        Partner partner = getValidPartner();
        PartnerResponse response = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);

        assertThat(response)
            .isNotNull();
        assertThat(response.getPartner())
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "accounts.uuid",
                "accounts.digitalId",
                "accounts.bank.uuid",
                "accounts.bank.accounts.uuid",
                "addresses.uuid",
                "documents.uuid",
                "contacts.uuid")
            .isEqualTo(partner);
        assertThat(response.getErrors())
            .isNull();
    }

    @Test
    void testUpdatePartner() {
        Partner partner = getValidPartner();
        PartnerResponse createdPartner = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);

        assertThat(createdPartner)
            .isNotNull();

        String newKpp = "999999999";
        Partner updatePartner = new Partner();
        updatePartner.uuid(createdPartner.getPartner().getUuid());
        updatePartner.digitalId(createdPartner.getPartner().getDigitalId());
        updatePartner.type(createdPartner.getPartner().getType());
        updatePartner.kpp(newKpp);
        PartnerResponse newUpdatePartner = given()
            .spec(requestSpec)
            .body(updatePartner)
            .when()
            .put(baseRoutePath)
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);

        assertThat(updatePartner)
            .isNotNull();
        assertThat(newUpdatePartner.getPartner().getKpp())
            .isEqualTo(newKpp);
        assertThat(newUpdatePartner.getErrors())
            .isNull();
    }

    @Test
    void testDeletePartner() {
        Partner partner = getValidPartner();
        PartnerResponse createdPartner = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);

        assertThat(createdPartner)
            .isNotNull();

        PartnerResponse actualPartner = given()
            .spec(requestSpec)
            .when()
            .get(baseRoutePath + "/{digitalId}" + "/{id}", createdPartner.getPartner().getDigitalId(), createdPartner.getPartner().getUuid())
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);

        assertThat(actualPartner)
            .isNotNull();

        assertThat(actualPartner.getPartner())
            .isNotNull()
            .isEqualTo(createdPartner.getPartner());

        PartnerResponse deletePartner = given()
            .spec(requestSpec)
            .when()
            .delete(baseRoutePath + "/{digitalId}" + "/{id}", actualPartner.getPartner().getDigitalId(), actualPartner.getPartner().getUuid())
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);

        assertThat(deletePartner)
            .isNotNull();

        assertThat(deletePartner.getPartner())
            .isNotNull()
            .isEqualTo(createdPartner.getPartner());

        PartnerResponse searchPartner = given()
            .spec(requestSpec)
            .when()
            .get(baseRoutePath + "/{digitalId}" + "/{id}", createdPartner.getPartner().getDigitalId(), createdPartner.getPartner().getUuid())
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);

        assertThat(searchPartner)
            .isNotNull();

        assertThat(searchPartner.getPartner())
            .isNull();
    }

    public static Partner getValidPartner() {
        return new Partner()
            .uuid(UUID.randomUUID().toString())
            .version(0L)
            .digitalId("111111")
            .type(Partner.TypeEnum.PHYSICAL_PERSON)
            .orgName("Наименование компании")
            .firstName("Имя клиента")
            .secondName("Фамилия клиента")
            .middleName("Отчество клиента")
            .inn("111111")
            .kpp("222222")
            .ogrn("333333")
            .okpo("444444")
            .phone("+79241111111")
            .email("a.a.a@sberbank.ru")
            .comment("555555")
            .addresses(List.of(new Address()
                .uuid(UUID.randomUUID().toString())
                .version(0L)
                .type(Address.TypeEnum.LEGAL_ADDRESS)
                .zipCode("111111")
                .region("222222")
                .city("333333")
                .location("444444")
                .street("555555")
                .building("666666")
                .buildingBlock("777777")
                .flat("888888")
            ))
            .accounts(List.of(new Account()
                .uuid(UUID.randomUUID().toString())
                .version(0L)
                .name("111111")
                .account("222222")
                .bank(new Bank()
                    .uuid(UUID.randomUUID().toString())
                    .version(0L)
                    .bic("1111111")
                    .name("222222")
                    .addAccountsItem(new BankAccount()
                        .uuid(UUID.randomUUID().toString())
                        .account("111111111111111"))
                )
                .state(Account.StateEnum.NOT_SIGNED)))
            .documents(List.of(new Document()
                .uuid(UUID.randomUUID().toString())
                .version(0L)
                .type(Document.TypeEnum.PASSPORT_OF_RUSSIA)
                .series("111111")
                .number("222222")
                .dateIssue(LocalDate.now())
                .divisionIssue("333333")
                .divisionCode("444444")))
            .contacts(List.of(new Contact()
                .version(0L)
                .uuid(UUID.randomUUID().toString())
                .name("111111")
                .position("222222")
                .phone("333333")
                .email("444444")));
    }
}

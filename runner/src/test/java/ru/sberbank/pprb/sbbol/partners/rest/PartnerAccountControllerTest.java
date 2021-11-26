package ru.sberbank.pprb.sbbol.partners.rest;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccount;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.PartnerControllerTest.getValidPartner;

class PartnerAccountControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetAccount() {
        Partner partner = createValidPartner();
        PartnerAccount account = getValidAccount(partner.getUuid());

        PartnerAccountResponse createAccount = given()
            .spec(requestSpec)
            .body(account)
            .when()
            .post(baseRoutePath + "/account/")
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerAccountResponse.class);

        assertThat(createAccount)
            .isNotNull();
        PartnerAccountResponse actualAccount = given()
            .spec(requestSpec)
            .when()
            .get(baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}", createAccount.getAccount().getDigitalId(), createAccount.getAccount().getUuid())
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerAccountResponse.class);

        assertThat(actualAccount)
            .isNotNull();

        assertThat(actualAccount.getAccount())
            .isNotNull()
            .isEqualTo(createAccount.getAccount());
    }

    @Test
    void testCreateAccount() {
        Partner partner = createValidPartner();
        PartnerAccount account = getValidAccount(partner.getUuid());

        PartnerAccountResponse createAccount = given()
            .spec(requestSpec)
            .body(account)
            .when()
            .post(baseRoutePath + "/account/")
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerAccountResponse.class);

        assertThat(createAccount)
            .isNotNull();

        assertThat(createAccount.getAccount())
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "bank.uuid",
                "bank.accounts.uuid")
            .isEqualTo(account);
        assertThat(createAccount.getErrors())
            .isNull();
    }

    @Test
    void testUpdateAccount() {
        Partner partner = createValidPartner();
        PartnerAccount account = getValidAccount(partner.getUuid());

        PartnerAccountResponse createAccount = given()
            .spec(requestSpec)
            .body(account)
            .when()
            .post(baseRoutePath + "/account/")
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerAccountResponse.class);

        assertThat(createAccount)
            .isNotNull();

        String newName = "Новое наименование";
        PartnerAccount updateAccount = new PartnerAccount();
        updateAccount.uuid(createAccount.getAccount().getUuid());
        updateAccount.digitalId(createAccount.getAccount().getDigitalId());
        updateAccount.partnerUuid(createAccount.getAccount().getPartnerUuid());
        updateAccount.name(newName);
        PartnerAccountResponse newUpdateAccount = given()
            .spec(requestSpec)
            .body(updateAccount)
            .when()
            .put(baseRoutePath + "/account/")
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerAccountResponse.class);

        assertThat(newUpdateAccount)
            .isNotNull();
        assertThat(newUpdateAccount.getAccount().getName())
            .isEqualTo(newName);
        assertThat(newUpdateAccount.getErrors())
            .isNull();
    }

    @Test
    void testDeleteAccount() {
        Partner partner = createValidPartner();
        PartnerAccount account = getValidAccount(partner.getUuid());

        PartnerAccountResponse createdAccount = given()
            .spec(requestSpec)
            .body(account)
            .when()
            .post(baseRoutePath + "/account/")
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerAccountResponse.class);

        assertThat(createdAccount)
            .isNotNull();

        PartnerAccountResponse actualAccount = given()
            .spec(requestSpec)
            .when()
            .get(baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}", createdAccount.getAccount().getDigitalId(), createdAccount.getAccount().getUuid())
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerAccountResponse.class);

        assertThat(actualAccount)
            .isNotNull();

        assertThat(actualAccount.getAccount())
            .isNotNull()
            .isEqualTo(createdAccount.getAccount());

        PartnerAccountResponse deleteAccount = given()
            .spec(requestSpec)
            .when()
            .delete(baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}", actualAccount.getAccount().getDigitalId(), actualAccount.getAccount().getUuid())
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerAccountResponse.class);

        assertThat(deleteAccount)
            .isNotNull();

        assertThat(deleteAccount.getAccount())
            .isNotNull()
            .isEqualTo(createdAccount.getAccount());

        PartnerAccountResponse searchAccount = given()
            .spec(requestSpec)
            .when()
            .get(baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}", createdAccount.getAccount().getDigitalId(), createdAccount.getAccount().getUuid())
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerAccountResponse.class);

        assertThat(searchAccount)
            .isNotNull();

        assertThat(searchAccount.getAccount())
            .isNull();
    }

    private PartnerAccount getValidAccount(String partnerUuid) {
        return new PartnerAccount()
            .uuid(UUID.randomUUID().toString())
            .version(0L)
            .partnerUuid(partnerUuid)
            .digitalId("11111")
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
            .state(PartnerAccount.StateEnum.NOT_SIGNED);
    }

    private Partner createValidPartner() {
        Partner partner = getValidPartner();

        PartnerResponse createPartner = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post("/partners")
            .then()
            .spec(responseSpec)
            .extract()
            .as(PartnerResponse.class);
        assertThat(createPartner)
            .isNotNull();
        return createPartner.getPartner();
    }
}

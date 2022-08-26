package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressType;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.CertifierType;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFilterType;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.SearchPartners;
import ru.sberbank.pprb.sbbol.partners.model.SignType;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.partners.handler.ErrorCode.MODEL_DUPLICATE_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.partners.handler.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.partners.handler.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.partners.handler.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.ACCOUNT_FOR_TEST_PARTNER;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidBudgetAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountSignControllerTest.createValidAccountsSign;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class PartnerControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetPartner() {
        var partner = getValidPartner();
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();

        var actualPartner = get(
            "/partners/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(actualPartner)
            .isNotNull();
        assertThat(actualPartner)
            .isNotNull()
            .isEqualTo(createdPartner);
    }

    @Test
    void testGetOnePartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .offset(1)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testCreateContact2() {
        var partner = getValidPartner(randomAlphabetic(10));
        partner.setEmails(null);
        partner.setPhones(null);
        var createPartner = createValidPartner(partner);
        assertThat(createPartner)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version",
                "phones",
                "emails"
            )
            .isEqualTo(createPartner);
    }


    @Test
    void testGetSearchInnPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(createdPartner1.getInn().substring(4))
        );
        filter.setPagination(
            new Pagination()
                .offset(1)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchBudgetPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        createValidBudgetAccount(createdPartner1.getId(), createdPartner1.getDigitalId());

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersFilter(PartnerFilterType.BUDGET);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchOrgNamePartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(createdPartner1.getOrgName().substring(4))
        );
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchFIOPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(createdPartner1.getSecondName() + " " + createdPartner1.getFirstName())
        );
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchByOrgName_whenDifferentWordCase() {
        var digitalId = randomAlphabetic(10);
        var partnerCreate = getValidPartner(digitalId);
        partnerCreate.setOrgName(partnerCreate.getOrgName().toLowerCase());
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        partnerCreate.setOrgName(partnerCreate.getOrgName().toUpperCase());
        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(partnerCreate.getOrgName())
        );
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(2)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners())
            .hasSize(2);
    }

    @Test
    void testGetSearchLegalPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,

            getValidPhysicalPersonPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersFilter(PartnerFilterType.LEGAL_ENTITY);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchPhysicalPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPhysicalPersonPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersFilter(PartnerFilterType.PHYSICAL_PERSON);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetSearchEntrepreneurPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidEntrepreneurPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersFilter(PartnerFilterType.ENTREPRENEUR);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetPartnersNotSignedAccount() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        createValidAccount(createdPartner1.getId(), createdPartner1.getDigitalId());

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidEntrepreneurPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.accountSignType(SignType.NOT_SIGNED);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetPartnersSignedAccount() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        var validAccount = createValidAccount(createdPartner1.getId(), createdPartner1.getDigitalId());
        createValidAccountsSign(createdPartner1.getDigitalId(), validAccount.getId());

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidEntrepreneurPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.accountSignType(SignType.SIGNED);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testNegativeUpdateChildPartner() {
        var partner = createValidPartner(randomAlphabetic(10));
        HashSet<Phone> newPhones = new HashSet<>();
        if (partner.getPhones() != null) {
            for (var phone : partner.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion());
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(13));
                newPhones.add(newPhone);
            }
        }
        HashSet<Email> newEmails = new HashSet<>();
        if (partner.getEmails() != null) {
            for (var email : partner.getEmails()) {
                var newEmail = new Email();
                newEmail.setVersion(email.getVersion());
                newEmail.setId(email.getId());
                newEmail.setUnifiedId(email.getUnifiedId());
                newEmail.setDigitalId(email.getDigitalId());
                newEmail.setEmail(email.getEmail());
                newEmails.add(newEmail);
            }
        }
        partner.setPhones(newPhones);
        partner.setEmails(newEmails);
        var newUpdatePartner = put(
            baseRoutePath,
            HttpStatus.OK,
            updatePartner(partner),
            Partner.class
        );
        assertThat(newUpdatePartner)
            .isNotNull();
        assertThat(newUpdatePartner.getFirstName())
            .isEqualTo(newUpdatePartner.getFirstName());
        assertThat(newUpdatePartner.getFirstName())
            .isNotEqualTo(partner.getFirstName());

        HashSet<Phone> newPhones1 = new HashSet<>();
        if (partner.getPhones() != null) {
            for (var phone : partner.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion() + 1);
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(13));
                newPhones1.add(newPhone);
            }
        }
        HashSet<Email> newEmails1 = new HashSet<>();
        if (partner.getEmails() != null) {
            for (var email : partner.getEmails()) {
                var newEmail = new Email();
                newEmail.setVersion(email.getVersion() + 10);
                newEmail.setId(email.getId());
                newEmail.setUnifiedId(email.getUnifiedId());
                newEmail.setDigitalId(email.getDigitalId());
                newEmail.setEmail(email.getEmail());
                newEmails1.add(newEmail);
            }
        }
        partner.setPhones(newPhones1);
        partner.setEmails(newEmails1);
        partner.setVersion(newUpdatePartner.getVersion() + 1);
        var newUpdatePartner1 = put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            updatePartner(partner),
            Error.class
        );
        assertThat(newUpdatePartner1)
            .isNotNull();
        assertThat(newUpdatePartner1.getCode())
            .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
    }

    @Test
    void testUpdateChildPartner() {
        var partner = createValidPartner(randomAlphabetic(10));
        HashSet<Phone> newPhones = new HashSet<>();
        if (partner.getPhones() != null) {
            for (var phone : partner.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion());
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(13));
                newPhones.add(newPhone);
            }
        }
        HashSet<Email> newEmails = new HashSet<>();
        if (partner.getEmails() != null) {
            for (var email : partner.getEmails()) {
                var newEmail = new Email();
                newEmail.setVersion(email.getVersion());
                newEmail.setId(email.getId());
                newEmail.setUnifiedId(email.getUnifiedId());
                newEmail.setDigitalId(email.getDigitalId());
                newEmail.setEmail(email.getEmail());
                newEmails.add(newEmail);
            }
        }
        partner.setPhones(newPhones);
        partner.setEmails(newEmails);
        var newUpdatePartner = put(
            baseRoutePath,
            HttpStatus.OK,
            updatePartner(partner),
            Partner.class
        );
        assertThat(newUpdatePartner)
            .isNotNull();
        assertThat(newUpdatePartner.getFirstName())
            .isEqualTo(newUpdatePartner.getFirstName());
        assertThat(newUpdatePartner.getFirstName())
            .isNotEqualTo(partner.getFirstName());

        Set<Phone> newPhones1 = new HashSet<>();
        if (partner.getPhones() != null) {
            for (var phone : partner.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion() + 1);
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(13));
                newPhones1.add(newPhone);
            }
        }
        Set<Email> newEmails1 = new HashSet<>();
        if (partner.getEmails() != null) {
            for (var email : partner.getEmails()) {
                var newEmail1 = new Email();
                newEmail1.setVersion(email.getVersion() + 1);
                newEmail1.setId(email.getId());
                newEmail1.setUnifiedId(email.getUnifiedId());
                newEmail1.setDigitalId(email.getDigitalId());
                newEmail1.setEmail(randomAlphabetic(64) + "@mail.ru");
                newEmails1.add(newEmail1);
            }
        }
        partner.setPhones(newPhones1);
        partner.setEmails(newEmails1);
        partner.setVersion(partner.getVersion() + 1);
        var newUpdatePartner1 = put(
            baseRoutePath,
            HttpStatus.OK,
            updatePartner(partner),
            Partner.class
        );
        assertThat(newUpdatePartner1)
            .isNotNull();
        assertThat(newUpdatePartner1.getFirstName())
            .isEqualTo(newUpdatePartner1.getFirstName());
        assertThat(newUpdatePartner1.getFirstName())
            .isNotEqualTo(partner.getFirstName());
    }


    @Test
    void testNegativeGetAllPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var createdPartner3 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner3)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);

        var response = post(
            "/partners/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var filter1 = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .count(2));
        var response1 = post(
            "/partners/view",
            HttpStatus.BAD_REQUEST,
            filter1,
            Error.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var filter2 = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .offset(0));
        var response2 = post(
            "/partners/view",
            HttpStatus.BAD_REQUEST,
            filter2,
            Error.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
    }

    @Test
    void testGetAllPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var createdPartner3 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner3)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(2)
        );

        var response = post(
            "/partners/view",
            HttpStatus.OK,
            filter,
            PartnersResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isEqualTo(2);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }

    @Test
    void testCreatePartner() {
        var partner = createValidPartner();
        assertThat(partner)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "emails.unifiedUuid",
                "emails.uuid",
                "phones.unifiedUuid",
                "phones.uuid"
            )
            .isEqualTo(partner);
    }

    @Test
    void testCreatePartner_unique() {
        var partner = getValidPartner();
        createValidPartner(partner);
        var error = post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_DUPLICATE_EXCEPTION.getValue());
    }

    @Test
    void testCreateNotValidPartner() {
        var error = createNotValidPartner();
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
    }

    @Test
    void testCreatePartnerEmptyOrgName() {
        var partner = getValidPartner();
        partner.setOrgName("");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        AssertionsForClassTypes.assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Введите наименование");
    }

    @Test
    void testCreatePartnerInvalidCharsInOrgName() {
        var partner = getValidPartner();
        partner.setOrgName("[Наименование] [§±]");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        AssertionsForClassTypes.assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Наименование содержит недопустимые символы: [][§±]");
    }

    @Test
    void testUpdatePartner() {
        var partner = getValidPartner();
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        String newKpp = "999999999";
        createdPartner.kpp(newKpp);
        var newUpdatePartner = put(
            baseRoutePath,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );

        assertThat(createdPartner)
            .isNotNull();
        assertThat(newUpdatePartner.getKpp())
            .isEqualTo(newKpp);
    }

    @Test
    void testUpdatePartner2() {
        var partner = getValidPartner();
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        String newKpp = "999999999";
        createdPartner.kpp(newKpp);
        createdPartner.setPhones(null);
        createdPartner.setEmails(null);
        Partner newUpdatePartner = put(
            baseRoutePath,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );

        assertThat(createdPartner)
            .isNotNull();
        assertThat(newUpdatePartner.getKpp())
            .isEqualTo(newKpp);
    }

    @Test
    void testUpdatePartner_unique() {
        var partner = getValidPartner();
        String newKpp = "999999999";
        var createdPartner1 = createValidPartner(partner);
        partner.setKpp(newKpp);
        createValidPartner(partner);
        var errorUpdatePartner = createdPartner1.kpp(newKpp);
        var error = put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            errorUpdatePartner,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_DUPLICATE_EXCEPTION.getValue());
    }

    @Test
    void negativeTestUpdatePartnerVersion() {
        var partner = createValidPartner();
        Long version = partner.getVersion() + 1;
        partner.setVersion(version);
        var partnerError = put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            updatePartner(partner),
            Error.class
        );
        assertThat(partnerError.getCode())
            .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
        assertThat(partnerError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains("Версия записи в базе данных " + (partner.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    void positiveTestUpdatePartnerVersion() {
        var partner = createValidPartner();
        var updatePartner = put(
            baseRoutePath,
            HttpStatus.OK,
            updatePartner(partner),
            Partner.class
        );
        var checkPartner = get(
            "/partners/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Partner.class,
            updatePartner.getDigitalId(),
            updatePartner.getId()
        );
        assertThat(checkPartner)
            .isNotNull();
        assertThat(checkPartner.getVersion())
            .isEqualTo(partner.getVersion() + 1);
    }

    @Test
    void testDeletePartner() {
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(),
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        var actualPartner = get(
            "/partners/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(actualPartner)
            .isNotNull()
            .isEqualTo(createdPartner);

        delete(
            "/partners/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("ids", actualPartner.getId()),
            actualPartner.getDigitalId()
        ).getBody();

        var searchPartner = get(
            "/partners/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            Error.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(searchPartner)
            .isNotNull();

        assertThat(searchPartner.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
    }

    @Test
    void savePartnerFullModel() {
        var request = getValidFullModelPartner();
        var createdPartner = post(
            baseRoutePath + "/full-model",
            HttpStatus.CREATED,
            request,
            PartnerCreateFullModelResponse.class
        );
        assertThat(createdPartner)
            .isNotNull();
    }

    @Test
    void savePartnerFullModelEmptyOrgName() {
        var partner = getValidFullModelPartner();
        partner.setOrgName("");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        AssertionsForClassTypes.assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Введите наименование");
    }

    @Test
    void savePartnerFullModelInvalidOrgName() {
        var partner = getValidFullModelPartner();
        partner.setOrgName("[Наименование] §±`~><");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        AssertionsForClassTypes.assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Наименование содержит недопустимые символы: []§±");
    }

    @Test
    void savePhysicalPartnerInvalidName() {
        var partner = getValidPhysicalPersonPartner();
        partner.setFirstName("[Имя] §±`~><");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        AssertionsForClassTypes.assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Имя содержит недопустимые символы: []§±");
    }

    public static PartnerCreate getValidPartner() {
        return getValidPartner(randomAlphabetic(10));
    }

    public static PartnerCreate getValidPartner(String digitalId) {
        var partner = new PartnerCreate()
            .legalForm(LegalForm.LEGAL_ENTITY)
            .orgName(randomAlphabetic(10))
            .firstName(randomAlphabetic(10))
            .secondName(randomAlphabetic(10))
            .middleName(randomAlphabetic(10))
            .inn("4139314257")
            .kpp("123456789")
            .ogrn("1035006110083")
            .okpo("12345678")
            .phones(
                Set.of(
                    "0079241111111"
                ))
            .emails(
                Set.of(
                    "a.a.a@sberbank.ru"
                ))
            .comment("555555");
        partner.setDigitalId(digitalId);
        return partner;
    }

    public static PartnerCreateFullModel getValidFullModelPartner() {
        return getValidFullModelPartner(randomAlphabetic(10));
    }

    public static PartnerCreateFullModel getValidFullModelPartner(String digitalId) {
        return new PartnerCreateFullModel()
            .digitalId(digitalId)
            .legalForm(LegalForm.LEGAL_ENTITY)
            .orgName(randomAlphabetic(10))
            .firstName(randomAlphabetic(10))
            .secondName(randomAlphabetic(10))
            .middleName(randomAlphabetic(10))
            .inn("4139314257")
            .kpp("123456789")
            .ogrn("1035006110083")
            .okpo("12345678")
            .phones(
                Set.of(
                    "0079241111111"
                ))
            .emails(
                Set.of(
                    "a.a.a@sberbank.ru"
                ))
            .comment("555555")
            .accounts(Set.of(new AccountCreateFullModel()
                .account(ACCOUNT_FOR_TEST_PARTNER)
                .comment("Это тестовый комментарий")
                .bank(new BankCreate()
                    .bic("044525411")
                    .name(randomAlphabetic(10))
                    .bankAccount(
                        new BankAccountCreate()
                            .bankAccount("30101810145250000411"))
                ))
            )
            .documents(Set.of(new DocumentCreateFullModel()
                    .certifierName(RandomStringUtils.randomAlphabetic(100))
                    .certifierType(CertifierType.NOTARY)
                    .dateIssue(LocalDate.now())
                    .divisionCode(RandomStringUtils.randomAlphanumeric(50))
                    .divisionIssue(RandomStringUtils.randomAlphanumeric(250))
                    .number(RandomStringUtils.randomAlphanumeric(50))
                    .series(RandomStringUtils.randomAlphanumeric(50))
                    .positionCertifier(RandomStringUtils.randomAlphanumeric(100))
                    .documentTypeId("8a4d4464-64a1-4f3d-ab86-fd3be614f7a2")
                )
            )
            .address(Set.of(new AddressCreateFullModel()
                    .building("1")
                    .buildingBlock("2")
                    .city("3")
                    .flat("4")
                    .location("5")
                    .region("6")
                    .regionCode("7")
                    .street("8")
                    .type(AddressType.LEGAL_ADDRESS)
                    .zipCode("9")
                )
            )
            .contacts(Set.of(new ContactCreateFullModel()
                    .legalForm(LegalForm.LEGAL_ENTITY)
                    .orgName("Наименование компании")
                    .firstName("Имя клиента")
                    .secondName("Фамилия клиента")
                    .middleName("Отчество клиента")
                    .position("Должность")
                    .phones(
                        Set.of(
                            "0079241111111"
                        ))
                    .emails(
                        Set.of(
                            "a.a.a@sberbank.ru"
                        ))
                )
            );
    }

    public static PartnerCreate getValidPhysicalPersonPartner() {
        return getValidPhysicalPersonPartner(randomAlphabetic(10));
    }

    private static PartnerCreate getValidPhysicalPersonPartner(String digitalId) {
        var partner = getValidPartner(digitalId);
        partner.setLegalForm(LegalForm.PHYSICAL_PERSON);
        partner.setInn("521031961500");
        return partner;
    }

    private static PartnerCreate getValidEntrepreneurPartner(String digitalId) {
        var partner = getValidPartner(digitalId);
        partner.setLegalForm(LegalForm.ENTREPRENEUR);
        partner.setInn("521031961500");
        return partner;
    }

    protected static Partner createValidPartner() {
        return post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(),
            Partner.class
        );
    }

    protected static Partner createValidPartner(PartnerCreate partner) {
        return post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
    }

    protected static Partner createValidPartner(String digitalId) {
        return post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            Partner.class);
    }

    private static Error createNotValidPartner() {
        var partner = getValidPartner();
        partner.setInn("222222");
        partner.setKpp("JDujsfdwjinfiwef3224234234");
        partner.setOkpo("1234123123123213123123123123");
        partner.setEmails(Set.of
            ("@@@@@@@@@@@@"));
        return post(baseRoutePath, HttpStatus.BAD_REQUEST, partner, Error.class);
    }

    public static Partner updatePartner(Partner partner) {
        return new Partner()
            .id(partner.getId())
            .digitalId(partner.getDigitalId())
            .legalForm(partner.getLegalForm())
            .orgName(partner.getOrgName())
            .secondName(partner.getSecondName())
            .middleName(partner.getMiddleName())
            .phones(partner.getPhones())
            .emails(partner.getEmails())
            .firstName(randomAlphabetic(10))
            .version(partner.getVersion());
    }
}

package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
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
import static ru.sberbank.pprb.sbbol.partners.rest.partner.BaseAccountControllerTest.getValidInnNumber;
import ru.sberbank.pprb.sbbol.renter.model.Renter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_DUPLICATE_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidBudgetAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountSignControllerTest.createValidAccountsSign;
import static ru.sberbank.pprb.sbbol.partners.rest.renter.RenterUtils.getValidRenter;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.BaseAccountControllerTest.getValidAccountNumber;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class PartnerControllerTest extends AbstractIntegrationTest {

    private static final String VALID_PHYSICAL_OKPO = "1234567890";

    public static final String baseRoutePath = "/partner";
    public static final String baseRoutePathForGet = "/partners/{digitalId}/{id}";

    @Test
    void testCreatePartnerWithoutDigitalId() {
        var partner = getValidLegalEntityPartner("");
        var error = post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        List<Descriptions> descriptions = error.getDescriptions();
        assertThat(descriptions).isNotNull();
        assertThat(descriptions).size().isEqualTo(1);
        Descriptions description = descriptions.stream()
            .filter(value -> value.getField().equals("digitalId"))
            .findFirst().orElse(null);
        assertThat(description).isNotNull();
        assertThat(description.getMessage()).contains("Поле обязательно для заполнения");
        assertThat(description.getMessage()).contains("размер должен находиться в диапазоне от 1 до 40");
        assertThat(description.getMessage()).size().isEqualTo(2);
    }

    @Test
    void testCreateEmptyLegalEntityPartner() {
        var partner = getValidLegalEntityPartner(randomAlphabetic(10))
            .firstName(null)
            .secondName(null)
            .middleName(null)
            .kpp(null)
            .ogrn(null)
            .okpo(null)
            .phones(null)
            .emails(null)
            .comment(null);
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        var actualPartner = get(
            baseRoutePathForGet,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(actualPartner)
            .isNotNull()
            .isEqualTo(createdPartner);
    }

    @Test
    void testCreateEmptyEntrepreneurPartner() {
        var partner = getValidLegalEntityPartner(randomAlphabetic(10))
            .legalForm(LegalForm.ENTREPRENEUR)
            .firstName(null)
            .secondName(null)
            .middleName(null)
            .inn(getValidInnNumber(LegalForm.ENTREPRENEUR))
            .kpp(null)
            .ogrn(null)
            .okpo(null)
            .phones(null)
            .emails(null)
            .comment(null);
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        var actualPartner = get(
            baseRoutePathForGet,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(actualPartner)
            .isNotNull()
            .isEqualTo(createdPartner);
    }

    @Test
    void testCreateEmptyPhysicalPersonPartner() {
        var partner = getValidLegalEntityPartner(randomAlphabetic(10))
            .legalForm(LegalForm.PHYSICAL_PERSON)
            .orgName(null)
            .secondName(null)
            .middleName(null)
            .inn(null)
            .kpp(null)
            .ogrn(null)
            .okpo(null)
            .phones(null)
            .emails(null)
            .comment(null);
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        var actualPartner = get(
            baseRoutePathForGet,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(actualPartner)
            .isNotNull()
            .isEqualTo(createdPartner);
    }

    @Test
    void testGetPartner() {
        var partner = getValidLegalEntityPartner();
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();

        var actualPartner = get(
            baseRoutePathForGet,
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
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
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
        var partner = getValidLegalEntityPartner(randomAlphabetic(10));
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
        var innForPartner = getValidInnNumber(LegalForm.LEGAL_ENTITY);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId)
                .inn(innForPartner),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId)
                .inn(innForPartner),
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
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        createValidBudgetAccount(createdPartner1.getId(), createdPartner1.getDigitalId());

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
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
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
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
    void testGetSearchOrgNamePartnersWithQuotesAndSpaceSymbol() {
        var digitalId = randomAlphabetic(10);
        var orgName = "ОАО \"Ромашка\"";
        var createPartner = getValidLegalEntityPartner(digitalId);
        createPartner.setOrgName(orgName);
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            createPartner,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(orgName)
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
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
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
        var partnerCreate = getValidLegalEntityPartner(digitalId);
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
            getValidLegalEntityPartner(digitalId),
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
            getValidLegalEntityPartner(digitalId),
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
            getValidLegalEntityPartner(digitalId),
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
            getValidLegalEntityPartner(digitalId),
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
            getValidLegalEntityPartner(digitalId),
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
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var createdPartner3 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
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
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var createdPartner3 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
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
    void testGetPartnersWithoutRenters() {
        var digitalId = randomAlphabetic(10);
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(digitalId),
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();

        var createdRenter = createValidRenter(digitalId);
        assertThat(createdRenter)
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
            .isEqualTo(1);
        assertThat(response.getPartners().get(0).getDigitalId())
            .isEqualTo(digitalId);
        assertThat(response.getPartners().get(0).getId())
            .isEqualTo(createdPartner.getId());
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
        var partner = getValidLegalEntityPartner();
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
        var partner = getValidLegalEntityPartner();
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

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле обязательно для заполнения");
    }

    @Test
    void testCreatePartnerInvalidCharsInOrgName() {
        var partner = getValidLegalEntityPartner();
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

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): [][§±]");
    }

    @Test
    void testUpdatePartner() {
        var partner = getValidLegalEntityPartner();
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
        var partner = getValidLegalEntityPartner();
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
        var partner = getValidLegalEntityPartner();
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
            baseRoutePathForGet,
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
            getValidLegalEntityPartner(),
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        var actualPartner = get(
            baseRoutePathForGet,
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
            baseRoutePathForGet,
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
    void testCreatePartnerWithContacts() {
        var partnerCreate = getValidLegalEntityPartner();
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(createdPartner.getEmails())
            .isNotNull();
        var expectedPhones = partnerCreate.getPhones();
        var expectedEmails = partnerCreate.getEmails();
        var actualPhones = createdPartner.getPhones()
            .stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        var actualEmails = createdPartner.getEmails()
            .stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        assertThat(actualPhones)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedPhones);
        assertThat(actualEmails)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedEmails);
    }

    @Test
    void testUpdatePartnerContacts_updateExistContacts() {
        var partnerCreate = getValidLegalEntityPartner();
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones().size() > 0)
            .isTrue();
        assertThat(createdPartner.getEmails().size() > 0)
            .isTrue();
        var updatePhone = createdPartner.getPhones().stream().findFirst().orElse(null);
        assertThat(updatePhone)
            .isNotNull();
        updatePhone.setPhone("0070987654321");
        var updateEmail = createdPartner.getEmails().stream().findFirst().orElse(null);
        assertThat(updateEmail)
            .isNotNull();
        updateEmail.setEmail("12345@mail.ru");
        put(
            baseRoutePath,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );
        var updatedPartner = get(
            baseRoutePathForGet,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(updatedPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(updatedPartner.getPhones())
            .isNotNull();
        var updatePhones = createdPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        var updatedPhones = updatedPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        assertThat(updatedPhones)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updatePhones);
        var updateEmails = createdPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        var updatedEmails = updatedPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        assertThat(updatedEmails)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updateEmails);
    }

    @Test
    void testUpdatePartnerContacts_addNewContacts() {
        var partnerCreate = getValidLegalEntityPartner();
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(createdPartner.getEmails())
            .isNotNull();
        createdPartner.getPhones().add(new Phone().phone("0071234567890"));
        createdPartner.getEmails().add(new Email().email("007@mail.ru"));
        put(
            baseRoutePath,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );
        var updatedPartner = get(
            baseRoutePathForGet,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(updatedPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(updatedPartner.getPhones())
            .isNotNull();
        var updatePhones = createdPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        var updatedPhones = updatedPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        assertThat(updatedPhones)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updatePhones);
        var updateEmails = createdPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        var updatedEmails = updatedPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        assertThat(updatedEmails)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updateEmails);
    }

    @Test
    void testUpdatePartnerContacts_deleteAnyContacts() {
        var partnerCreate = getValidLegalEntityPartner();
        partnerCreate.getPhones().add("0071234567890");
        partnerCreate.getEmails().add("123@mail.ru");
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(createdPartner.getEmails())
            .isNotNull();
        var phones = createdPartner.getPhones().stream().skip(1).collect(Collectors.toSet());
        var emails = createdPartner.getEmails().stream().skip(1).collect(Collectors.toSet());
        createdPartner.setPhones(phones);
        createdPartner.setEmails(emails);
        put(
            baseRoutePath,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );
        var updatedPartner = get(
            baseRoutePathForGet,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(updatedPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(updatedPartner.getPhones())
            .isNotNull();
        var updatePhones = createdPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        var updatedPhones = updatedPartner.getPhones().stream()
            .map(Phone::getPhone)
            .collect(Collectors.toList());
        assertThat(updatedPhones)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updatePhones);
        var updateEmails = createdPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        var updatedEmails = updatedPartner.getEmails().stream()
            .map(Email::getEmail)
            .collect(Collectors.toList());
        assertThat(updatedEmails)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(updateEmails);
    }

    @Test
    void testUpdatePartnerContacts_deleteAllContacts() {
        var partnerCreate = getValidLegalEntityPartner();
        partnerCreate.getPhones().add("0071234567890");
        partnerCreate.getEmails().add("123@mail.ru");
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partnerCreate,
            Partner.class
        );
        assertThat(createdPartner)
            .isNotNull();
        assertThat(createdPartner.getPhones())
            .isNotNull();
        assertThat(createdPartner.getEmails())
            .isNotNull();
        createdPartner.setPhones(Collections.emptySet());
        createdPartner.setEmails(Collections.emptySet());
        put(
            baseRoutePath,
            HttpStatus.OK,
            createdPartner,
            Partner.class
        );
        var updatedPartner = get(
            baseRoutePathForGet,
            HttpStatus.OK,
            Partner.class,
            createdPartner.getDigitalId(),
            createdPartner.getId()
        );
        assertThat(updatedPartner)
            .isNotNull();
        assertThat(updatedPartner.getPhones())
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(Collections.emptySet());

        assertThat(updatedPartner.getEmails())
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(Collections.emptySet());
    }

    @Test
    void savePartnerFullModel() {
        var request = getValidFullModelLegalEntityPartner();
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
        var partner = getValidFullModelLegalEntityPartner();
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

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле обязательно для заполнения");
    }

    @Test
    void savePartnerFullModelInvalidOrgName() {
        var partner = getValidFullModelLegalEntityPartner();
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

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): []§±");
    }

    @Test
    void savePartnerFullModelInvalidBankName() {
        var partner = getValidFullModelLegalEntityPartner();
        partner.getAccounts().forEach(x -> x.getBank().setName(""));
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotNull.message"));

        var partner1 = getValidFullModelLegalEntityPartner();
        partner1.getAccounts().forEach(x -> x.getBank().setName("Наименование банка [§±]"));
        var error1 = given()
            .spec(requestSpec)
            .body(partner1)
            .when()
            .post(baseRoutePath + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error1.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.illegal_symbols") + " [§±]");

        var str = "0123456789";
        var partner2 = getValidFullModelLegalEntityPartner();
        partner2.getAccounts().forEach(x -> x.getBank().setName(str.repeat(17)));
        var error2 = given()
            .spec(requestSpec)
            .body(partner2)
            .when()
            .post(baseRoutePath + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error2.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Максимальное количество символов – 160");
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

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): []§±");
    }

    @Test
    void savePhysicalPartnerInvalidComment() {
        var partner = getValidPhysicalPersonPartner();
        partner.setComment("[Коммент Ёё §±]");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): [§±]");

        var str = "0123456789";
        partner.setComment(str.repeat(26));
        var error1 = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error1.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Максимальное количество символов – 255");
    }

    @Test
    void saveFullModelPartnerInvalidAccountComment() {
        var partner = getValidFullModelLegalEntityPartner();
        partner.getAccounts().forEach(x -> x.setComment("[Comment §± Ёё]"));
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): [§±]");

        var str = "0123456789";
        partner.getAccounts().forEach(x -> x.setComment(str.repeat(6)));
        var error1 = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath + "/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        assertThat(error1.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Максимальное количество символов – 50");
    }

    @Test
    void testSavePartnerWithInvalidKpp() {
        var partner = getValidLegalEntityPartner();
        partner.setKpp("1234567890");
        var error = post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Максимальное количество символов – 9");

        partner.setKpp("[АБВ1234]");
        var error1 = post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error1.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): [АБВ]")
            .contains("Введён неверный КПП");

        partner.setKpp("003456789");
        var error2 = post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            partner,
            Error.class
        );
        assertThat(error2.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Введён неверный КПП");
    }

    @Test
    void testSavePartnerWithNullOrEmptyKpp() {
        var partner = getValidLegalEntityPartner();
        partner.setKpp(null);
        var response = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getKpp())
            .isNull();

        partner.setKpp("0");
        var response1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            Partner.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getKpp())
            .isEqualTo("0");
    }

    @Test
    void testSavePartner_whenInvalidOgrnLength() {
        var partner = getValidLegalEntityPartner();
        partner.setOgrn("11");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.legal_entity.ogrn.length"))
            .contains(MessagesTranslator.toLocale("validation.partner.ogrn.control_number"));
    }

    @Test
    void testSavePartner_whenLegalFormIsNull() {
        var partner = getValidLegalEntityPartner();
        partner.setLegalForm(null);
        partner.setInn(getValidInnNumber(LegalForm.PHYSICAL_PERSON));
        partner.setOkpo(VALID_PHYSICAL_OKPO);
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        var expectedDescription = new Descriptions().field("legalForm").message(List.of("Поле обязательно для заполнения"));
        var expectedDescriptions = List.of(expectedDescription);
        assertThat(error.getDescriptions())
            .usingRecursiveComparison()
            .isEqualTo(expectedDescriptions);
    }

    @Test
    void testSaveFullModelPartner_whenLegalFormIsNull() {
        var partner = getValidFullModelLegalEntityPartner();
        partner.setLegalForm(null);
        partner.setInn(getValidInnNumber(LegalForm.PHYSICAL_PERSON));
        partner.setOkpo(VALID_PHYSICAL_OKPO);
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath+"/full-model")
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);

        var expectedDescription = new Descriptions().field("legalForm").message(List.of("Поле обязательно для заполнения"));
        var expectedDescriptions = List.of(expectedDescription);
        assertThat(error.getDescriptions())
            .usingRecursiveComparison()
            .isEqualTo(expectedDescriptions);
    }

    @Test
    void testSavePartner_whenInvalidOgrnKey() {
        var partner = getValidLegalEntityPartner();
        partner.setOgrn("1234567890123");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.ogrn.control_number"));
    }

    @Test
    void testSavePartner_whenInvalidOkpoLength() {
        var partner = getValidLegalEntityPartner();
        partner.setOkpo("1234567890123");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.legal_entity.okpo.length"));

        var partner1 = getValidEntrepreneurPartner(randomAlphabetic(10));
        partner1.setOkpo("123");
        var error1 = given()
            .spec(requestSpec)
            .body(partner1)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        assertThat(error1.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.entrepreneur.okpo.length"));
    }

    @Test
    void testSavePartner_whenInvalidCharsOkpo() {
        var partner = getValidLegalEntityPartner();
        partner.setOkpo("12345АБВ");
        var error = given()
            .spec(requestSpec)
            .body(partner)
            .when()
            .post(baseRoutePath)
            .then()
            .spec(createBadRequestResponseSpec)
            .extract()
            .as(Error.class);
        assertThat(error.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains("Поле содержит недопустимый(-е) символ(-ы): АБВ");
    }

    public static PartnerCreate getValidLegalEntityPartner() {
        return getValidLegalEntityPartner(randomAlphabetic(10));
    }

    public static PartnerCreate getValidLegalEntityPartner(String digitalId) {
        var partner = new PartnerCreate()
            .legalForm(LegalForm.LEGAL_ENTITY)
            .orgName(randomAlphabetic(10))
            .firstName(randomAlphabetic(10))
            .secondName(randomAlphabetic(10))
            .middleName(randomAlphabetic(10))
            .inn(getValidInnNumber(LegalForm.LEGAL_ENTITY))
            .kpp("123456789")
            .ogrn("1035006110083")
            .okpo("12345678")
            .phones( new HashSet<>(List.of("0079241111111")))
            .emails( new HashSet<>(List.of("a.a.a@sberbank.ru")))
            .comment("555555");
        partner.setDigitalId(digitalId);
        return partner;
    }

    public static PartnerCreateFullModel getValidFullModelLegalEntityPartner() {
        return getValidFullModelLegalEntityPartner(randomAlphabetic(10));
    }

    public static PartnerCreateFullModel getValidFullModelLegalEntityPartner(String digitalId) {
        return new PartnerCreateFullModel()
            .digitalId(digitalId)
            .legalForm(LegalForm.LEGAL_ENTITY)
            .orgName(randomAlphabetic(10))
            .firstName(randomAlphabetic(10))
            .secondName(randomAlphabetic(10))
            .middleName(randomAlphabetic(10))
            .inn(getValidInnNumber(LegalForm.LEGAL_ENTITY))
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
                .account(getValidAccountNumber())
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
        var partner = getValidLegalEntityPartner(digitalId);
        partner.setLegalForm(LegalForm.PHYSICAL_PERSON);
        partner.setInn(getValidInnNumber(LegalForm.PHYSICAL_PERSON));
        return partner;
    }

    private static PartnerCreate getValidEntrepreneurPartner(String digitalId) {
        var partner = getValidLegalEntityPartner(digitalId);
        partner.setLegalForm(LegalForm.ENTREPRENEUR);
        partner.setOkpo(VALID_PHYSICAL_OKPO);
        partner.setInn(getValidInnNumber(LegalForm.ENTREPRENEUR));
        partner.setOgrn("314505309900027");
        return partner;
    }

    protected static Partner createValidPartner() {
        return post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidLegalEntityPartner(),
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
            getValidLegalEntityPartner(digitalId),
            Partner.class);
    }

    private static Error createNotValidPartner() {
        var partner = getValidLegalEntityPartner();
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
            .version(partner.getVersion())
            .inn(partner.getInn());
    }

    private Renter createValidRenter(String digitalId) {
        Renter renter = getValidRenter(digitalId);
        return given()
            .spec(requestSpec)
            .body(renter)
            .when()
            .post("/sbbol-partners/renter/create")
            .then()
            .spec(responseSpec)
            .extract()
            .as(Renter.class);
    }
}

package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFilterType;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.SearchPartners;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidBudgetAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountSignControllerTest.createValidAccountsSign;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class PartnerControllerTest extends AbstractIntegrationTest {

    public static final String ORG_NAME_FOR_TEST_PARTNER = "Наименование компании";

    public static final String baseRoutePath = "/partner";

    @Test
    @AllureId("34209")
    void testGetPartner() {
        var partner = getValidPartner();
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            PartnerResponse.class
        );
        assertThat(createdPartner)
            .isNotNull();

        var actualPartner = get(
            baseRoutePath + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            PartnerResponse.class,
            createdPartner.getPartner().getDigitalId(),
            createdPartner.getPartner().getId()
        );
        assertThat(actualPartner)
            .isNotNull();
        assertThat(actualPartner.getPartner())
            .isNotNull()
            .isEqualTo(createdPartner.getPartner());
    }

    @Test
    @AllureId("34174")
    void testGetOnePartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
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
    @AllureId("")
    void testCreateContact2() {
        var partner = getValidPartner(randomAlphabetic(10));
        partner.setEmails(null);
        partner.setPhones(null);
        var CreatePartner = createValidPartner1(partner);
        assertThat(CreatePartner)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version",
                "phones",
                "emails"
            )
            .isEqualTo(CreatePartner);
    }


    @Test
    @AllureId("34151")
    void testGetSearchInnPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(createdPartner1.getPartner().getInn().substring(4))
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
    @AllureId("34141")
    void testGetSearchBudgetPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        createValidBudgetAccount(createdPartner1.getPartner().getId(), createdPartner1.getPartner().getDigitalId());

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
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
    @AllureId("34124")
    void testGetSearchOrgNamePartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(createdPartner1.getPartner().getOrgName().substring(4))
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
            PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    @AllureId("34186")
    void testGetSearchFIOPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.search(
            new SearchPartners()
                .search(createdPartner1.getPartner().getSecondName() + " " + createdPartner1.getPartner().getFirstName())
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
    @AllureId("34127")
    void testGetSearchLegalPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,

            getValidPhysicalPersonPartner(digitalId),
            PartnerResponse.class
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
    @AllureId("34128")
    void testGetSearchPhysicalPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPhysicalPersonPartner(digitalId),
            PartnerResponse.class
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
    @AllureId("34199")
    void testGetSearchEntrepreneurPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidEntrepreneurPartner(digitalId),
            PartnerResponse.class
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
    @AllureId("34209")
    void testGetPartnersNotSignedAccount() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        createValidAccount(createdPartner1.getPartner().getId(), createdPartner1.getPartner().getDigitalId());

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidEntrepreneurPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.accountSignType(PartnersFilter.AccountSignTypeEnum.NOT_SIGNED);
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
    @AllureId("34132")
    void testGetPartnersSignedAccount() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();
        var validAccount = createValidAccount(createdPartner1.getPartner().getId(), createdPartner1.getPartner().getDigitalId());
        createValidAccountsSign(createdPartner1.getPartner().getDigitalId(), validAccount.getId());

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidEntrepreneurPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.accountSignType(PartnersFilter.AccountSignTypeEnum.SIGNED);
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
    @AllureId("")
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
                newPhone.setPhone(randomNumeric(12));
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
            PartnerResponse.class
        );
        assertThat(newUpdatePartner)
            .isNotNull();
        assertThat(newUpdatePartner.getPartner().getFirstName())
            .isEqualTo(newUpdatePartner.getPartner().getFirstName());
        assertThat(newUpdatePartner.getPartner().getFirstName())
            .isNotEqualTo(partner.getFirstName());
        assertThat(newUpdatePartner.getErrors())
            .isNull();

        HashSet<Phone> newPhones1 = new HashSet<>();
        if (partner.getPhones() != null) {
            for (var phone : partner.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion() + 1);
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(12));
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
        partner.setVersion(newUpdatePartner.getPartner().getVersion() + 1);
        var newUpdatePartner1 = put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            updatePartner(partner),
            Error.class
        );
        assertThat(newUpdatePartner1)
            .isNotNull();
        assertThat(newUpdatePartner1.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
    @AllureId("")
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
                newPhone.setPhone(randomNumeric(12));
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
            PartnerResponse.class
        );
        assertThat(newUpdatePartner)
            .isNotNull();
        assertThat(newUpdatePartner.getPartner().getFirstName())
            .isEqualTo(newUpdatePartner.getPartner().getFirstName());
        assertThat(newUpdatePartner.getPartner().getFirstName())
            .isNotEqualTo(partner.getFirstName());
        assertThat(newUpdatePartner.getErrors())
            .isNull();


        HashSet<Phone> newPhones1 = new HashSet<>();
        if (partner.getPhones() != null) {
            for (var phone : partner.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion() + 1);
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(12));
                newPhones1.add(newPhone);
            }
        }
        HashSet<Email> newEmails1 = new HashSet<>();
        if (partner.getEmails() != null) {
            for (var email : partner.getEmails()) {
                var newEmail1 = new Email();
                newEmail1.setVersion(email.getVersion() + 1);
                newEmail1.setId(email.getId());
                newEmail1.setUnifiedId(email.getUnifiedId());
                newEmail1.setDigitalId(email.getDigitalId());
                newEmail1.setEmail(randomAlphabetic(64) + "@" + randomAlphabetic(255));
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
            PartnerResponse.class
        );
        assertThat(newUpdatePartner1)
            .isNotNull();
        assertThat(newUpdatePartner1.getPartner().getFirstName())
            .isEqualTo(newUpdatePartner1.getPartner().getFirstName());
        assertThat(newUpdatePartner1.getPartner().getFirstName())
            .isNotEqualTo(partner.getFirstName());
        assertThat(newUpdatePartner1.getErrors())
            .isNull();
    }


    @Test
    @AllureId("")
    void testNegativeGetAllPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var createdPartner3 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
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
            .isEqualTo(HttpStatus.BAD_REQUEST.name());

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
            .isEqualTo(HttpStatus.BAD_REQUEST.name());

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
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
    @AllureId("34148")
    void testGetAllPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
        );
        assertThat(createdPartner2)
            .isNotNull();

        var createdPartner3 = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class
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
    @AllureId("34178")
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
    @AllureId("34165")
    void testCreateNotValidPartner() {
        var error = createNotValidPartner();
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
    @AllureId("34207")
    void testUpdatePartner() {
        var partner = getValidPartner();
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            PartnerResponse.class
        );
        String newKpp = "999999999";
        var updatePartner = createdPartner.getPartner();
        updatePartner.kpp(newKpp);
        var newUpdatePartner = put(
            baseRoutePath,
            HttpStatus.OK,
            updatePartner,
            PartnerResponse.class
        );

        assertThat(updatePartner)
            .isNotNull();
        assertThat(newUpdatePartner.getPartner().getKpp())
            .isEqualTo(newKpp);
        assertThat(newUpdatePartner.getErrors())
            .isNull();
    }

    @Test
    @AllureId("")
    void testUpdatePartner2() {
        var partner = getValidPartner();
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            PartnerResponse.class
        );
        String newKpp = "999999999";
        var updatePartner = createdPartner.getPartner();
        updatePartner.kpp(newKpp);
        updatePartner.setPhones(null);
        updatePartner.setEmails(null);
        PartnerResponse newUpdatePartner = put(
            baseRoutePath,
            HttpStatus.OK,
            updatePartner,
            PartnerResponse.class
        );

        assertThat(updatePartner)
            .isNotNull();
        assertThat(newUpdatePartner.getPartner().getKpp())
            .isEqualTo(newKpp);
        assertThat(newUpdatePartner.getErrors())
            .isNull();
    }

    @Test
    @AllureId("36943")
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
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(partnerError.getText())
            .contains("Версия записи в базе данных " + (partner.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    @AllureId("36944")
    void positiveTestUpdatePartnerVersion() {
        var partner = createValidPartner();
        var updatePartner = put(
            baseRoutePath,
            HttpStatus.OK,
            updatePartner(partner),
            PartnerResponse.class
        );
        var checkPartner = get(
            baseRoutePath + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            PartnerResponse.class,
            updatePartner.getPartner().getDigitalId(),
            updatePartner.getPartner().getId()
        );
        assertThat(checkPartner)
            .isNotNull();
        assertThat(checkPartner.getPartner().getVersion())
            .isEqualTo(partner.getVersion() + 1);
        assertThat(checkPartner.getErrors())
            .isNull();
    }

    @Test
    @AllureId("34197")
    void testDeletePartner() {
        var createdPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(),
            PartnerResponse.class
        );
        assertThat(createdPartner)
            .isNotNull();
        var actualPartner = get(
            baseRoutePath + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            PartnerResponse.class,
            createdPartner.getPartner().getDigitalId(),
            createdPartner.getPartner().getId()
        );
        assertThat(actualPartner)
            .isNotNull();
        assertThat(actualPartner.getPartner())
            .isNotNull()
            .isEqualTo(createdPartner.getPartner());

        delete(
            baseRoutePath + "/{digitalId}" + "/{id}",
            HttpStatus.NO_CONTENT,
            actualPartner.getPartner().getDigitalId(),
            actualPartner.getPartner().getId()
        ).getBody();

        var searchPartner = get(
            baseRoutePath + "/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            Error.class,
            createdPartner.getPartner().getDigitalId(),
            createdPartner.getPartner().getId()
        );
        assertThat(searchPartner)
            .isNotNull();

        assertThat(searchPartner.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    public static PartnerCreate getValidPartner() {
        return getValidPartner(randomAlphabetic(10));
    }

    public static PartnerCreate getValidPartner(String digitalId) {
        var partner = new PartnerCreate()
            .legalForm(LegalForm.LEGAL_ENTITY)
            .orgName(ORG_NAME_FOR_TEST_PARTNER)
            .firstName("Имя клиента")
            .secondName("Фамилия клиента")
            .middleName("Отчество клиента")
            .inn("4139314257")
            .kpp("123456789")
            .ogrn("1035006110083")
            .okpo("12345678")
            .phones(
                Set.of(
                    "79241111111"
                ))
            .emails(
                Set.of(
                    "a.a.a@sberbank.ru"
                ))
            .comment("555555");
        partner.setDigitalId(digitalId);
        return partner;
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
        return partner;
    }

    protected static Partner createValidPartner() {
        var createPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(),
            PartnerResponse.class
        );
        assertThat(createPartner)
            .isNotNull();
        return createPartner.getPartner();
    }

    protected static Partner createValidPartner1(PartnerCreate partner) {
        var createPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            partner,
            PartnerResponse.class
        );
        assertThat(createPartner)
            .isNotNull();
        return createPartner.getPartner();
    }

    protected static Partner createValidPartner(String digitalId) {
        var createPartner = post(
            baseRoutePath,
            HttpStatus.CREATED,
            getValidPartner(digitalId),
            PartnerResponse.class);
        assertThat(createPartner)
            .isNotNull();
        return createPartner.getPartner();
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

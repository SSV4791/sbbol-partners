package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.model.SearchPartners;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.Set;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidBudgetAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountSignControllerTest.createValidAccountSign;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class PartnerControllerTest extends AbstractIntegrationTest {

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
        filter.setPartnersFilter(PartnerFilter.BUDGET);
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
        filter.setPartnersFilter(PartnerFilter.LEGAL_ENTITY);
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
        filter.setPartnersFilter(PartnerFilter.PHYSICAL_PERSON);
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
        filter.setPartnersFilter(PartnerFilter.ENTREPRENEUR);
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
        createValidAccountSign(createdPartner1.getPartner().getDigitalId(), validAccount.getId());

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
            .orgName("Наименование компании")
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

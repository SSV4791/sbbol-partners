package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.model.SearchPartners;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidBudgetAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountSignControllerTest.createValidAccountSign;

class PartnerControllerTest extends AbstractIntegrationWithOutSbbolTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @AllureId("34209")
    void testGetPartner() {
        var partner = getValidPartner();
        var createdPartner = createPost(baseRoutePath, partner, PartnerResponse.class);
        assertThat(createdPartner)
            .isNotNull();

        var actualPartner = get(
            baseRoutePath + "/{digitalId}" + "/{id}",
            PartnerResponse.class,
            createdPartner.getPartner().getDigitalId(), createdPartner.getPartner().getId()
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
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .offset(1)
                .count(1)
        );

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    @AllureId("34151")
    void testGetSearchInnPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
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

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    @AllureId("34141")
    void testGetSearchBudgetPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();
        createValidBudgetAccount(createdPartner1.getPartner().getId(), createdPartner1.getPartner().getDigitalId());

        var createdPartner2 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersType(PartnersFilter.PartnersTypeEnum.BUDGET);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    @AllureId("34124")
    void testGetSearchOrgNamePartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
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

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    @AllureId("34186")
    void testGetSearchFIOPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
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

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    @AllureId("34127")
    void testGetSearchLegalPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = createPost(baseRoutePath, getValidPhysicalPersonPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersType(PartnersFilter.PartnersTypeEnum.LEGAL_ENTITY);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    @AllureId("34128")
    void testGetSearchPhysicalPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = createPost(baseRoutePath, getValidPhysicalPersonPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersType(PartnersFilter.PartnersTypeEnum.PHYSICAL_PERSON);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    @AllureId("34199")
    void testGetSearchEntrepreneurPersonPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = createPost(baseRoutePath, getValidEntrepreneurPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPartnersType(PartnersFilter.PartnersTypeEnum.ENTREPRENEUR);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(1)
        );

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    @AllureId("34209")
    void testGetPartnersNotSignedAccount() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();
        createValidAccount(createdPartner1.getPartner().getId(), createdPartner1.getPartner().getDigitalId());

        var createdPartner2 = createPost(baseRoutePath, getValidEntrepreneurPartner(digitalId), PartnerResponse.class);
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

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    @AllureId("34132")
    void testGetPartnersSignedAccount() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();
        var validAccount = createValidAccount(createdPartner1.getPartner().getId(), createdPartner1.getPartner().getDigitalId());
        createValidAccountSign(createdPartner1.getPartner().getDigitalId(), validAccount.getId());

        var createdPartner2 = createPost(baseRoutePath, getValidEntrepreneurPartner(digitalId), PartnerResponse.class);
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

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }


    @Test
    @AllureId("34148")
    void testGetAllPartners() {
        var digitalId = randomAlphabetic(10);
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner2)
            .isNotNull();

        var createdPartner3 = createPost(baseRoutePath, getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createdPartner3)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId(digitalId);
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(2)
        );

        var response = post("/partners/view", filter, PartnersResponse.class);
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
        var createdPartner = createPost(baseRoutePath, partner, PartnerResponse.class);
        String newKpp = "999999999";
        var updatePartner = createdPartner.getPartner();
        updatePartner.kpp(newKpp);
        PartnerResponse newUpdatePartner = put(baseRoutePath, updatePartner, PartnerResponse.class);

        assertThat(updatePartner)
            .isNotNull();
        assertThat(newUpdatePartner.getPartner().getKpp())
            .isEqualTo(newKpp);
        assertThat(newUpdatePartner.getErrors())
            .isNull();
    }

    @Test
    @AllureId("34197")
    void testDeletePartner() {
        var createdPartner = createPost(baseRoutePath, getValidPartner(), PartnerResponse.class);
        assertThat(createdPartner)
            .isNotNull();
        var actualPartner =
            get(
                baseRoutePath + "/{digitalId}" + "/{id}",
                PartnerResponse.class,
                createdPartner.getPartner().getDigitalId(), createdPartner.getPartner().getId()
            );
        assertThat(actualPartner)
            .isNotNull();
        assertThat(actualPartner.getPartner())
            .isNotNull()
            .isEqualTo(createdPartner.getPartner());

        delete(
            baseRoutePath + "/{digitalId}" + "/{id}",
            actualPartner.getPartner().getDigitalId(), actualPartner.getPartner().getId()
        );

        var searchPartner =
            getNotFound(
                baseRoutePath + "/{digitalId}" + "/{id}",
                Error.class,
                createdPartner.getPartner().getDigitalId(), createdPartner.getPartner().getId()
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
            .okpo("444444")
            .phones(
                List.of(
                    "+79241111111"
                ))
            .emails(
                List.of(
                    "a.a.a@sberbank.ru"
                ))
            .comment("555555");
        partner.setDigitalId(digitalId);
        return partner;
    }

    private static PartnerCreate getValidPhysicalPersonPartner(String digitalId) {
        var partner = getValidPartner(digitalId);
        partner.setLegalForm(LegalForm.PHYSICAL_PERSON);
        return partner;
    }

    private static PartnerCreate getValidEntrepreneurPartner(String digitalId) {
        var partner = getValidPartner(digitalId);
        partner.setLegalForm(LegalForm.ENTREPRENEUR);
        return partner;
    }

    protected static Partner createValidPartner() {
        var createPartner = createPost("/partner", getValidPartner(), PartnerResponse.class);
        assertThat(createPartner)
            .isNotNull();
        return createPartner.getPartner();
    }

    protected static Partner createValidPartner(String digitalId) {
        var createPartner = createPost("/partner", getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createPartner)
            .isNotNull();
        return createPartner.getPartner();
    }

    private static Error createNotValidPartner() {
        var partner = getValidPartner();
        partner.setInn("222222");
        return createBadRequestPost("/partner", partner);
    }
}

package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PartnerControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partners";

    @Test
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
    void testGetOnePartners() {
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = createPost(baseRoutePath, getValidPartner(), PartnerResponse.class);
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId("111111");
        filter.setPagination(
            new Pagination()
                .offset(1)
                .count(1)
        );

        var response = post(baseRoutePath + "/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isOne();
    }

    @Test
    void testGetAllPartners() {
        var createdPartner1 = createPost(baseRoutePath, getValidPartner(), PartnerResponse.class);
        assertThat(createdPartner1)
            .isNotNull();

        var createdPartner2 = createPost(baseRoutePath, getValidPartner(), PartnerResponse.class);
        assertThat(createdPartner2)
            .isNotNull();

        var filter = new PartnersFilter();
        filter.setDigitalId("111111");
        filter.setPagination(
            new Pagination()
                .offset(0)
                .count(2)
        );

        var response = post(baseRoutePath + "/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isEqualTo(2);
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
    void testCreateNotValidPartner() {
        var error = createNotValidPartner();
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
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

    public static Partner getValidPartner() {
        return getValidPartner("111111");
    }

    public static Partner getValidPartner(String digitalId) {
        return new Partner()
            .version(0L)
            .digitalId(digitalId)
            .partnerType(Partner.PartnerTypeEnum.PARTNER)
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
                    new Phone().phone("+79241111111")
                        .version(0L)
                ))
            .emails(List.of(
                new Email().email("a.a.a@sberbank.ru")
                    .version(0L)
            ))
            .comment("555555")
            ;
    }

    protected static Partner createValidPartner() {
        var createPartner = createPost("/partners", getValidPartner(), PartnerResponse.class);
        assertThat(createPartner)
            .isNotNull();
        return createPartner.getPartner();
    }

    protected static Partner createValidPartner(String digitalId) {
        var createPartner = createPost("/partners", getValidPartner(digitalId), PartnerResponse.class);
        assertThat(createPartner)
            .isNotNull();
        return createPartner.getPartner();
    }

    private static Error createNotValidPartner() {
        var partner = getValidPartner();
        partner.setInn("222222");

        return createBadRequestPost("/partners", partner, Error.class);
    }
}

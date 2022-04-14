package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class PartnerControllerWithSbbolTest extends AbstractIntegrationWithSbbolTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @AllureId("34191")
    void testGetPartner() {
        var response = get(
            baseRoutePath + "/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            Error.class,
            RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    @Test
    @AllureId("34187")
    void testPartners() {
        var filter = new PartnersFilter();
        filter.setDigitalId(RandomStringUtils.randomAlphabetic(10));
        filter.setPagination(
            new Pagination()
                .offset(1)
                .count(1)
        );
        var response = post(
            "/partners/view",
            HttpStatus.NOT_FOUND,
            filter,
            Error.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    @Test
    @AllureId("34156")
    void testCreatePartner() {
        var response = post(baseRoutePath, HttpStatus.NOT_FOUND, PartnerControllerTest.getValidPartner(), Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    @Test
    @AllureId("34194")
    void testUpdatePartner() {
        var partner = new Partner()
            .id(UUID.randomUUID().toString())
            .legalForm(LegalForm.LEGAL_ENTITY)
            .orgName("Наименование компании")
            .firstName("Имя клиента")
            .secondName("Фамилия клиента")
            .middleName("Отчество клиента")
            .inn("4139314257")
            .kpp("123456789")
            .ogrn("1035006110083")
            .okpo("444444")
            .comment("555555");
        partner.setDigitalId(RandomStringUtils.randomAlphabetic(10));
        var response = put(baseRoutePath, HttpStatus.NOT_FOUND, partner, Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    @Test
    @AllureId("34114")
    void testDeletePartner() {
        var response = delete(
            baseRoutePath + "/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)
        ).as(Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }
}

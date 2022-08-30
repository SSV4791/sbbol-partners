package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithSbbolConfiguration;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.partners.handler.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;

@ContextConfiguration(classes = SbbolIntegrationWithSbbolConfiguration.class)
class PartnerControllerWithSbbolTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetPartner() {
        var response = get(
            "/partners/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            Error.class,
            RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
    }

    @Test
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
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
    }

    @Test
    void testCreatePartner() {
        var response = post(baseRoutePath, HttpStatus.NOT_FOUND, PartnerControllerTest.getValidPartner(), Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
    }

    @Test
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
            .okpo("12345678")
            .version(0L)
            .comment("555555");
        partner.setDigitalId(RandomStringUtils.randomAlphabetic(10));
        var response = put(baseRoutePath, HttpStatus.NOT_FOUND, partner, Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
    }

    @Test
    void testDeletePartner() {
        var response = delete(
            "/partners/{digitalId}",
            HttpStatus.NOT_FOUND,
            Map.of("ids", RandomStringUtils.randomAlphabetic(10)),
            RandomStringUtils.randomAlphabetic(10)
        ).as(Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
    }
}

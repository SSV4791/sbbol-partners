package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.getValidPartner;

class PartnerControllerWithSbbolTest extends AbstractIntegrationWithSbbolTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetPartner() {
        var digitalId = RandomStringUtils.randomAlphabetic(10);
        var partner = counterpartyMapper.toPartner(counterparty, digitalId);
        var actualPartner = get(
            baseRoutePath + "/{digitalId}" + "/{id}",
            PartnerResponse.class,
            partner.getDigitalId(), partner.getId()
        );
        assertThat(actualPartner)
            .isNotNull();
        assertThat(actualPartner.getPartner())
            .isNotNull()
            .isEqualTo(partner);
    }

    @Test
    void testPartners() {
        var digitalId = RandomStringUtils.randomAlphabetic(10);
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
        assertThat(response.getPartners().size()).isEqualTo(1);
    }

    @Test
    void testGetAllPartners() {
        var digitalId = RandomStringUtils.randomAlphabetic(10);
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
                .offset(0)
                .count(2)
        );

        var response = post("/partners/view", filter, PartnersResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPartners().size())
            .isEqualTo(1);
    }

    @Test
    void testCreatePartner() {
        var digitalId = RandomStringUtils.randomAlphabetic(10);
        var partner = counterpartyMapper.toPartner(counterparty, digitalId);
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
    void testUpdatePartner() {
        var partner = getValidPartner();
        var createdPartner = createPost(baseRoutePath, partner, PartnerResponse.class);
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
        createValidAccount(createdPartner.getPartner().getId(), createdPartner.getPartner().getDigitalId());
        assertThat(createdPartner)
            .isNotNull();
        var executePartner = counterpartyMapper.toPartner(counterparty, createdPartner.getPartner().getDigitalId());
        var actualPartner =
            get(
                baseRoutePath + "/{digitalId}" + "/{id}",
                PartnerResponse.class,
                createdPartner.getPartner().getDigitalId(), counterparty.getPprbGuid()
            );
        assertThat(actualPartner)
            .isNotNull();
        assertThat(actualPartner.getPartner())
            .isNotNull()
            .isEqualTo(executePartner);

        delete(
            baseRoutePath + "/{digitalId}" + "/{id}",
            actualPartner.getPartner().getDigitalId(), counterparty.getPprbGuid()
        );

        when(legacySbbolAdapter.getByPprbGuid(actualPartner.getPartner().getDigitalId(), counterparty.getPprbGuid())).thenReturn(null);

        var searchPartner =
            getNotFound(
                baseRoutePath + "/{digitalId}" + "/{id}",
                Error.class,
                createdPartner.getPartner().getDigitalId(), counterparty.getPprbGuid()
            );
        assertThat(searchPartner)
            .isNotNull();

        assertThat(searchPartner.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }
}

package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneResponse;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class PartnerPhoneControllerTest extends AbstractIntegrationWithOutSbbolTest {

    public static final String baseRoutePath = "/partner/phone";

    @Test
    void testViewPartnerPhone() {
        Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createPhone(partner.getId());
        createPhone(partner.getId());
        createPhone(partner.getId());
        createPhone(partner.getId());

        var filter1 = new PhonesFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(
                List.of(
                    partner.getId()
                )
            )
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/view",
            filter1,
            PhonesResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getPhones().size())
            .isEqualTo(4);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }

    @Test
    void testCreatePartnerPhone() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var phone = createPhone(partner.getId());
        assertThat(phone)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid")
            .isEqualTo(phone);
    }

    @Test
    void testUpdatePartnerPhone() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var phone = createPhone(partner.getId());
        String newPhone = "bbbb@sber.ru";

        var updatePhone = new Phone();
        updatePhone.id(phone.getId());
        updatePhone.unifiedId(phone.getUnifiedId());
        updatePhone.phone(newPhone);
        var newUpdatePhone = put(baseRoutePath, updatePhone, PhoneResponse.class);

        assertThat(newUpdatePhone)
            .isNotNull();
        assertThat(newUpdatePhone.getPhone().getPhone())
            .isEqualTo(newPhone);
        assertThat(newUpdatePhone.getErrors())
            .isNull();
    }

    @Test
    void testDeletePartnerPhone() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));

        var filter1 = new PhonesFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(
                List.of(
                    partner.getId()
                )
            )
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var actualPhone = post(
            baseRoutePath + "/view",
            filter1,
            PhonesResponse.class
        );
        assertThat(actualPhone)
            .isNotNull();

        var deletePhone =
            delete(
                baseRoutePath + "/{digitalId}" + "/{id}",
                partner.getDigitalId(), actualPhone.getPhones().get(0).getId()
            );
        assertThat(deletePhone)
            .isNotNull();

        var searchPhone = post(
            baseRoutePath + "/view",
            filter1,
            PhonesResponse.class
        );
        assertThat(searchPhone.getPhones())
            .isNull();
    }

    private static Phone getPhone(String partnerUuid) {
        return new Phone()
            .version(0L)
            .id(UUID.randomUUID().toString())
            .unifiedId(partnerUuid)
            .phone(RandomStringUtils.randomAlphabetic(10));
    }

    private static Phone createPhone(String partnerUuid) {
        var phoneResponse = createPost(baseRoutePath, getPhone(partnerUuid), PhoneResponse.class);
        assertThat(phoneResponse)
            .isNotNull();
        assertThat(phoneResponse.getErrors())
            .isNull();
        return phoneResponse.getPhone();
    }
}

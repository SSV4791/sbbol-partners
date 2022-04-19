package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhoneResponse;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class PartnerPhoneControllerTest extends AbstractIntegrationWithOutSbbolTest {

    public static final String baseRoutePath = "/partner/phone";

    @Test
    @AllureId("34161")
    void testViewPartnerPhone() {
        Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createPhone(partner.getId(), partner.getDigitalId());
        createPhone(partner.getId(), partner.getDigitalId());
        createPhone(partner.getId(), partner.getDigitalId());
        createPhone(partner.getId(), partner.getDigitalId());

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
            HttpStatus.OK,
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
    @AllureId("34119")
    void testCreatePartnerPhone() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var expected = getPhone(partner.getId(), partner.getDigitalId());
        var phone = createPhone(expected);
        assertThat(phone)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version"
            )
            .isEqualTo(expected);
    }

    @Test
    @AllureId("34121")
    void testUpdatePartnerPhone() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var phone = createPhone(partner.getId(), partner.getDigitalId());
        String newPhone = "bbbb@sber.ru";

        var updatePhone = new Phone();
        updatePhone.id(phone.getId());
        updatePhone.unifiedId(phone.getUnifiedId());
        updatePhone.digitalId(phone.getDigitalId());
        updatePhone.phone(newPhone);
        updatePhone.setVersion(phone.getVersion() + 1);
        var newUpdatePhone = put(baseRoutePath, HttpStatus.OK, updatePhone, PhoneResponse.class);

        assertThat(newUpdatePhone)
            .isNotNull();
        assertThat(newUpdatePhone.getPhone().getPhone())
            .isEqualTo(newPhone);
        assertThat(newUpdatePhone.getErrors())
            .isNull();
    }

    @Test
    @AllureId("34198")
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
            HttpStatus.OK,
            filter1,
            PhonesResponse.class
        );
        assertThat(actualPhone)
            .isNotNull();

        var deletePhone =
            delete(
                baseRoutePath + "/{digitalId}" + "/{id}",
                HttpStatus.NO_CONTENT,
                partner.getDigitalId(), actualPhone.getPhones().get(0).getId()
            ).getBody();
        assertThat(deletePhone)
            .isNotNull();

        var searchPhone = post(
            baseRoutePath + "/view",
            HttpStatus.OK,
            filter1,
            PhonesResponse.class
        );
        assertThat(searchPhone.getPhones())
            .isNull();
    }

    private static PhoneCreate getPhone(String partnerUuid, String digitalId) {
        return new PhoneCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .phone(RandomStringUtils.randomAlphabetic(10));
    }

    private static Phone createPhone(String partnerUuid, String digitalId) {
        var phoneResponse = post(baseRoutePath, HttpStatus.CREATED, getPhone(partnerUuid, digitalId), PhoneResponse.class);
        assertThat(phoneResponse)
            .isNotNull();
        assertThat(phoneResponse.getErrors())
            .isNull();
        return phoneResponse.getPhone();
    }

    private static Phone createPhone(PhoneCreate phone) {
        var phoneResponse = post(baseRoutePath, HttpStatus.CREATED, phone, PhoneResponse.class);
        assertThat(phoneResponse)
            .isNotNull();
        assertThat(phoneResponse.getErrors())
            .isNull();
        return phoneResponse.getPhone();
    }
}

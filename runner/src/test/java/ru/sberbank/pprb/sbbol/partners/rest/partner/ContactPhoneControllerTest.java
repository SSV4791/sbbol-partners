package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.partners.handler.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.partners.handler.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactPhoneControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact/phone";

    @Test
    void testNegativeViewPartnerPhone() {
        Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        createPhone(contact.getId(), contact.getDigitalId());
        createPhone(contact.getId(), contact.getDigitalId());
        createPhone(contact.getId(), contact.getDigitalId());
        createPhone(contact.getId(), contact.getDigitalId());

        var filter1 = new PhonesFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            )
            .pagination(new Pagination()
                .count(4));
        var response = post(
            "/partner/contact/phones/view",
            HttpStatus.BAD_REQUEST,
            filter1,
            Error.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var filter2 = new PhonesFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            );
        var response1 = post(
            "/partner/contact/phones/view",
            HttpStatus.BAD_REQUEST,
            filter2,
            Error.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var filter3 = new PhonesFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            )
            .pagination(new Pagination()
                .offset(0));
        var response2 = post(
            "/partner/contact/phones/view",
            HttpStatus.BAD_REQUEST,
            filter3,
            Error.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
    }

    @Test
    void testViewContactPhone() {
        Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        createPhone(contact.getId(), contact.getDigitalId());
        createPhone(contact.getId(), contact.getDigitalId());
        createPhone(contact.getId(), contact.getDigitalId());
        createPhone(contact.getId(), contact.getDigitalId());

        var filter1 = new PhonesFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            )
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            "/partner/contact/phones/view",
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
    void testUpdateContactPhoneValidation() {
        Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var phone = createPhone(contact.getId(), contact.getDigitalId());
        phone.setPhone(randomNumeric(13));
        var newUpdatePhone = put(
            baseRoutePath,
            HttpStatus.OK,
            updatePhone(phone),
            Phone.class
        );
        assertThat(newUpdatePhone)
            .isNotNull();
        assertThat(newUpdatePhone.getPhone())
            .isEqualTo(newUpdatePhone.getPhone());
        assertThat(phone.getPhone())
            .isNotEqualTo(newUpdatePhone.getPhone());

        var phone1 = createPhone(contact.getId(), contact.getDigitalId());
        updatePhone(phone1);
        phone1.setPhone("+" + (randomNumeric(12)));
        var newUpdatePhone1 = put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            phone1,
            Error.class
        );
        assertThat(newUpdatePhone1)
            .isNotNull();
        assertThat(newUpdatePhone1.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var phone2 = createPhone(contact.getId(), contact.getDigitalId());
        updatePhone(phone2);
        phone2.setPhone((randomNumeric(11)) + "+");
        var newUpdatePhone2 = put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            phone2,
            Error.class
        );
        assertThat(newUpdatePhone2)
            .isNotNull();
        assertThat(newUpdatePhone2.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var phone3 = createPhone(contact.getId(), contact.getDigitalId());
        updatePhone(phone3);
        phone3.setPhone((randomNumeric(6)) + "+" + (randomNumeric(5)));
        var newUpdatePhone3 = put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            phone3,
            Error.class
        );
        assertThat(newUpdatePhone3)
            .isNotNull();
        assertThat(newUpdatePhone3.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var phone4 = createPhone(contact.getId(), contact.getDigitalId());
        updatePhone(phone4);
        phone4.setPhone((randomNumeric(4)) + "+" + (randomNumeric(5)) + "+");
        var newUpdatePhone4 = put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            phone4,
            Error.class
        );
        assertThat(newUpdatePhone4)
            .isNotNull();
        assertThat(newUpdatePhone4.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
    }

    @Test
    void testCreateContactPhone() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var expected = getPhone(contact.getId(), contact.getDigitalId());
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
    void testUpdateContactPhone() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var phone = createPhone(contact.getId(), contact.getDigitalId());
        var newUpdatePhone = put(
            baseRoutePath,
            HttpStatus.OK,
            updatePhone(phone),
            Phone.class
        );
        assertThat(newUpdatePhone)
            .isNotNull();
        assertThat(newUpdatePhone.getPhone())
            .isEqualTo(newUpdatePhone.getPhone());
        assertThat(phone.getPhone())
            .isNotEqualTo(newUpdatePhone.getPhone());
    }

    @Test
    void negativeTestUpdatePhoneVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var phone = createPhone(contact.getId(), contact.getDigitalId());
        Long version = phone.getVersion() + 1;
        phone.setVersion(version);
        var phoneError = put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            updatePhone(phone),
            Error.class
        );
        assertThat(phoneError.getCode())
            .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
        assertThat(phoneError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains("Версия записи в базе данных " + (phone.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    void positiveTestUpdatePhoneVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var phone = createPhone(contact.getId(), contact.getDigitalId());
        var updatePhone = put(
            baseRoutePath,
            HttpStatus.OK,
            updatePhone(phone),
            Phone.class
        );
        var checkPhone = new PhonesFilter();
        checkPhone.digitalId(updatePhone.getDigitalId());
        checkPhone.unifiedIds(Collections.singletonList(updatePhone.getUnifiedId()));
        checkPhone.pagination(new Pagination()
            .count(4)
            .offset(0));
        var response = post(
            "/partner/contact/phones/view",
            HttpStatus.OK,
            checkPhone,
            PhonesResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getPhones())
            .isNotNull();
        assertThat(response.getPhones()
            .stream()
            .filter(curPhone -> curPhone.getId()
                .equals(phone.getId()))
            .map(Phone::getVersion)
            .findAny()
            .orElse(null))
            .isEqualTo(phone.getVersion() + 1);
    }

    @Test
    void testDeleteContactPhone() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());

        var filter1 = new PhonesFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            )
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var actualPhone = post(
            "/partner/contact/phones/view",
            HttpStatus.OK,
            filter1,
            PhonesResponse.class
        );
        assertThat(actualPhone)
            .isNotNull();

        var deletePhone =
            delete(
                "/partner/contact/phones/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", actualPhone.getPhones().get(0).getId()),
                contact.getDigitalId()
            ).getBody();
        assertThat(deletePhone)
            .isNotNull();

        var searchPhone = post(
            "/partner/contact/phones/view",
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
            .phone(randomNumeric(13));
    }

    private static Phone createPhone(String partnerUuid, String digitalId) {
        return post(baseRoutePath, HttpStatus.CREATED, getPhone(partnerUuid, digitalId), Phone.class);
    }

    private static Phone createPhone(PhoneCreate phone) {
        return post(baseRoutePath, HttpStatus.CREATED, phone, Phone.class);
    }

    public static Phone updatePhone(Phone phone) {
        return new Phone()
            .phone(randomNumeric(13))
            .id(phone.getId())
            .version(phone.getVersion())
            .unifiedId(phone.getUnifiedId())
            .digitalId(phone.getDigitalId());
    }
}

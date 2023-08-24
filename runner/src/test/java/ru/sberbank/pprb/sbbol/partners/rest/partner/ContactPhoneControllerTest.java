package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
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
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactPhoneControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact/phone";

    @Test
    @DisplayName("POST /partner/contact/phones/view негативные сценарии")
    void testNegativeViewPartnerPhone() {
        var partner = step("Подготовка тестовых данных. Partner", () ->
            createValidPartner(RandomStringUtils.randomAlphabetic(10)));
        var contact = step("Подготовка тестовых данных. Contact", () -> {
            var contactTest = createValidContact(partner.getId(), partner.getDigitalId());
            createPhone(contactTest.getId(), contactTest.getDigitalId());
            createPhone(contactTest.getId(), contactTest.getDigitalId());
            createPhone(contactTest.getId(), contactTest.getDigitalId());
            createPhone(contactTest.getId(), contactTest.getDigitalId());
            return contactTest;
        });

        var response = step("Выполнение post-запроса /partner/contact/phones/view. Не заполнено поле pagination.offset", () -> {
            var filter1 = new PhonesFilter()
                .digitalId(partner.getDigitalId())
                .unifiedIds(
                    List.of(
                        contact.getId()))
                .pagination(new Pagination()
                    .count(4));
            return post(
                "/partner/contact/phones/view",
                HttpStatus.BAD_REQUEST,
                filter1,
                Error.class);
        });
        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });

        var response1 = step("Выполнение post-запроса /partner/contact/phones/view. Не заполнено поле pagination", () -> {
            var filter2 = new PhonesFilter()
                .digitalId(partner.getDigitalId())
                .unifiedIds(
                    List.of(
                        contact.getId()));
            return post(
                "/partner/contact/phones/view",
                HttpStatus.BAD_REQUEST,
                filter2,
                Error.class);
        });
        step("Проверка корректности ответа", () -> {
            assertThat(response1)
                .isNotNull();
            assertThat(response1.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });

        var response2 = step("Выполнение post-запроса /partner/contact/phones/view. Не заполнено поле pagination.count", () -> {
            var filter3 = new PhonesFilter()
                .digitalId(partner.getDigitalId())
                .unifiedIds(
                    List.of(
                        contact.getId()))
                .pagination(new Pagination()
                    .offset(0));
            return post(
                "/partner/contact/phones/view",
                HttpStatus.BAD_REQUEST,
                filter3,
                Error.class);
        });
        step("Проверка корректности ответа", () -> {
            assertThat(response2)
                .isNotNull();
            assertThat(response2.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });
    }


    @Test
    @DisplayName("POST /partner/contact/phones/view позитивный сценарий сценарий")
    void testViewContactPhone() {
        var contact = step("Подготовка тестовых данных", () -> {
            Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contactTest = createValidContact(partner.getId(), partner.getDigitalId());
            createPhone(contactTest.getId(), contactTest.getDigitalId());
            createPhone(contactTest.getId(), contactTest.getDigitalId());
            createPhone(contactTest.getId(), contactTest.getDigitalId());
            createPhone(contactTest.getId(), contactTest.getDigitalId());
            return contactTest;
        });

        var response = step("Выполнение post-запроса /partner/contact/phones/view", () -> {
            var filter1 = new PhonesFilter()
                .digitalId(contact.getDigitalId())
                .unifiedIds(
                    List.of(
                        contact.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
            return post(
                "/partner/contact/phones/view",
                HttpStatus.OK,
                filter1,
                PhonesResponse.class);
        });

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getPhones())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.TRUE);
        });
    }

    @Test
    @DisplayName("PUT /partner/contact/phone проверка валидации")
    void testUpdateContactPhoneValidation() {
        var contact = step("Подготовка тестовых данных. Contact", () -> {
            Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });
        var phone = step("Подготовка тестовых данных. Phone", () -> {
            Phone phoneTest = createPhone(contact.getId(), contact.getDigitalId());
            phoneTest.setPhone(randomNumeric(13));
            return phoneTest;
        });

        var newUpdatePhone = step("Выполнение put-запроса /partner/contact/phone. Валидное поле phone", () ->
            put(
                baseRoutePath,
                HttpStatus.OK,
                updatePhone(phone),
                Phone.class));
        step("Проверка корректности ответа", () -> {
            assertThat(newUpdatePhone)
                .isNotNull();
            assertThat(newUpdatePhone.getPhone())
                .isEqualTo(newUpdatePhone.getPhone());
            assertThat(phone.getPhone())
                .isNotEqualTo(newUpdatePhone.getPhone());
        });

        var newUpdatePhone1 = step("Выполнение put-запроса /partner/contact/phone. Невалидное поле phone", () -> {
            var phone1 = createPhone(contact.getId(), contact.getDigitalId());
            updatePhone(phone1);
            phone1.setPhone("+" + (randomNumeric(12)));
            return put(
                baseRoutePath,
                HttpStatus.BAD_REQUEST,
                phone1,
                Error.class);
        });
        step("Проверка корректности ответа", () -> {
            assertThat(newUpdatePhone1)
                .isNotNull();
            assertThat(newUpdatePhone1.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });

        var newUpdatePhone2 = step("Выполнение put-запроса /partner/contact/phone. Невалидное поле phone", () -> {
            var phone2 = createPhone(contact.getId(), contact.getDigitalId());
            updatePhone(phone2);
            phone2.setPhone((randomNumeric(11)) + "+");
            return put(
                baseRoutePath,
                HttpStatus.BAD_REQUEST,
                phone2,
                Error.class);
        });
        step("Проверка корректности ответа", () -> {
            assertThat(newUpdatePhone2)
                .isNotNull();
            assertThat(newUpdatePhone2.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });

        var newUpdatePhone3 = step("Выполнение put-запроса /partner/contact/phone. Невалидное поле phone", () -> {
            var phone3 = createPhone(contact.getId(), contact.getDigitalId());
            updatePhone(phone3);
            phone3.setPhone((randomNumeric(6)) + "+" + (randomNumeric(5)));
            return put(
                baseRoutePath,
                HttpStatus.BAD_REQUEST,
                phone3,
                Error.class);
        });
        step("Проверка корректности ответа", () -> {
            assertThat(newUpdatePhone3)
                .isNotNull();
            assertThat(newUpdatePhone3.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });

        var newUpdatePhone4 = step("Выполнение put-запроса /partner/contact/phone. Невалидное поле phone", () -> {
            var phone4 = createPhone(contact.getId(), contact.getDigitalId());
            updatePhone(phone4);
            phone4.setPhone((randomNumeric(4)) + "+" + (randomNumeric(5)) + "+");
            return put(
                baseRoutePath,
                HttpStatus.BAD_REQUEST,
                phone4,
                Error.class);
        });
        step("Проверка корректности ответа", () -> {
            assertThat(newUpdatePhone4)
                .isNotNull();
            assertThat(newUpdatePhone4.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("POST /partner/contact/phone создание телефона контакта")
    void testCreateContactPhone() {
        var expected = step("Подготовка тестовых данных. PhoneCreate", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return getPhone(contact.getId(), contact.getDigitalId());
        });
        var phone = step("Выполнение post-запроса /partner/contact/phone. Create Phone", () ->
            createPhone(expected));

        step("Проверка корректности ответа", () ->
            assertThat(phone)
                .usingRecursiveComparison()
                .ignoringFields(
                    "id",
                    "version")
                .isEqualTo(expected));
    }

    @Test
    @DisplayName("PUT /partner/contact/phone обновление телефона контакта")
    void testUpdateContactPhone() {
        var phone = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createPhone(contact.getId(), contact.getDigitalId());
        });

        var newUpdatePhone = step("Выполнение put-запроса /partner/contact/phone. Update Phone", () ->
            put(
                baseRoutePath,
                HttpStatus.OK,
                updatePhone(phone),
                Phone.class));

        step("Проверка корректности ответа", () -> {
            assertThat(newUpdatePhone)
                .isNotNull();
            assertThat(newUpdatePhone.getPhone())
                .isEqualTo(newUpdatePhone.getPhone());
            assertThat(phone.getPhone())
                .isNotEqualTo(newUpdatePhone.getPhone());
        });

    }

    @Test
    @DisplayName("PUT /partner/contact/phone негативный сценарий обновления телефона контакта")
    void negativeTestUpdatePhoneVersion() {
        var phone = step("Подготовка тестовых данных. Phone", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createPhone(contact.getId(), contact.getDigitalId());
        });

        Long version = step("Изменение версии", () -> {
            Long versionTest = phone.getVersion() + 1;
            phone.setVersion(versionTest);
            return versionTest;
        });

        var phoneError = step("Выполнение put-запроса /partner/contact/phone. Обновление телефона", () -> put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            updatePhone(phone),
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(phoneError.getCode())
                .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
            assertThat(phoneError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
                .contains("Версия записи в базе данных " + (phone.getVersion() - 1) +
                    " не равна версии записи в запросе version=" + version);
        });
    }

    @Test
    @DisplayName("PUT /partner/contact/phone позитивный сценарий обновления телефона контакта")
    void positiveTestUpdatePhoneVersion() {
        var phone = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createPhone(contact.getId(), contact.getDigitalId());
        });

        var updatePhone = step("Выполнение put-запроса /partner/contact/phone", () ->
            put(
                baseRoutePath,
                HttpStatus.OK,
                updatePhone(phone),
                Phone.class));

        var checkPhone = step("Подготовка фильтра", () -> {
            var phonesFilter = new PhonesFilter();
            phonesFilter.digitalId(updatePhone.getDigitalId());
            phonesFilter.unifiedIds(Collections.singletonList(updatePhone.getUnifiedId()));
            phonesFilter.pagination(new Pagination()
                .count(4)
                .offset(0));
            return phonesFilter;
        });

        var response = step("Выполнение post-запроса /partner/contact/phones/view", () ->
            post(
                "/partner/contact/phones/view",
                HttpStatus.OK,
                checkPhone,
                PhonesResponse.class));

        step("Проверка корректности ответа", () -> {
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
        });
    }

    @Test
    @DisplayName("DELETE /partner/contact/phones/{digitalId} удаление телефона контакта")
    void testDeleteContactPhone() {
        var contact = step("подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });

        var filter1 = step("Подготовка фильтра", () ->
            new PhonesFilter()
                .digitalId(contact.getDigitalId())
                .unifiedIds(
                    List.of(
                        contact.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0)));

        var actualPhone = step("Выполнение post-запроса /partner/contact/phones/view", () ->
            post(
                "/partner/contact/phones/view",
                HttpStatus.OK,
                filter1,
                PhonesResponse.class));
        step("Проверка корректности ответа", () ->
            assertThat(actualPhone)
                .isNotNull());

        var deletePhone = step("Выполнение delete-запроса /partner/contact/phones/{digitalId}", () ->
            delete(
                "/partner/contact/phones/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", actualPhone.getPhones().get(0).getId()),
                contact.getDigitalId()
            ).getBody());
        step("Проверка корректности ответа", () ->
            assertThat(deletePhone)
                .isNotNull());

        var searchPhone = step("Выполнение post-запроса /partner/contact/phones/view", () ->
            post(
                "/partner/contact/phones/view",
                HttpStatus.OK,
                filter1,
                PhonesResponse.class));
        step("Проверка корректности ответа", () ->
            assertThat(searchPhone.getPhones())
                .isNull());
    }

    private static PhoneCreate getPhone(UUID partnerUuid, String digitalId) {
        return new PhoneCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .phone(randomNumeric(13));
    }

    private static Phone createPhone(UUID partnerUuid, String digitalId) {
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

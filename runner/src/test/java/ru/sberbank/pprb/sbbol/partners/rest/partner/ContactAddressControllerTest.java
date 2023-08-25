package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressType;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactAddressControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact";

    @Test
    @DisplayName("GET /partner/contact/addresses/{digitalId}/{id} Получение адреса")
    void testGetContactAddress() {
        var address = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidAddress(contact.getId(), partner.getDigitalId());
        });

        var actualAddress = step("Выполнение get-запроса /partner/contact/addresses/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/addresses" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Address.class,
            address.getDigitalId(), address.getId()));

        step("Проверка корректности ответа", () ->
            assertThat(actualAddress)
                .isNotNull()
                .isEqualTo(address));
    }

    @Test
    @DisplayName("POST /partner/contact/addresses/view Получение адреса для просмотра")
    void testViewContactAddress() {
        var partner = step("Подготовка тестового партнера", () ->
            createValidPartner(randomAlphabetic(10)));
        var contact = step("Подготовка тестового контакта", () ->
            createValidContact(partner.getId(), partner.getDigitalId()));

        step("Подготовка тестовых данных", () -> {
            createValidAddress(contact.getId(), contact.getDigitalId());
            createValidAddress(contact.getId(), contact.getDigitalId());
            createValidAddress(contact.getId(), contact.getDigitalId());
            createValidAddress(contact.getId(), contact.getDigitalId());
            createValidAddress(contact.getId(), contact.getDigitalId());
        });

        var filterWithFourElements = step("Подготовка фильтра с четырьмя элементами", () ->
            new AddressesFilter()
                .digitalId(contact.getDigitalId())
                .unifiedIds(List.of(contact.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0)));

        var responseWithFourElements = step("Выполнение post-запроса /partner/contact/addresses/view, код ответа 200", () -> post(
            baseRoutePath + "/addresses/view",
            HttpStatus.OK,
            filterWithFourElements,
            AddressesResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(responseWithFourElements)
                .isNotNull();
            assertThat(responseWithFourElements.getAddresses())
                .hasSize(4);
        });

        var filterWithPagination = step("Подготовка фильтра с пагинацией", () ->
            new AddressesFilter()
                .digitalId(contact.getDigitalId())
                .unifiedIds(List.of(contact.getId()))
                .type(AddressType.LEGAL_ADDRESS)
                .pagination(new Pagination()
                    .count(4)
                    .offset(0)));

        var responseWithPagination = step("Выполнение post-запроса /partner/contact/addresses/view, код ответа 200, pagination true", () -> post(
            baseRoutePath + "/addresses/view",
            HttpStatus.OK,
            filterWithPagination,
            AddressesResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(responseWithPagination)
                .isNotNull();
            assertThat(responseWithPagination.getAddresses())
                .hasSize(4);
            assertThat(responseWithPagination.getPagination().getHasNextPage())
                .isEqualTo(Boolean.TRUE);
        });
    }

    @Test
    @DisplayName("POST /partner/contact/address Создание контактного адреса")
    void testCreateContactAddress() {
        var contact = step("Подготовка тестового контакта", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });

        var expected = step("Подготовка ожидаемого результата", () ->
            getValidPartnerAddress(contact.getId(), contact.getDigitalId()));
        var address = step("Подготовка валидного адреса", () ->
            createValidAddress(expected));

        step("Проверка корректности ответа", () -> {
            assertThat(address)
                .usingRecursiveComparison()
                .ignoringFields("id", "version")
                .isEqualTo(expected);
        });
    }

    @Test
    @DisplayName("PUT /partner/contact/address Обновление контактного адреса")
    void testUpdateContactAddress() {
        var address = step("Подготовка валидного адреса", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidAddress(contact.getId(), contact.getDigitalId());
        });

        var newUpdateAddress = step("Выполнение PUT-запроса /partner/contact/address, код ответа 200", () -> put(
            baseRoutePath + "/address",
            HttpStatus.OK,
            updateAddress(address),
            Address.class));

        step("Проверка корректности ответа", () -> {
            assertThat(newUpdateAddress)
                .isNotNull();
            assertThat(newUpdateAddress.getStreet())
                .isEqualTo(newUpdateAddress.getStreet());
            assertThat(newUpdateAddress.getStreet())
                .isNotEqualTo(address.getStreet());
        });
    }

    @Test
    @DisplayName("Negative PUT /partner/contact/address редактирование ненайденного адреса")
    void negativeTestUpdateDeletedAddress() {
        var address = step("Подготовка валидного адреса", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            var address1 = createValidAddress(contact.getId(), contact.getDigitalId());
            delete(
                baseRoutePath + "/addresses" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", address1.getId()),
                address1.getDigitalId()
            ).getBody();
            return address1;
        });

        var addressError = step("Выполнение PUT-запроса /partner/contact/address, код ответа 404", () -> put(
            baseRoutePath + "/address",
            HttpStatus.NOT_FOUND,
            updateAddress(address),
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(addressError.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(addressError.getMessage())
                .isEqualTo("Искомая сущность contact_address с id: " +
                    address.getId() + ", digitalId: " + address.getDigitalId() + " не найдена");
        });
    }

    @Test
    @DisplayName("Negative PUT /partner/contact/address Обновление версии контактного адреса")
    void negativeTestUpdateAddressVersion() {
        var address = step("Подготовка валидного адреса", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidAddress(contact.getId(), contact.getDigitalId());
        });

        Long version = step("Получение версии записи", () ->
            address.getVersion() + 1);

        step("Установка версии записи", () ->
            address.setVersion(version));

        var addressError = step("Выполнение PUT-запроса /partner/contact/address, код ответа 400", () -> put(
            baseRoutePath + "/address",
            HttpStatus.BAD_REQUEST,
            updateAddress(address),
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(addressError.getCode())
                .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
            assertThat(addressError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
                .contains("Версия записи в базе данных " + (address.getVersion() - 1) +
                    " не равна версии записи в запросе version=" + version);
        });
    }

    @Test
    @DisplayName("Negative POST /partner/contact/addresses контрагент не найден")
    void createAddressWithNotFoundPartner() {
        var partner = step("Подготовка тестовых данных", () -> {
            var createPartner = createValidPartner(randomAlphabetic(10));
            delete(
                "/partners/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", createPartner.getId()),
                Map.of("Fraud-Meta-Data", getBase64FraudMetaData()),
                createPartner.getDigitalId()
            ).getBody();
            return createPartner;
        });

        var response = step("Выполнение POST-запроса /partner/contact/address, код ответа 404", () -> post(
            baseRoutePath + "/address",
            HttpStatus.NOT_FOUND,
            getValidPartnerAddress(partner.getId(), partner.getDigitalId()),
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response).isNotNull();
            assertThat(response.getCode()).isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(response.getMessage()).isEqualTo("Искомая сущность contact c digitalId: " +
                partner.getDigitalId() + " не найдена");
        });
    }

    @Test
    @DisplayName("Negative POST /partner/contact/addresses Попытка создания адреса с пустыми полями")
    void negativeTestCreateAddressWithAllEmptyField() {
        step("Выполнение PUT-запроса /partner/contact/address, код ответа 400", () -> post(
            baseRoutePath + "/addresses",
            HttpStatus.NOT_FOUND,
            new AddressCreate())
            .then()
            .body("error", equalTo("Not Found")));
    }

    @Test
    @DisplayName("GET /partner/contact/addresses/{digitalId}/{id} Обновление версии контактного адреса")
    void positiveTestUpdateAddressVersion() {
        var address = step("Подготовка валидного адреса", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidAddress(contact.getId(), contact.getDigitalId());
        });

        var addressUpdate = step("Выполнение PUT-запроса /partner/contact/address, код ответа 200", () -> put(
            baseRoutePath + "/address",
            HttpStatus.OK,
            updateAddress(address),
            Address.class));

        var checkAddress = step("Выполнение GET-запроса /partner/contact/addresses/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/addresses" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Address.class,
            addressUpdate.getDigitalId(), addressUpdate.getId()));

        step("Проверка корректности ответа", () -> {
            assertThat(checkAddress)
                .isNotNull();
            assertThat(checkAddress.getVersion())
                .isEqualTo(address.getVersion() + 1);
        });
    }

    @Test
    @DisplayName("DELETE /partner/contact/addresses/{digitalId} Удаление контактного адреса")
    void testDeleteContactAddress() {
        var address = step("Подготовка валидного адреса", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidAddress(contact.getId(), contact.getDigitalId());
        });

        var actualAddress = step("Выполнение GET-запроса /partner/contact/addresses/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/addresses" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Address.class,
            address.getDigitalId(), address.getId()));

        step("Проверка корректности ответа", () ->
            assertThat(actualAddress)
                .isNotNull()
                .isEqualTo(address));

        var deleteAddress = step("Выполнение Delete-запроса /partner/contact/addresses/{digitalId}, код ответа 204", () -> delete(
            baseRoutePath + "/addresses" + "/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("ids", actualAddress.getId()),
            actualAddress.getDigitalId()
        ).getBody());

        step("Проверка корректности ответа", () -> {
            assertThat(deleteAddress)
                .isNotNull();
        });

        var searchAddress = step("Выполнение GET-запроса /partner/contact/addresses/{digitalId}/{id}, код ответа 404", () -> get(
            baseRoutePath + "/addresses" + "/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            Error.class,
            address.getDigitalId(), address.getId()));

        step("Проверка корректности ответа", () -> {
            assertThat(searchAddress)
                .isNotNull();
            assertThat(searchAddress.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("DELETE /partner/contact/addresses/{digitalId} Попытка повторного удаление контактного адреса")
    void testNegativeDeleteContactAddress() {
        var address = step("Подготовка валидного адреса", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidAddress(contact.getId(), contact.getDigitalId());
        });

        step("Выполнение Delete-запроса /partner/contact/addresses/{digitalId}, код ответа 204", () -> delete(
            baseRoutePath + "/addresses" + "/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("ids", address.getId()),
            address.getDigitalId()
        ).getBody());

        step("Выполнение Delete-запроса /partner/contact/addresses/{digitalId}, код ответа 404", () -> delete(
            baseRoutePath + "/addresses" + "/{digitalId}",
            HttpStatus.NOT_FOUND,
            Map.of("ids", address.getId()),
            address.getDigitalId()
        ).then()
            .body("message", equalTo("Искомая сущность contact_address с id: " + address.getId() + ", digitalId: " + address.getDigitalId() + " не найдена")));
    }

    private static Address createValidAddress(UUID partnerUuid, String digitalId) {
        return post(baseRoutePath + "/address", HttpStatus.CREATED, getValidPartnerAddress(partnerUuid, digitalId), Address.class);
    }

    private static Address createValidAddress(AddressCreate address) {
        return post(baseRoutePath + "/address", HttpStatus.CREATED, address, Address.class);
    }

    private static AddressCreate getValidPartnerAddress(UUID partnerUuid, String digitalId) {
        return new AddressCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .building("1")
            .buildingBlock(RandomStringUtils.randomAlphabetic(300))
            .city("3")
            .flat("4")
            .location("5")
            .region("6")
            .regionCode("7")
            .street("8")
            .type(AddressType.LEGAL_ADDRESS)
            .zipCode("9");
    }

    public static Address updateAddress(Address address) {
        return new Address()
            .id(address.getId())
            .version(address.getVersion())
            .unifiedId(address.getUnifiedId())
            .digitalId(address.getDigitalId())
            .building(address.getBuilding())
            .buildingBlock(address.getBuildingBlock())
            .city(address.getCity())
            .flat(address.getFlat())
            .location(address.getLocation())
            .region(address.getRegion())
            .regionCode(address.getRegionCode())
            .street(randomAlphabetic(10))
            .type(address.getType())
            .zipCode(address.getZipCode());
    }
}

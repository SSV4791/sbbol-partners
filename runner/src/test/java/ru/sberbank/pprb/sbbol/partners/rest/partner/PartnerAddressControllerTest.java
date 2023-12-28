package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressType;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.model.AddressType.*;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class PartnerAddressControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @DisplayName("GET /partner/addresses/{digitalId}/{id} получение адреса партнера")
    void testGetPartnerAddress() {
        var address = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            return createValidAddress(partner.getId(), partner.getDigitalId());
        });

        var actualAddress = step("Выполнение get-запроса /partner/addresses/{digitalId}/{id}", () ->
            get(
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
    @DisplayName("POST /partner/addresses/view успешный запрос адреса с фильтром")
    void testViewPartnerAddress() {
        var partner = step("Подготовка тестовых данных", () -> {
            Partner validPartner = createValidPartner(randomAlphabetic(10));
            createValidAddress(validPartner.getId(), validPartner.getDigitalId());
            createValidAddress(validPartner.getId(), validPartner.getDigitalId());
            createValidAddress(validPartner.getId(), validPartner.getDigitalId());
            createValidAddress(validPartner.getId(), validPartner.getDigitalId());
            createValidAddress(validPartner.getId(), validPartner.getDigitalId());
            return validPartner;
        });

        var filter1 = step("Фильтр для запроса адресов партнеров", () ->
            new AddressesFilter()
                .digitalId(partner.getDigitalId())
                .unifiedIds(List.of(partner.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0)));
        var response1 = step("Выполнение post-запроса /partner/addresses/view", () ->
            post(
                baseRoutePath + "/addresses/view",
                HttpStatus.OK,
                filter1,
                AddressesResponse.class));
        step("Проверка корректности ответа", () -> {
            assertThat(response1)
                .isNotNull();
            assertThat(response1.getAddresses())
                .hasSize(4);
        });

        var filter2 = step("Фильтр для запроса адресов партнеров", () ->
            new AddressesFilter()
                .digitalId(partner.getDigitalId())
                .unifiedIds(List.of(partner.getId()))
                .type(LEGAL_ADDRESS)
                .pagination(new Pagination()
                    .count(4)
                    .offset(0)));
        var response2 = step("Выполнение post-запроса /partner/addresses/view", () ->
            post(
                baseRoutePath + "/addresses/view",
                HttpStatus.OK,
                filter2,
                AddressesResponse.class));
        step("Проверка корректности ответа", () -> {
            assertThat(response2)
                .isNotNull();
            assertThat(response2.getAddresses())
                .hasSize(4);
            assertThat(response2.getPagination().getHasNextPage())
                .isEqualTo(Boolean.TRUE);
        });
    }

    @Test
    @DisplayName("POST /partner/address создание валидного адреса")
    void testCreatePartnerAddress() {
        var expected = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            return getValidPartnerAddress(partner.getId(), partner.getDigitalId());
        });

        var address = step("Выполнение post-запроса /partner/address", () ->
            createValidAddress(expected));

        step("Проверка корректности ответа", () -> {
            assertThat(address)
                .usingRecursiveComparison()
                .ignoringFields(
                    "id",
                    "version")
                .isEqualTo(expected);
        });
    }

    @Test
    @DisplayName("POST /partner/address создание адреса с невалидным типом")
    void testCreatePartnerAddress_invalidType() {
        var partner = step("Подготовка тестовых данных", () ->
            createValidPartner(RandomStringUtils.randomAlphabetic(10)));

        var error = step("Выполнение post-запроса /partner/address", () ->
            post(
                baseRoutePath + "/address",
                HttpStatus.BAD_REQUEST,
                getValidPartnerAddress(partner.getId(), partner.getDigitalId()).type(REGISTRATION_ADDRESS),
                Error.class
            ));

        step("Проверка корректности ответа", () -> {
            var expectedDescription = new Descriptions()
                .field("type")
                .addMessageItem(MessagesTranslator.toLocale("validation.partner.address.legal_entity_or_entrepreneur.type"));

            assertThat(error)
                .isNotNull();
            assertThat(error.getDescriptions())
                .contains(expectedDescription);
        });
    }

    @Test
    @DisplayName("PUT /partner/address обновление адреса партнера")
    void testUpdatePartnerAddress() {
        var address = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            return createValidAddress(partner.getId(), partner.getDigitalId());
        });

        var newUpdateAddress = step("Выполнение put-запроса /partner/address", () ->
            put(
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
    @DisplayName("PUT /partner/address обновление адреса партнера")
    void testUpdatePartnerAddress_invalidType() {
        var address = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            return createValidAddress(partner.getId(), partner.getDigitalId());
        });

        var error = step("Выполнение put-запроса /partner/address", () ->
            put(
                baseRoutePath + "/address",
                HttpStatus.BAD_REQUEST,
                updateAddress(address).type(RESIDENTIAL_ADDRESS),
                Error.class));

        step("Проверка корректности ответа", () -> {
            var expectedDescription = new Descriptions()
                .field("type")
                .addMessageItem(MessagesTranslator.toLocale("validation.partner.address.legal_entity_or_entrepreneur.type"));

            assertThat(error)
                .isNotNull();
            assertThat(error.getDescriptions())
                .contains(expectedDescription);
        });
    }

    @Test
    @DisplayName("PUT /partner/address негативный сценарий обновления версии адреса")
    void negativeTestUpdateAddressVersion() {
        var address = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            return createValidAddress(partner.getId(), partner.getDigitalId());
        });

        Long version = step("Изменение версии адреса", () -> {
            Long versionTest = address.getVersion() + 1;
            address.setVersion(versionTest);
            return versionTest;
        });

        var addressError = step("Выполнение put-запроса /partner/address", () ->
            put(
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
    @DisplayName("PUT /partner/address позитивный сценарий обновления версии адреса")
    void positiveTestUpdateAddressVersion() {
        var address = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            return createValidAddress(partner.getId(), partner.getDigitalId());
        });

        var addressUpdate = step("Выполнение put-запроса /partner/address", () ->
            put(
                baseRoutePath + "/address",
                HttpStatus.OK,
                updateAddress(address),
                Address.class));

        var checkAddress = step("Выполнение get-запроса /partner/addresses/{digitalId}/{id}", () ->
            get(
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
    @DisplayName("DELETE /partner/addresses/{digitalId} удаление адреса")
    void testDeletePartnerAddress() {
        var address = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            return createValidAddress(partner.getId(), partner.getDigitalId());
        });

        var actualAddress = step("Выполнение delete-запроса /partner/addresses/{digitalId}/{id}", () ->
            get(
                baseRoutePath + "/addresses" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Address.class,
                address.getDigitalId(), address.getId()));

        step("Проверка корректности ответа", () ->
            assertThat(actualAddress)
                .isNotNull()
                .isEqualTo(address));

        var deleteAddress = step("Выполнение delete-запроса /partner/addresses/{digitalId}", () ->
            delete(
                baseRoutePath + "/addresses" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", actualAddress.getId()),
                actualAddress.getDigitalId())
                .getBody());

        step("Проверка корректности ответа", () ->
            assertThat(deleteAddress)
                .isNotNull());

        var searchAddress = step("Выполнение get-запроса /partner/addresses/{digitalId}/{id}", () ->
            get(
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

    private static Address createValidAddress(UUID partnerUuid, String digitalId) {
        return post(
            baseRoutePath + "/address",
            HttpStatus.CREATED,
            getValidPartnerAddress(partnerUuid, digitalId),
            Address.class
        );
    }

    private static Address createValidAddress(AddressCreate address) {
        return post(
            baseRoutePath + "/address",
            HttpStatus.CREATED,
            address,
            Address.class
        );
    }

    private static AddressCreate getValidPartnerAddress(UUID partnerUuid, String digitalId) {
        return new AddressCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .building("1")
            .buildingBlock("2")
            .city("3")
            .area("10")
            .flat("4")
            .location("5")
            .region("6")
            .regionCode("7")
            .street("8")
            .type(LEGAL_ADDRESS)
            .zipCode("9")
            ;
    }

    public static Address updateAddress(Address address) {
        return new Address()
            .id(address.getId())
            .version(address.getVersion())
            .unifiedId(address.getUnifiedId())
            .digitalId(address.getDigitalId())
            .building(address.getBuilding())
            .buildingBlock(address.getBuildingBlock())
            .area(address.getArea())
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

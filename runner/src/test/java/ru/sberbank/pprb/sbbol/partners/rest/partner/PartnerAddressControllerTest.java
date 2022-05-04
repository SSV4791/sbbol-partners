package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class PartnerAddressControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @AllureId("34158")
    void testGetPartnerAddress() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var address = createValidAddress(partner.getId(), partner.getDigitalId());
        var actualAddress =
            get(
                baseRoutePath + "/address" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                AddressResponse.class,
                address.getDigitalId(), address.getId()
            );
        assertThat(actualAddress)
            .isNotNull();
        assertThat(actualAddress.getAddress())
            .isNotNull()
            .isEqualTo(address);
    }

    @Test
    @AllureId("34202")
    void testViewPartnerAddress() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createValidAddress(partner.getId(), partner.getDigitalId());
        createValidAddress(partner.getId(), partner.getDigitalId());
        createValidAddress(partner.getId(), partner.getDigitalId());
        createValidAddress(partner.getId(), partner.getDigitalId());
        createValidAddress(partner.getId(), partner.getDigitalId());

        var filter1 = new AddressesFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(List.of(partner.getId()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response1 =
            post(
                baseRoutePath + "/address/view",
                HttpStatus.OK,
                filter1,
                AddressesResponse.class
            );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getAddresses().size())
            .isEqualTo(4);
        var filter2 = new AddressesFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(List.of(partner.getId()))
            .type("LEGAL_ADDRESS")
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response2 =
            post(
                baseRoutePath + "/address/view",
                HttpStatus.OK,
                filter2,
                AddressesResponse.class
            );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getAddresses().size())
            .isEqualTo(4);
        assertThat(response2.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }

    @Test
    @AllureId("34204")
    void testCreatePartnerAddress() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var expected = getValidPartnerAddress(partner.getId(), partner.getDigitalId());
        var address = createValidAddress(expected);
        assertThat(address)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version"
            )
            .isEqualTo(expected);
    }

    @Test
    @AllureId("34173")
    void testUpdatePartnerAddress() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var address = createValidAddress(partner.getId(), partner.getDigitalId());
        var newUpdateAddress = put(
            baseRoutePath + "/address",
            HttpStatus.OK,
            updateAddress(address),
            AddressResponse.class
        );
        assertThat(newUpdateAddress)
            .isNotNull();
        assertThat(newUpdateAddress.getAddress().getStreet())
            .isEqualTo(newUpdateAddress.getAddress().getStreet());
        assertThat(newUpdateAddress.getAddress().getStreet())
            .isNotEqualTo(address.getStreet());
        assertThat(newUpdateAddress.getErrors())
            .isNull();
    }

    @Test
    @AllureId("36941")
    void negativeTestUpdateAddressVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var address = createValidAddress(partner.getId(), partner.getDigitalId());
        Long version = address.getVersion() + 1;
        address.setVersion(version);
        var addressError = put(
            baseRoutePath + "/address",
            HttpStatus.BAD_REQUEST,
            updateAddress(address),
            Error.class
        );
        assertThat(addressError.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(addressError.getText())
            .contains("Версия записи в базе данных " + (address.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    @AllureId("36942")
    void positiveTestUpdateAddressVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var address = createValidAddress(partner.getId(), partner.getDigitalId());
        var addressUpdate = put(
            baseRoutePath + "/address",
            HttpStatus.OK,
            updateAddress(address),
            AddressResponse.class
        );
        var checkAddress = get(
            baseRoutePath + "/address" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            AddressResponse.class,
            addressUpdate.getAddress().getDigitalId(), addressUpdate.getAddress().getId());
        assertThat(checkAddress)
            .isNotNull();
        assertThat(checkAddress.getAddress().getVersion())
            .isEqualTo(address.getVersion() + 1);
        assertThat(checkAddress.getErrors())
            .isNull();
    }

    @Test
    @AllureId("34172")
    void testDeletePartnerAddress() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var address = createValidAddress(partner.getId(), partner.getDigitalId());
        var actualAddress =
            get(
                baseRoutePath + "/address" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                AddressResponse.class,
                address.getDigitalId(), address.getId()
            );
        assertThat(actualAddress)
            .isNotNull();

        assertThat(actualAddress.getAddress())
            .isNotNull()
            .isEqualTo(address);

        var deleteAddress =
            delete(
                baseRoutePath + "/address" + "/{digitalId}" + "/{id}",
                HttpStatus.NO_CONTENT,
                actualAddress.getAddress().getDigitalId(), actualAddress.getAddress().getId()
            ).getBody();
        assertThat(deleteAddress)
            .isNotNull();

        var searchAddress =
            get(
                baseRoutePath + "/address" + "/{digitalId}" + "/{id}",
                HttpStatus.NOT_FOUND,
                Error.class,
                address.getDigitalId(), address.getId()
            );

        assertThat(searchAddress)
            .isNotNull();

        assertThat(searchAddress.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    private static Address createValidAddress(String partnerUuid, String digitalId) {
        var response = post(
            baseRoutePath + "/address",
            HttpStatus.CREATED,
            getValidPartnerAddress(partnerUuid, digitalId),
            AddressResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getErrors())
            .isNull();
        return response.getAddress();
    }

    private static Address createValidAddress(AddressCreate address) {
        var response = post(
            baseRoutePath + "/address",
            HttpStatus.CREATED,
            address,
            AddressResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getErrors())
            .isNull();
        return response.getAddress();
    }

    private static AddressCreate getValidPartnerAddress(String partnerUuid, String digitalId) {
        return new AddressCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .building("1")
            .buildingBlock("2")
            .city("3")
            .flat("4")
            .location("5")
            .region("6")
            .regionCode("7")
            .street("8")
            .type(AddressCreate.TypeEnum.LEGAL_ADDRESS)
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

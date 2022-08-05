package ru.sberbank.pprb.sbbol.partners.rest.partner;

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

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactAddressControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact";

    @Test
    void testGetContactAddress() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var address = createValidAddress(contact.getId(), partner.getDigitalId());
        var actualAddress = get(
            baseRoutePath + "/addresses" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Address.class,
            address.getDigitalId(), address.getId()
        );
        assertThat(actualAddress)
            .isNotNull()
            .isEqualTo(address);
    }

    @Test
    void testViewContactAddress() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        createValidAddress(contact.getId(), contact.getDigitalId());
        createValidAddress(contact.getId(), contact.getDigitalId());
        createValidAddress(contact.getId(), contact.getDigitalId());
        createValidAddress(contact.getId(), contact.getDigitalId());
        createValidAddress(contact.getId(), contact.getDigitalId());

        var filter1 = new AddressesFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(List.of(contact.getId()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response1 = post(
            baseRoutePath + "/addresses/view",
            HttpStatus.OK,
            filter1,
            AddressesResponse.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getAddresses().size())
            .isEqualTo(4);

        var filter2 = new AddressesFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(List.of(contact.getId()))
            .type(AddressType.LEGAL_ADDRESS)
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response2 = post(
            baseRoutePath + "/addresses/view",
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
    void testCreateContactAddress() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var expected = getValidPartnerAddress(contact.getId(), contact.getDigitalId());
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
    void testUpdateContactAddress() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var address = createValidAddress(contact.getId(), contact.getDigitalId());
        var newUpdateAddress = put(
            baseRoutePath + "/address",
            HttpStatus.OK,
            updateAddress(address),
            Address.class
        );
        assertThat(newUpdateAddress)
            .isNotNull();
        assertThat(newUpdateAddress.getStreet())
            .isEqualTo(newUpdateAddress.getStreet());
        assertThat(newUpdateAddress.getStreet())
            .isNotEqualTo(address.getStreet());
    }

    @Test
    void negativeTestUpdateAddressVersion() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var address = createValidAddress(contact.getId(), contact.getDigitalId());
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
        assertThat(addressError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains("Версия записи в базе данных " + (address.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    void positiveTestUpdateAddressVersion() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var address = createValidAddress(contact.getId(), contact.getDigitalId());
        var addressUpdate = put(
            baseRoutePath + "/address",
            HttpStatus.OK,
            updateAddress(address),
            Address.class
        );
        var checkAddress = get(
            baseRoutePath + "/addresses" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Address.class,
            addressUpdate.getDigitalId(), addressUpdate.getId());
        assertThat(checkAddress)
            .isNotNull();
        assertThat(checkAddress.getVersion())
            .isEqualTo(address.getVersion() + 1);
    }

    @Test
    void testDeleteContactAddress() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var address = createValidAddress(contact.getId(), contact.getDigitalId());
        var actualAddress =
            get(
                baseRoutePath + "/addresses" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Address.class,
                address.getDigitalId(), address.getId()
            );
        assertThat(actualAddress)
            .isNotNull()
            .isEqualTo(address);

        var deleteAddress =
            delete(
                baseRoutePath + "/addresses" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", actualAddress.getId()),
                actualAddress.getDigitalId()
            ).getBody();
        assertThat(deleteAddress)
            .isNotNull();

        var searchAddress =
            get(
                baseRoutePath + "/addresses" + "/{digitalId}" + "/{id}",
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
        return post(baseRoutePath + "/address", HttpStatus.CREATED, getValidPartnerAddress(partnerUuid, digitalId), Address.class);
    }

    private static Address createValidAddress(AddressCreate address) {
        return post(baseRoutePath + "/address", HttpStatus.CREATED, address, Address.class);
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

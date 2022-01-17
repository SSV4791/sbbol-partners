package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class PartnerAddressControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetPartnerAddress() {
        var partner = createValidPartner();
        var address = createValidAddress(partner.getId());
        var actualAddress = get(
            baseRoutePath + "/address" + "/{digitalId}" + "/{id}",
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
    void testViewPartnerAddress() {
        var partner = createValidPartner("3333");
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
        var response1 = post(
            baseRoutePath + "/address/view",
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
        var response2 = post(
            baseRoutePath + "/address/view",
            filter2,
            AddressesResponse.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getAddresses().size())
            .isEqualTo(4);
    }

    @Test
    void testCreatePartnerAddress() {
        var partner = createValidPartner();
        var address = createValidAddress(partner.getId());
        assertThat(address)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "unifiedUuid")
            .isEqualTo(address);
    }

    @Test
    void testUpdatePartnerAddress() {
        var partner = createValidPartner();
        var address = createValidAddress(partner.getId());
        String newName = "Новое наименование";
        var updateAddress = new Address();
        updateAddress.id(address.getId());
        updateAddress.digitalId(address.getDigitalId());
        updateAddress.unifiedId(address.getUnifiedId());
        updateAddress.city(newName);
        var newUpdateAddress = put(baseRoutePath + "/address", updateAddress, AddressResponse.class);

        assertThat(newUpdateAddress)
            .isNotNull();
        assertThat(newUpdateAddress.getAddress().getCity())
            .isEqualTo(newName);
        assertThat(newUpdateAddress.getErrors())
            .isNull();
    }

    @Test
    void testDeletePartnerAddress() {
        var partner = createValidPartner();
        var address = createValidAddress(partner.getId());
        var actualAddress =
            get(
                baseRoutePath + "/address" + "/{digitalId}" + "/{id}",
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
                actualAddress.getAddress().getDigitalId(), actualAddress.getAddress().getId()
            );
        assertThat(deleteAddress)
            .isNotNull();

        var searchAddress =
            getNotFound(
                baseRoutePath + "/address" + "/{digitalId}" + "/{id}",
                Error.class,
                address.getDigitalId(), address.getId()
            );

        assertThat(searchAddress)
            .isNotNull();

        assertThat(searchAddress.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    private static Address createValidAddress(String partnerUuid, String digitalId) {
        var response = createPost(baseRoutePath + "/address", getValidPartnerAddress(partnerUuid, digitalId), AddressResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getErrors())
            .isNull();
        return response.getAddress();
    }

    private static Address createValidAddress(String partnerUuid) {
        var response = createPost(baseRoutePath + "/address", getValidPartnerAddress(partnerUuid), AddressResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getErrors())
            .isNull();
        return response.getAddress();
    }

    private static Address getValidPartnerAddress(String partnerUuid) {
        return getValidPartnerAddress(partnerUuid, "111111");
    }

    private static Address getValidPartnerAddress(String partnerUuid, String digitalId) {
        return new Address()
            .version(0L)
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
            .type(Address.TypeEnum.LEGAL_ADDRESS)
            .zipCode("9")
            ;
    }
}

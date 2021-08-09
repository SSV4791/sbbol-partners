package ru.sberbank.pprb.sbbol.partners;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterAddress;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterIdentifier;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterListResponse;
import ru.sberbank.pprb.sbbol.partners.renter.model.Version;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
class RenterTest extends BaseControllerTest{

    RenterClient renterClient;

    @Autowired
    public RenterTest(RenterClient renterClient) {
        this.renterClient = renterClient;
    }

    @Test
    void version() {
        Version version = renterClient.version();
        assertThat(version)
            .isNotNull()
            .isEqualTo(version.ver("1.0.0"));
    }

    @Test
    void testCreateValidRenter() {
        Renter renter = getValidRenter();
        Renter result = renterClient.createRenter(renter);

        assertThat(result).isNotNull();
        assertThat(result.getCheckResults()).isNull();
        assertThat(result).isEqualToIgnoringGivenFields(renter, "uuid");
    }

    @Test
    void testCreateInvalidRenter() {
        Renter renter = getValidRenter();
        renter.setOgrn("1");
        Renter result = renterClient.createRenter(renter);

        assertThat(result).isNotNull();
        assertThat(result.getCheckResults()).isNotNull();
        assertThat(result.getUuid()).isNull();
    }

    @Test
    void testUpdateValidRenter() {
        Renter renter = getValidRenter();
        Renter createdRenter = renterClient.createRenter(renter);
        String newKpp ="999999999";
        createdRenter.setKpp(newKpp);
        Renter updated = renterClient.updateRenter(createdRenter);

        assertThat(updated).isNotNull();
        assertThat(updated.getCheckResults()).isNull();
        assertThat(updated.getKpp()).isEqualTo(newKpp);
    }

    @Test
    void testUpdateInvalidRenter() {
        Renter renter = getValidRenter();
        Renter createdRenter = renterClient.createRenter(renter);
        createdRenter.setOgrn("1");
        Renter updated = renterClient.updateRenter(createdRenter);

        assertThat(updated).isNotNull();
        assertThat(updated.getCheckResults()).isNotNull();
        assertThat(updated.getUuid()).isNull();
    }

    @Test
    void testGetRenter() {
        Renter renter = getValidRenter();
        Renter createdRenter = renterClient.createRenter(renter);
        Renter testRenter = renterClient.getRenter(new RenterIdentifier().digitalId("1").uuid(createdRenter.getUuid()));

        assertThat(testRenter)
            .isNotNull()
            .isEqualTo(createdRenter);
    }

    @Test
    void getGetRentersList() {
        RenterFilter filter = new RenterFilter().digitalId("999");
        RenterListResponse response = renterClient.getRenters(filter);

        assertThat(response).isNotNull();
        assertThat(response.getItems().size()).isEqualTo(0);

        Renter renter = getValidRenter();
        renter.setDigitalId("999");
        renterClient.createRenter(renter);

        response = renterClient.getRenters(filter);
        assertThat(response).isNotNull();
        assertThat(response.getItems().size()).isEqualTo(1);

        Renter renter2 = getValidRenter();
        renter2.setDigitalId("999");
        renterClient.createRenter(renter2);

        response = renterClient.getRenters(filter);
        assertThat(response).isNotNull();
        assertThat(response.getItems().size()).isEqualTo(2);
    }

    private Renter getValidRenter() {
        RenterAddress address = new RenterAddress().zipCode("655511")
            .regionCode("42")
            .region("Кемеровская область")
            .city("Кемерово")
            .locality("Кемерово")
            .street("Ленина")
            .building("162")
            .buildingBlock("1")
            .flat("55");

        return new Renter()
            .digitalId("1")
            .type(Renter.TypeEnum.LEGAL_ENTITY)
            .legalName("ОАО Рога и копыта")
            .inn("132456789132")
            .kpp("0")
            .ogrn("123456789012345")
            .okpo("1234567890")
            .account("40702810538261023926")
            .bankBic("044525225")
            .bankName("ПАО СБЕРБАНК")
            .bankAccount("30101810400000000225")
            .phoneNumbers("+79991112233")
            .emails("roga@mail.ru")
            .legalAddress(address)
            .physicalAddress(address);
    }
}

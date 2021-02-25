package ru.sberbank.pprb.sbbol.partners;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterAddress;
import ru.sberbank.pprb.sbbol.partners.renter.model.Version;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
public class RenterTest extends BaseControllerTest{

    RenterClient renterClient;

    @Autowired
    public RenterTest(RenterClient renterClient) {
        this.renterClient = renterClient;
    }

    @Test
    public void version() {
        Version version = renterClient.version();
        assertThat(version)
                .isNotNull()
                .isEqualTo(version.ver("1.0.0"));

    }

    @Test
    public void testCreateRenter() {
        RenterAddress address = new RenterAddress().zipCode("655511")
                .regionCode("42")
                .region("Кемеровская область")
                .city("Кемерово")
                .locality("Кемерово")
                .street("Ленина")
                .building("162")
                .buildingBlock("1")
                .flat("55");
        Renter renter = new Renter()
                .digitalId("1")
                .type(Renter.TypeEnum.LEGAL_ENTITY)
                .legalName("ОАО Рога и копыта")
                .inn("132456789132")
                .kpp("0")
                .ogrn("0")
                .okpo("0")
                .account("40702810538261023926")
                .bankBic("044525225")
                .bankName("ПАО СБЕРБАНК")
                .bankAccount("30101810400000000225")
                .phoneNumbers("+79991112233")
                .emails("roga@mail.ru")
                .legalAddress(address)
                .physicalAddress(address);


        Renter result = renterClient.createRenter(renter);


    }



}

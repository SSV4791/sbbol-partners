package ru.sberbank.pprb.sbbol.partners.rest.renter;

import ru.sberbank.pprb.sbbol.partners.entity.renter.DulType;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterAddress;

import java.time.LocalDate;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class RenterUtils {

    public static Renter getValidRenter(String digitalId) {
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
            .digitalId(digitalId)
            .type(Renter.TypeEnum.LEGAL_ENTITY)
            .legalName("ОАО Рога и копыта")
            .inn("132456789132")
            .kpp("0")
            .ogrn("123456789012345")
            .okpo("1234567890")
            .lastName("Фамилия")
            .firstName("Имя")
            .middleName("Отчество")
            .dulType(Renter.DulTypeEnum.PASSPORTOFRUSSIA)
            .dulName(DulType.PASSPORTOFRUSSIA.getDesc())
            .dulSerie("Серия")
            .dulNumber("Номер")
            .dulDivisionIssue("Место")
            .dulDateIssue(LocalDate.now())
            .dulDivisionCode("Код")
            .account("40702810538261023926")
            .bankBic("044525225")
            .bankName("ПАО СБЕРБАНК")
            .bankAccount("30101810400000000225")
            .phoneNumbers("+79991112233")
            .emails("roga@mail.ru")
            .legalAddress(address)
            .physicalAddress(address);
    }

    public static Renter getValidRenter() {
        String digitalId = randomAlphabetic(10);
        return getValidRenter(digitalId);
    }
}

package ru.sberbank.pprb.sbbol.partners;

import org.springframework.stereotype.Service;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterAddress;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterListResponse;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RenterServiceImpl implements RenterService {

    private final RenterDao renterDao;

    public RenterServiceImpl(RenterDao renterDao) {
        this.renterDao = renterDao;
    }

    private static List<String> phoneNumbers;
    private static List<String> emails;

    {
        phoneNumbers = new ArrayList<>();
        phoneNumbers.add("+79991112233");
        emails = new ArrayList<>();
        emails.add("roga@mail.ru");
    }

    @Override
    public RenterListResponse getRenters(@Nonnull RenterFilter renterFilter) {
        RenterAddress adress = new RenterAddress()
                .zipCode("655511")
                .regionCode("42")
                .region("Кемеровская область")
                .city("Кемерово")
                .locality("Кемерово")
                .street("Ленина")
                .building("162")
                .buildingBlock("1")
                .flat("55");
        RenterAddress adressPhysical = new RenterAddress()
                .zipCode("143000")
                .regionCode("50")
                .region("Московская область")
                .city("Москва")
                .locality("Москва")
                .street("Пушкина")
                .building("77")
                .buildingBlock(null)
                .flat("88");
        Renter renter = new Renter()
                .guid(UUID.randomUUID().toString())
                .type("legal_entity")
                .legalName("ОАО Рога и копыта")
                .inn("132456789132")
                .kpp("0")
                .ogrn("0")
                .okpo("0")
                .lastName(null)
                .firstName(null)
                .middleName(null)
                .dulType(null)
                .dulName(null)
                .dulSerie(null)
                .dulNumber(null)
                .dulDivisionIssue(null)
                .dulDateIssue(null)
                .dulDivisionCode(null)
                .account("40702810538261023926")
                .bankBic("044525225")
                .bankName("ПАО СБЕРБАНК")
                .bankAccount("30101810400000000225")
                .phoneNumbers(phoneNumbers)
                .emails(emails)
                .legalAddress(adress)
                .physicalAddress(adressPhysical);

        return new RenterListResponse().addItemsItem(renter);
    }

    @Override
    public Renter createRenter(@Nonnull Renter renter) {
        return renter;
    }

    @Override
    public Renter updateRenter(@Nonnull Renter renter) {
        return renter;
    }

    @Override
    public Renter getRenter(@Nonnull String renterGuid) {
        RenterAddress adress = new RenterAddress()
                .zipCode("655511")
                .regionCode("42")
                .region("Кемеровская область")
                .city("Кемерово")
                .locality("Кемерово")
                .street("Ленина")
                .building("162")
                .buildingBlock("1")
                .flat("55");
        RenterAddress adressPhysical = new RenterAddress()
                .zipCode("143000")
                .regionCode("50")
                .region("Московская область")
                .city("Москва")
                .locality("Москва")
                .street("Пушкина")
                .building("77")
                .buildingBlock(null)
                .flat("88");
        Renter renter = new Renter()
                .guid(UUID.randomUUID().toString())
                .type("legal_entity")
                .legalName("ОАО Рога и копыта")
                .inn("132456789132")
                .kpp("0")
                .ogrn("0")
                .okpo("0")
                .lastName(null)
                .firstName(null)
                .middleName(null)
                .dulType(null)
                .dulName(null)
                .dulSerie(null)
                .dulNumber(null)
                .dulDivisionIssue(null)
                .dulDateIssue(null)
                .dulDivisionCode(null)
                .account("40702810538261023926")
                .bankBic("044525225")
                .bankName("ПАО СБЕРБАНК")
                .bankAccount("30101810400000000225")
                .phoneNumbers(phoneNumbers)
                .emails(emails)
                .legalAddress(adress)
                .physicalAddress(adressPhysical);
        return renter;
    }
}

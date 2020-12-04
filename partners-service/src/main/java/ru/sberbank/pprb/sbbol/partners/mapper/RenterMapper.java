package ru.sberbank.pprb.sbbol.partners.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.sberbank.pprb.sbbol.partners.graph.get.AddressReferenceGet;
import ru.sberbank.pprb.sbbol.partners.graph.get.RenterGet;
import ru.sberbank.pprb.sbbol.partners.packet.CreateAddressParam;
import ru.sberbank.pprb.sbbol.partners.packet.CreateRenterParam;
import ru.sberbank.pprb.sbbol.partners.packet.UpdateRenterParam;
import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterAddress;

@Mapper(componentModel = "spring")
public interface RenterMapper {

    /**
     * Преобразовывает модель данных Renter DataSpace в модель данных Renter ППРБ
     */
    @Mapping(target = "type", source = "renterType")
    Renter renterToFront(RenterGet contract);

    @Mapping(target = "legalAddress", ignore = true)
    @Mapping(target = "physicalAddress", ignore = true)
    @Mapping(target = "renterType", source = "type")
    void createRenterParam(Renter contract, @MappingTarget CreateRenterParam createRenterParam);

    @Mapping(target = "legalAddress", ignore = true)
    @Mapping(target = "physicalAddress", ignore = true)
    @Mapping(target = "renterType", source = "type")
    void updateRenterParam(Renter contract, @MappingTarget UpdateRenterParam updateRenterParam);

    void createAddressParam(RenterAddress address, @MappingTarget CreateAddressParam createAddressParam);

    RenterAddress addressToFront(AddressReferenceGet address);

}

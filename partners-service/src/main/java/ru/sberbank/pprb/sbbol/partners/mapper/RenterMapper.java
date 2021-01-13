package ru.sberbank.pprb.sbbol.partners.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.sberbank.pprb.sbbol.partners.graph.get.LegalAddressGet;
import ru.sberbank.pprb.sbbol.partners.graph.get.PhysicalAddressGet;
import ru.sberbank.pprb.sbbol.partners.graph.get.RenterGet;
import ru.sberbank.pprb.sbbol.partners.packet.CreateLegalAddressParam;
import ru.sberbank.pprb.sbbol.partners.packet.CreatePhysicalAddressParam;
import ru.sberbank.pprb.sbbol.partners.packet.CreateRenterParam;
import ru.sberbank.pprb.sbbol.partners.packet.UpdateLegalAddressParam;
import ru.sberbank.pprb.sbbol.partners.packet.UpdatePhysicalAddressParam;
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

    @Mapping(target = "renterType", source = "type")
    void createRenterParam(Renter contract, @MappingTarget CreateRenterParam createRenterParam);

    @Mapping(target = "renterType", source = "type")
    void updateRenterParam(Renter contract, @MappingTarget UpdateRenterParam updateRenterParam);

    void createAddressParam(RenterAddress address, @MappingTarget CreateLegalAddressParam createAddressParam);
    void createAddressParam(RenterAddress address, @MappingTarget CreatePhysicalAddressParam createAddressParam);

    void updateAddressParam(RenterAddress address, @MappingTarget UpdateLegalAddressParam updateAddressParam);
    void updateAddressParam(RenterAddress address, @MappingTarget UpdatePhysicalAddressParam updateAddressParam);

    RenterAddress addressToFront(LegalAddressGet address);
    RenterAddress addressToFront(PhysicalAddressGet address);

}

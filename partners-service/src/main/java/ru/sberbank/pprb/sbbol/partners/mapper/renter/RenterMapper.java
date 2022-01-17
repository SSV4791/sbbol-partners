package ru.sberbank.pprb.sbbol.partners.mapper.renter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.renter.LegalAddress;
import ru.sberbank.pprb.sbbol.partners.entity.renter.PhysicalAddress;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterAddress;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RenterMapper {

    @Mapping(target = "renterType", source = "type")
    ru.sberbank.pprb.sbbol.partners.entity.renter.Renter toRenter(Renter renter);

    RenterAddress toRentalAddress(LegalAddress address);

    RenterAddress toRentalAddress(PhysicalAddress address);

    @Mapping(target = "legalAddress", source = "legalAddress", qualifiedByName = "updateLegalAddress")
    @Mapping(target = "physicalAddress", source = "physicalAddress", qualifiedByName = "updatePhysicalAddress")
    void updateRenter(Renter dtoRenter, @MappingTarget() ru.sberbank.pprb.sbbol.partners.entity.renter.Renter entityRenter);

    @Named("updateLegalAddress")
    void updateRentalAddress(RenterAddress dtoAddress, @MappingTarget() LegalAddress entityAddress);

    @Named("updatePhysicalAddress")
    void updateRentalAddress(RenterAddress dtoAddress, @MappingTarget() PhysicalAddress entityAddress);

    @Mapping(target = "type", source = "renterType")
    Renter toRenter(ru.sberbank.pprb.sbbol.partners.entity.renter.Renter contract);

    LegalAddress toLegalAddress(RenterAddress address);

    PhysicalAddress toPhysicalAddress(RenterAddress address);

}

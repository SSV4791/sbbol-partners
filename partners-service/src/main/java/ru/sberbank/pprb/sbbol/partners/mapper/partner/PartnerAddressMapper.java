package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PartnerAddressMapper extends BaseMapper {

    @Mapping(target = "uuid", expression = "java(address.getId() != null ? address.getId().toString() : null)")
    @Mapping(target = "unifiedUuid", expression = "java(address.getUnifiedUuid() != null ? address.getUnifiedUuid().toString() : null)")
    @Mapping(target = "type", source = "type", qualifiedByName = "toAddressType")
    Address toAddress(AddressEntity address);

    @Named("toAddressType")
    static Address.TypeEnum toAddressType(AddressType addressType) {
        return addressType != null ? Address.TypeEnum.valueOf(addressType.name()) : null;
    }

    @Mapping(target = "id", expression = "java(mapUuid(address.getUuid()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(address.getUnifiedUuid()))")
    @Mapping(target = "type", source = "type", qualifiedByName = "toAddressType")
    AddressEntity toAddress(Address address);

    @Named("toAddressType")
    static AddressType toAddressType(Address.TypeEnum addressType) {
        return addressType != null ? AddressType.valueOf(addressType.getValue()) : null;
    }

    @Named("updateAddress")
    @Mapping(target = "id", expression = "java(mapUuid(address.getUuid()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(address.getUnifiedUuid()))")
    void updateAddress(Address address, @MappingTarget() AddressEntity addressEntity);
}

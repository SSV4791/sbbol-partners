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
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(address.getUuid().toString())")
    @Mapping(target = "unifiedId", expression = "java(address.getUnifiedUuid().toString())")
    @Mapping(target = "type", source = "type", qualifiedByName = "toAddressType")
    Address toAddress(AddressEntity address);

    @Named("toAddressType")
    static ru.sberbank.pprb.sbbol.partners.model.AddressType toAddressType(AddressType addressType) {
        return addressType != null ? ru.sberbank.pprb.sbbol.partners.model.AddressType.valueOf(addressType.name()) : null;
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(address.getUnifiedId()))")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = "toAddressType")
    AddressEntity toAddress(AddressCreate address);

    @Named("toAddressType")
    static AddressType toAddressType(ru.sberbank.pprb.sbbol.partners.model.AddressType addressType) {
        return addressType != null ? AddressType.valueOf(addressType.getValue()) : null;
    }

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", expression = "java(mapUuid(address.getId()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(address.getUnifiedId()))")
    @Mapping(target = "type", source = "type", qualifiedByName = "toAddressType")
    AddressEntity toAddress(Address address);


    @Named("updateAddress")
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", expression = "java(mapUuid(address.getId()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(address.getUnifiedId()))")
    void updateAddress(Address address, @MappingTarget() AddressEntity addressEntity);
}

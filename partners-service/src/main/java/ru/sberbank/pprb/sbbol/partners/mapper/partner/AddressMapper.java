package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreateFullModel;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Loggable
@Mapper(uses = {BaseMapper.class})
public interface AddressMapper {

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "unifiedId", source = "unifiedUuid")
    @Mapping(target = "type", source = "type", qualifiedByName = "toAddressType")
    Address toAddress(AddressEntity address);

    @Named("toAddressType")
    static ru.sberbank.pprb.sbbol.partners.model.AddressType toAddressType(AddressType addressType) {
        return addressType != null ? ru.sberbank.pprb.sbbol.partners.model.AddressType.valueOf(addressType.name()) : null;
    }

    default List<AddressCreate> toAddress(Set<AddressCreateFullModel> addresses, String digitalId, UUID unifiedUuid) {
        if (CollectionUtils.isEmpty(addresses)) {
            return Collections.emptyList();
        }
        return addresses.stream()
            .map(value -> toAddress(value, digitalId, unifiedUuid))
            .collect(Collectors.toList());
    }

    @Mapping(target = "unifiedId", source = "unifiedUuid")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "type", source = "address.type")
    @Mapping(target = "zipCode", source = "address.zipCode")
    @Mapping(target = "regionCode", source = "address.regionCode")
    @Mapping(target = "region", source = "address.region")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "location", source = "address.location")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "building", source = "address.building")
    @Mapping(target = "buildingBlock", source = "address.buildingBlock")
    @Mapping(target = "flat", source = "address.flat")
    AddressCreate toAddress(AddressCreateFullModel address, String digitalId, UUID unifiedUuid);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedId")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = "toAddressType")
    AddressEntity toAddress(AddressCreate address);

    @Named("toAddressType")
    static AddressType toAddressType(ru.sberbank.pprb.sbbol.partners.model.AddressType addressType) {
        return addressType != null ? AddressType.valueOf(addressType.getValue()) : null;
    }

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "unifiedUuid", source = "unifiedId")
    @Mapping(target = "type", source = "type", qualifiedByName = "toAddressType")
    AddressEntity toAddress(Address address);

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedId")
    void updateAddress(Address address, @MappingTarget() AddressEntity addressEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedId")
    void patchAddress(Address address, @MappingTarget() AddressEntity addressEntity);

    Address toAddress(AddressChangeFullModel address, String digitalId, UUID unifiedId);

    AddressCreate toAddressCreate(AddressChangeFullModel address, String digitalId, UUID unifiedId);
}

package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PhoneMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(phone.getUuid() == null ? null : phone.getUuid().toString())")
    @Mapping(target = "unifiedId", expression = "java(phone.getHashKey())")
    Phone toPhone(PhoneEntity phone);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(phone.getUnifiedId()))")
    @Mapping(target = "version", ignore = true)
    PhoneEntity toPhone(PhoneCreate phone);

    @Mapping(target = "uuid", expression = "java(mapUuid(phone.getId()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(phone.getUnifiedId()))")
    PhoneEntity toPhone(Phone phone);

    @Mapping(target = "uuid", expression = "java(mapUuid(phone.getId()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(phone.getUnifiedId()))")
    void updatePhone(Phone phone, @MappingTarget() PhoneEntity foundPhone);
}
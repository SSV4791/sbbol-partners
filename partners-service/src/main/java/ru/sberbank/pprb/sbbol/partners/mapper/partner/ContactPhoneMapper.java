package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

@Loggable
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactPhoneMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(phone.getUuid() == null ? null : phone.getUuid().toString())")
    @Mapping(target = "unifiedId", expression = "java(phone.getHashKey())")
    Phone toPhone(ContactPhoneEntity phone);

    @Mapping(target = "uuid", expression = "java(mapUuid(phone.getId()))")
    @Mapping(target = "contact", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    ContactPhoneEntity toPhone(Phone phone);
}

package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactPhoneMapper extends BaseMapper {

    @Mapping(target = "uuid", expression = "java(phone.getId() != null ? phone.getId().toString() : null)")
    @Mapping(target = "unifiedUuid", expression = "java(phone.getHashKey() != null ? phone.getHashKey() : null)")
    Phone toPhone(ContactPhoneEntity phone);

    @Mapping(target = "id", expression = "java(mapUuid(phone.getUuid()))")
    @Mapping(target = "contact", ignore = true)
    ContactPhoneEntity toPhone(Phone phone);
}

package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactEmailMapper extends BaseMapper {

    @Mapping(target = "uuid", expression = "java(email.getId() != null ? email.getId().toString() : null)")
    @Mapping(target = "unifiedUuid", expression = "java(email.getHashKey() != null ? email.getHashKey() : null)")
    Email toEmail(ContactEmailEntity email);

    @Mapping(target = "id", expression = "java(mapUuid(email.getUuid()))")
    @Mapping(target = "contact", ignore = true)
    ContactEmailEntity toEmail(Email email);
}

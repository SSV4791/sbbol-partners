package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactEmailMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(email.getUuid() == null ? null : email.getUuid().toString())")
    @Mapping(target = "unifiedId", expression = "java(email.getHashKey())")
    Email toEmail(ContactEmailEntity email);

    @Mapping(target = "uuid", expression = "java(mapUuid(email.getId()))")
    @Mapping(target = "contact", ignore = true)
    ContactEmailEntity toEmail(Email email);
}

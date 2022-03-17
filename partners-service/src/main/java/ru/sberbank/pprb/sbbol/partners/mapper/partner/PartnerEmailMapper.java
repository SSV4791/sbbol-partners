package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PartnerEmailMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(email.getUuid() == null ? null : email.getUuid().toString())")
    @Mapping(target = "unifiedId", expression = "java(email.getHashKey())")
    Email toEmail(PartnerEmailEntity email);

    @Mapping(target = "uuid", expression = "java(mapUuid(email.getId()))")
    @Mapping(target = "partner", ignore = true)
    PartnerEmailEntity toEmail(Email email);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partner", ignore = true)
    PartnerEmailEntity toEmail(String email);
}

package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.EmailEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmailMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(email.getUuid() == null ? null : email.getUuid().toString())")
    @Mapping(target = "unifiedId", expression = "java(email.getHashKey())")
    Email toEmail(EmailEntity email);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(email.getUnifiedId()))")
    EmailEntity toEmail(EmailCreate email);

    @Mapping(target = "uuid", expression = "java(mapUuid(email.getId()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(email.getUnifiedId()))")
    EmailEntity toEmail(Email email);

    @Mapping(target = "uuid", expression = "java(mapUuid(email.getId()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(email.getUnifiedId()))")
    void updateEmail(Email email, @MappingTarget() EmailEntity foundEmail);
}

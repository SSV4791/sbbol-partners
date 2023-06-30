package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.EmailEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;

@Loggable
@Mapper(uses = {BaseMapper.class})
public interface EmailMapper {

    @Mapping(target = "id", source = "uuid", qualifiedByName = "mapUuid")
    @Mapping(target = "unifiedId", source = "unifiedUuid", qualifiedByName = "mapUuid")
    Email toEmail(EmailEntity email);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedId", qualifiedByName = "mapUuid")
    EmailEntity toEmail(EmailCreate email);

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", source = "id", qualifiedByName = "mapUuid")
    @Mapping(target = "unifiedUuid", source = "unifiedId", qualifiedByName = "mapUuid")
    EmailEntity toEmail(Email email);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedId", qualifiedByName = "mapUuid")
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateEmail(Email email, @MappingTarget() EmailEntity foundEmail);
}

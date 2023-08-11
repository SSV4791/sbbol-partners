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

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "unifiedId", source = "unifiedUuid")
    Email toEmail(EmailEntity email);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedId")
    EmailEntity toEmail(EmailCreate email);

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "unifiedUuid", source = "unifiedId")
    EmailEntity toEmail(Email email);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedId")
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateEmail(Email email, @MappingTarget() EmailEntity foundEmail);
}

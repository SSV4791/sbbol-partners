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
@Mapper
public interface EmailMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(email.getUuid() == null ? null : email.getUuid().toString())")
    @Mapping(target = "unifiedId",
        expression = "java(email.getUnifiedUuid() == null ? null : email.getUnifiedUuid().toString())")
    Email toEmail(EmailEntity email);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(email.getUnifiedId()))")
    EmailEntity toEmail(EmailCreate email);

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", expression = "java(mapUuid(email.getId()))")
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(email.getUnifiedId()))")
    EmailEntity toEmail(Email email);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", expression = "java(mapUuid(email.getUnifiedId()))")
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateEmail(Email email, @MappingTarget() EmailEntity foundEmail);
}

package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailChangeFullModel;

import java.util.Optional;

@Loggable
@Mapper
public interface ContactEmailMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(email.getUuid() == null ? null : email.getUuid().toString())")
    @Mapping(target = "unifiedId",
        expression = "java(email.getContact().getUuid() == null ? null : email.getContact().getUuid().toString())")
    Email toEmail(ContactEmailEntity email);

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", expression = "java(mapUuid(email.getId()))")
    @Mapping(target = "contact", ignore = true)
    ContactEmailEntity toEmail(Email email);

    Email toEmail(EmailChangeFullModel email, String digitalId, String unifiedId);

    default String toEmailStr(EmailChangeFullModel email) {
        return Optional.ofNullable(email).map(EmailChangeFullModel::getEmail).orElse(null);
    }
}

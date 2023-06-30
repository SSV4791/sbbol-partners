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
@Mapper(uses = {BaseMapper.class})
public interface ContactEmailMapper {

    @Mapping(target = "id", source = "uuid", qualifiedByName = "mapUuid")
    @Mapping(target = "unifiedId", source = "contact.uuid", qualifiedByName = "mapUuid")
    Email toEmail(ContactEmailEntity email);

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", source = "id", qualifiedByName = "mapUuid")
    @Mapping(target = "contact", ignore = true)
    ContactEmailEntity toEmail(Email email);

    Email toEmail(EmailChangeFullModel email, String digitalId, String unifiedId);

    default String toEmailStr(EmailChangeFullModel email) {
        return Optional.ofNullable(email).map(EmailChangeFullModel::getEmail).orElse(null);
    }
}

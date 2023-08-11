package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailChangeFullModel;

import java.util.Optional;
import java.util.UUID;

@Loggable
@Mapper(uses = {BaseMapper.class})
public interface ContactEmailMapper {

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "unifiedId", source = "contact.uuid")
    Email toEmail(ContactEmailEntity email);

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "contact", ignore = true)
    ContactEmailEntity toEmail(Email email);

    Email toEmail(EmailChangeFullModel email, String digitalId, UUID unifiedId);

    default String toEmailStr(EmailChangeFullModel email) {
        return Optional.ofNullable(email).map(EmailChangeFullModel::getEmail).orElse(null);
    }
}

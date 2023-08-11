package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailChangeFullModel;

@Loggable
@Mapper(uses = {BaseMapper.class})
public interface PartnerEmailMapper {

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "unifiedId", source = "partner.uuid")
    Email toEmail(PartnerEmailEntity email);

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "partner", ignore = true)
    PartnerEmailEntity toEmail(Email email);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "digitalId", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    PartnerEmailEntity toEmail(EmailChangeFullModel email);

    @Mapping(target = "id", source = "uuid")
    EmailChangeFullModel toEmailChangeFullModel(PartnerEmailEntity email);
}

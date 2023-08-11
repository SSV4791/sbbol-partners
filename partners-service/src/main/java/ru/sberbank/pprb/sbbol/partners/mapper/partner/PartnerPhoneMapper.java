package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneChangeFullModel;

@Loggable
@Mapper(uses = {BaseMapper.class})
public interface PartnerPhoneMapper {

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "unifiedId", source = "partner.uuid")
    Phone toPhone(PartnerPhoneEntity phone);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    PartnerPhoneEntity toPhone(Phone phone);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "digitalId", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    PartnerPhoneEntity toPhone(PhoneChangeFullModel phone);

    @Mapping(target = "id", source = "uuid")
    PhoneChangeFullModel toPhoneChangeFullModel(PartnerPhoneEntity phone);
}

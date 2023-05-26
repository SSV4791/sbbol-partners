package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneChangeFullModel;

@Loggable
@Mapper
public interface PartnerPhoneMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(phone.getUuid() == null ? null : phone.getUuid().toString())")
    @Mapping(target = "unifiedId",
        expression = "java(phone.getPartner().getUuid() == null ? null : phone.getPartner().getUuid().toString())")
    Phone toPhone(PartnerPhoneEntity phone);

    @Mapping(target = "uuid", expression = "java(mapUuid(phone.getId()))")
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    PartnerPhoneEntity toPhone(Phone phone);

    @Mapping(target = "uuid", expression = "java(mapUuid(phone.getId()))")
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    PartnerPhoneEntity toPhone(PhoneChangeFullModel phone);

    @Mapping(target = "id", expression = "java(phone.getUuid() == null ? null : phone.getUuid().toString())")
    PhoneChangeFullModel toPhoneChangeFullModel(PartnerPhoneEntity phone);
}

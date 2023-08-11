package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;

@Loggable
@Mapper(uses = {BaseMapper.class})
public interface PhoneMapper {

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "unifiedId", source = "unifiedUuid")
    Phone toPhone(PhoneEntity phone);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedId")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    PhoneEntity toPhone(PhoneCreate phone);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "unifiedUuid", source = "unifiedId")
    @Mapping(target = "lastModifiedDate", ignore = true)
    PhoneEntity toPhone(Phone phone);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "unifiedUuid", source = "unifiedId")
    void updatePhone(Phone phone, @MappingTarget PhoneEntity foundPhone);
}

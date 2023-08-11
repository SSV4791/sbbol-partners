package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneChangeFullModel;

import java.util.Optional;
import java.util.UUID;

@Loggable
@Mapper(uses = {BaseMapper.class})
public interface ContactPhoneMapper {

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "unifiedId", source = "contact.uuid")
    Phone toPhone(ContactPhoneEntity phone);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "contact", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    ContactPhoneEntity toPhone(Phone phone);

    Phone toPhone(PhoneChangeFullModel phone, String digitalId, UUID unifiedId);

    default String toPhoneStr(PhoneChangeFullModel phone) {
        return Optional.ofNullable(phone).map(PhoneChangeFullModel::getPhone).orElse(null);
    }
}

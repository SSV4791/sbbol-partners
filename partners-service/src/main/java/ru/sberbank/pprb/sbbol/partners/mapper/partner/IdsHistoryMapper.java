package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;

@Loggable
@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class}
)
public interface IdsHistoryMapper {

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "digitalId", source = "savedAccount.digitalId")
    @Mapping(target = "externalId", source = "externalId", qualifiedByName = "mapUuid")
    @Mapping(target = "pprbEntityId", source = "savedAccount.uuid")
    IdsHistoryEntity toIdsHistoryEntity(String externalId, AccountEntity savedAccount);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "digitalId", source = "savedPartner.digitalId")
    @Mapping(target = "externalId", source = "externalId", qualifiedByName = "mapUuid")
    @Mapping(target = "pprbEntityId", source = "savedPartner.uuid")
    IdsHistoryEntity toIdsHistoryEntity(String externalId, PartnerEntity savedPartner);
}

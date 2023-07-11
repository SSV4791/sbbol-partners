package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;

import java.util.UUID;

@Loggable
@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class}
)
public interface IdsHistoryMapper {

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "externalId", source = "externalId")
    @Mapping(target = "pprbEntityId", source = "pprbId")
    IdsHistoryEntity toIdsHistoryEntity(String digitalId, UUID externalId, UUID pprbId);

}

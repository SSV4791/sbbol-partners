package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLink;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Loggable
@Mapper(
    uses = {BaseMapper.class}
)
public interface IdsHistoryMapper {

    default ExternalInternalIdLinksResponse toExternalInternalIdsResponse(List<UUID> externalIds, List<IdsHistoryEntity> idsHistoryEntities) {
        if (isEmpty(externalIds)) {
            return null;
        }
        Map<UUID, UUID> pprbIdByExternalIdMap = new HashMap<>();
        if (!isEmpty(idsHistoryEntities)) {
            pprbIdByExternalIdMap = idsHistoryEntities.stream()
                .collect(Collectors.toMap(
                    IdsHistoryEntity::getExternalId,
                    IdsHistoryEntity::getPprbEntityId)
                );
        }
        List<ExternalInternalIdLink> idLinks = new ArrayList<>(idsHistoryEntities.size());
        for (var externalId : externalIds) {
            idLinks.add(
                new ExternalInternalIdLink()
                    .externalId(externalId)
                    .internalId(pprbIdByExternalIdMap.get(externalId))
            );
        }
        return new ExternalInternalIdLinksResponse()
            .idLinks(idLinks);
    }

    @Mapping(target = "externalId", source = "externalId")
    @Mapping(target = "parentType", constant = "ACCOUNT")
    @Mapping(target = "pprbEntityId", source = "internalId")
    IdsHistoryEntity toAccountIdsHistoryEntity(UUID externalId, UUID internalId, String digitalId);

    default List<UUID> toExternalUuids(List<IdsHistoryEntity> history) {
        if (CollectionUtils.isEmpty(history)) {
            return Collections.emptyList();
        }
        return history.stream()
            .map(IdsHistoryEntity::getExternalId)
            .collect(Collectors.toList());
    }
}

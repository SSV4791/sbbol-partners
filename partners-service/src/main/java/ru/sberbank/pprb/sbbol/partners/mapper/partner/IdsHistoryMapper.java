package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLink;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Loggable
@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class}
)
public interface IdsHistoryMapper {

    default ExternalInternalIdLinksResponse toAccountIdsByExternalIdsResponse(List<UUID> externalIds, List<IdsHistoryEntity> idsHistoryEntities) {
        if (isEmpty(externalIds)) {
            return null;
        }
        Map<UUID, UUID> pprbIdByExternalIdMap = new HashMap<>();
        if (!isEmpty(idsHistoryEntities)) {
            pprbIdByExternalIdMap = idsHistoryEntities.stream()
                .collect(Collectors.toMap(
                    it -> it.getExternalId(),
                    it -> it.getAccount().getUuid())
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
}

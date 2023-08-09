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
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Loggable
@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class}
)
public interface IdsHistoryMapper {

    default ExternalInternalIdLinksResponse toAccountIdsByExternalIdsResponse(List<String> externalIds, List<IdsHistoryEntity> idsHistoryEntities) {
        if (isEmpty(externalIds)) {
            return null;
        }
        Map<String, String> pprbIdByExternalIdMap = new HashMap<>();
        if (!isEmpty(idsHistoryEntities)) {
            pprbIdByExternalIdMap = idsHistoryEntities.stream()
                .collect(Collectors.toMap(
                    it -> it.getExternalId().toString(),
                    it -> it.getAccount().getUuid().toString())
                );
        }
        List<ExternalInternalIdLink> idLinks = new ArrayList<>(idsHistoryEntities.size());
        for (String externalId : externalIds) {
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

package ru.sberbank.pprb.sbbol.partners.service.ids.history.impl;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GuidsHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Loggable
public class IdsHistoryServiceImpl implements IdsHistoryService {

    private final GuidsHistoryRepository repository;
    private final IdsHistoryMapper mapper;

    public IdsHistoryServiceImpl(GuidsHistoryRepository repository, IdsHistoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ExternalInternalIdLinksResponse getInternalIds(@NotEmpty String digitalId, @NotEmpty List<String> externalIds) {
        var externalUuids = externalIds.stream()
            .map(BaseMapper::mapUuid)
            .collect(Collectors.toList());
        var idsHistoryEntities =
            repository.findByDigitalIdAndExternalIdIn(digitalId, externalUuids);
        return mapper.toAccountIdsByExternalIdsResponse(externalIds, idsHistoryEntities);
    }
}

package ru.sberbank.pprb.sbbol.partners.service.ids.history.impl;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.ParentType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapper;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GuidsHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Loggable
public class IdsHistoryServiceImpl implements IdsHistoryService {

    private final GuidsHistoryRepository repository;
    private final IdsHistoryMapper mapper;

    public IdsHistoryServiceImpl(GuidsHistoryRepository repository, IdsHistoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ExternalInternalIdLinksResponse getAccountInternalIds(@NotEmpty String digitalId, @NotEmpty List<UUID> externalIds) {
        var idsHistoryEntities =
            repository.findByDigitalIdAndParentTypeAndExternalIdIn(digitalId, ParentType.ACCOUNT, externalIds);
        return mapper.toAccountIdsByExternalIdsResponse(externalIds, idsHistoryEntities);
    }
}

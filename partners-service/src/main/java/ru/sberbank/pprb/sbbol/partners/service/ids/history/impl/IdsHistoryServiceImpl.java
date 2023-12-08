package ru.sberbank.pprb.sbbol.partners.service.ids.history.impl;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
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
    @Transactional(readOnly = true)
    public List<UUID> getAccountByPprbUuid(@NotEmpty String digitalId, @NotEmpty UUID pprbUuid) {
        List<IdsHistoryEntity> history = repository.findByDigitalIdAndParentTypeAndPprbEntityId(digitalId, ParentType.ACCOUNT, pprbUuid);
        return mapper.toExternalUuids(history);
    }

    @Override
    @Transactional(readOnly = true)
    public ExternalInternalIdLinksResponse getAccountsInternalIds(@NotEmpty String digitalId, @NotEmpty List<UUID> externalIds) {
        var foundIdsHistory =
            repository.findByDigitalIdAndParentTypeAndExternalIdIn(digitalId, ParentType.ACCOUNT, externalIds);
        return mapper.toExternalInternalIdsResponse(externalIds, foundIdsHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public ExternalInternalIdLinksResponse getPartnersInternalId(@NotEmpty String digitalId, @NotEmpty List<UUID> externalIds) {
        var foundIdsHistory =
            repository.findByDigitalIdAndParentTypeAndExternalIdIn(digitalId, ParentType.PARTNER, externalIds);
        return mapper.toExternalInternalIdsResponse(externalIds, foundIdsHistory);
    }

    @Override
    @Transactional
    public IdsHistoryEntity saveAccountIdLink(UUID externalId, UUID internalId, String digitalId) {
        IdsHistoryEntity history = mapper.toAccountIdsHistoryEntity(externalId, internalId, digitalId);
        return repository.save(history);
    }
}

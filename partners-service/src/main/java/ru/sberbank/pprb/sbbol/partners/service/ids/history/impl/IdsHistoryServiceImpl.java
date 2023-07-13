package ru.sberbank.pprb.sbbol.partners.service.ids.history.impl;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.exception.BadRequestIdsHistoryException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapper;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.IdsHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Loggable
public class IdsHistoryServiceImpl implements IdsHistoryService {

    private static final String IDS_HISTORY_EXCEPTION_MESSAGE_EMPTY_PARAMS = "Отсутствует обязательный параметр. ";
    private static final String IDS_HISTORY_EXCEPTION_MESSAGE_BAD_FORMAT_UUID = "Не верный формат UUID. ";
    private static final String DIGITAL_ID = "digitalId: ";
    private static final String EXTERNAL_ID = "externalId: ";
    private static final String PPRB_ID = "pprbId: ";

    private final IdsHistoryRepository repository;
    private final IdsHistoryMapper mapper;

    public IdsHistoryServiceImpl(IdsHistoryRepository repository, IdsHistoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ExternalInternalIdLinksResponse getInternalIds(String digitalId, List<String> externalIds) {
        if (isBlank(digitalId) || isEmpty(externalIds)) {
            throw new BadRequestIdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE_EMPTY_PARAMS, DIGITAL_ID, digitalId, EXTERNAL_ID, externalIds)
            );
        }
        if (!checkUUIDs(externalIds)) {
            throw new BadRequestIdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE_BAD_FORMAT_UUID, EXTERNAL_ID, externalIds)
            );
        }
        var externalUuids = externalIds.stream()
            .map(UUID::fromString)
            .collect(Collectors.toList());
        var idsHistoryEntities =
            repository.findByDigitalIdAndExternalIdIn(digitalId, externalUuids);
        return mapper.toAccountIdsByExternalIdsResponse(externalIds, idsHistoryEntities);
    }

    private boolean checkUUIDs(List<String> externalIds) {
        try {
            externalIds.forEach(UUID::fromString);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public void add(String digitalId, UUID externalId, UUID pprbId) {
        if (isBlank(digitalId) || isNull(externalId) || isNull(pprbId)) {
            throw new BadRequestIdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE_EMPTY_PARAMS, DIGITAL_ID, digitalId, EXTERNAL_ID, externalId, PPRB_ID, pprbId)
            );
        }
        var foundEntity =
            repository.findByDigitalIdAndExternalId(digitalId, externalId);
        if (foundEntity.isEmpty()) {
            repository.save(mapper.toIdsHistoryEntity(digitalId, externalId, pprbId));
        }
    }

    @Override
    public void delete(String digitalId, UUID pprbId) {
        if (isBlank(digitalId) || isNull(pprbId)) {
            throw new BadRequestIdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE_EMPTY_PARAMS, DIGITAL_ID, digitalId, PPRB_ID, pprbId)
            );
        }
        var idsHistoryEntities =
            repository.findByDigitalIdAndPprbEntityId(digitalId, pprbId);
        if (!isEmpty(idsHistoryEntities)) {
            repository.deleteAll(idsHistoryEntities);
        }
    }

    @Override
    public void delete(String digitalId, List<UUID> pprbIds) {
        if (isBlank(digitalId) || isEmpty(pprbIds)) {
            throw new BadRequestIdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE_EMPTY_PARAMS, DIGITAL_ID, digitalId, PPRB_ID, pprbIds)
            );
        }
        for (var pprbId : pprbIds) {
            delete(digitalId, pprbId);
        }

    }
}

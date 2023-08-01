package ru.sberbank.pprb.sbbol.partners.service.ids.history.impl;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.exception.BadRequestIdsHistoryException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapper;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GuidsHistoryRepository;
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
    private static final String PPRB_UUID = "pprbUuid: ";

    private final GuidsHistoryRepository repository;
    private final IdsHistoryMapper mapper;

    public IdsHistoryServiceImpl(GuidsHistoryRepository repository, IdsHistoryMapper mapper) {
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
    public void create(String digitalId, UUID externalUuid, UUID pprbUuid) {
        if (isBlank(digitalId) || isNull(externalUuid) || isNull(pprbUuid)) {
            throw new BadRequestIdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE_EMPTY_PARAMS, DIGITAL_ID, digitalId, EXTERNAL_ID, externalUuid, PPRB_UUID, pprbUuid)
            );
        }
        var foundEntity =
            repository.findByDigitalIdAndExternalId(digitalId, externalUuid);
        if (foundEntity.isEmpty()) {
            repository.save(mapper.toIdsHistoryEntity(digitalId, externalUuid, pprbUuid));
        }
    }

    @Override
    public void delete(String digitalId, UUID pprbUuid) {
        if (isBlank(digitalId) || isNull(pprbUuid)) {
            throw new BadRequestIdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE_EMPTY_PARAMS, DIGITAL_ID, digitalId, PPRB_UUID, pprbUuid)
            );
        }
        var uuidsHistoryEntities =
            repository.findByDigitalIdAndPprbEntityId(digitalId, pprbUuid);
        if (!isEmpty(uuidsHistoryEntities)) {
            repository.deleteAll(uuidsHistoryEntities);
        }
    }

    @Override
    public void delete(String digitalId, List<UUID> pprbUuids) {
        if (isBlank(digitalId) || isEmpty(pprbUuids)) {
            throw new BadRequestIdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE_EMPTY_PARAMS, DIGITAL_ID, digitalId, PPRB_UUID, pprbUuids)
            );
        }
        for (var pprbUuid : pprbUuids) {
            delete(digitalId, pprbUuid);
        }

    }
}

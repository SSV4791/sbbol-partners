package ru.sberbank.pprb.sbbol.partners.service.ids.history.impl;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.exception.IdsHistoryException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapper;
import ru.sberbank.pprb.sbbol.partners.repository.partner.IdsHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Loggable
public class IdsHistoryServiceImpl implements IdsHistoryService {

    private static final String IDS_HISTORY_EXCEPTION_MESSAGE = "Отсутствует обязательный параметр. ";
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
    public void add(String digitalId, UUID externalId, UUID pprbId) {
        if (isBlank(digitalId) || isNull(externalId) || isNull(pprbId)) {
            throw new IdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE, DIGITAL_ID, digitalId, EXTERNAL_ID, externalId, PPRB_ID, pprbId)
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
            throw new IdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE, DIGITAL_ID, digitalId, PPRB_ID, pprbId)
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
            throw new IdsHistoryException(
                StringUtils.join(IDS_HISTORY_EXCEPTION_MESSAGE, DIGITAL_ID, digitalId, PPRB_ID, pprbIds)
            );
        }
        for (var pprbId : pprbIds) {
            delete(digitalId, pprbId);
        }

    }
}

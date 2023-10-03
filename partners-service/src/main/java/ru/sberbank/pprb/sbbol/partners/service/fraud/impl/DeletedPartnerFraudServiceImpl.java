package ru.sberbank.pprb.sbbol.partners.service.fraud.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.exception.FraudModelValidationException;
import ru.sberbank.pprb.sbbol.partners.fraud.FraudAdapter;
import ru.sberbank.pprb.sbbol.partners.fraud.config.FraudProperties;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudAdapterException;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudApplicationException;
import ru.sberbank.pprb.sbbol.partners.fraud.exception.FraudModelArgumentException;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.DeletedPartnerFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudService;

import static java.util.Objects.isNull;

@Loggable
public class DeletedPartnerFraudServiceImpl implements FraudService<PartnerEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(DeletedPartnerFraudServiceImpl.class);

    protected final FraudProperties properties;

    protected final FraudAdapter adapter;

    protected final DeletedPartnerFraudMetaDataMapper fraudMapper;

    public DeletedPartnerFraudServiceImpl(
        FraudProperties properties,
        FraudAdapter adapter,
        DeletedPartnerFraudMetaDataMapper fraudMapper
    ) {
        this.properties = properties;
        this.adapter = adapter;
        this.fraudMapper = fraudMapper;
    }

    @Override
    public FraudEventType getEventType() {
        return FraudEventType.DELETE_PARTNER;
    }

    @Override
    public void sendEvent(FraudMetaData metaData, PartnerEntity partnerEntity) {
        if (!properties.isEnabled() || isNull(metaData) || !checkEvent(metaData)) {
            return;
        }
        var fraudRequest = fraudMapper.mapToAnalyzeRequest(metaData, partnerEntity);
        try {
            LOG.debug("Отправляем запрос В АС Агрегатор данных ФРОД-мониторинг: {}", fraudRequest);
            adapter.send(fraudRequest);
            LOG.debug("Получили ответ от АС Агрегатора данных ФРОД-мониторинг: {}", fraudRequest);
        } catch (FraudModelArgumentException e) {
            throw new FraudModelValidationException(e.getMessage(), e);
        } catch (FraudAdapterException | FraudApplicationException e) {
            LOG.error("Ошибка адаптера АС Интегратор с ФМ ЮЛ: {}", e.getLocalizedMessage());
        }
    }
}

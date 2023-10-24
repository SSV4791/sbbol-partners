package ru.sberbank.pprb.sbbol.partners.service.fraud;

import ru.sberbank.pprb.sbbol.partners.entity.partner.BaseEntity;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

public interface FraudService<T extends BaseEntity> {

    FraudEventType getEventType();

    void sendEvent(FraudMetaData metaData, T entity);
}

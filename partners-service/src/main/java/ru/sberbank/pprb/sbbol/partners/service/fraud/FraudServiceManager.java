package ru.sberbank.pprb.sbbol.partners.service.fraud;

import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

public interface FraudServiceManager {

    FraudService getService(FraudEventType eventType);
}

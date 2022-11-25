package ru.sberbank.pprb.sbbol.partners.service.fraud.impl;

import ru.sberbank.pprb.sbbol.partners.exception.NotSupportedFraudEventTypeException;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudService;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudServiceManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FraudServiceManagerImpl implements FraudServiceManager {

    private final Map<FraudEventType, FraudService> services;

    public FraudServiceManagerImpl(List<FraudService> services) {
        this.services = services.stream()
            .collect(Collectors.toMap(FraudService::getEventType, it -> it));
    }

    @Override
    public FraudService getService(FraudEventType eventType) {
        return Optional.ofNullable(services.get(eventType))
            .orElseThrow(() -> new NotSupportedFraudEventTypeException(eventType));
    }
}

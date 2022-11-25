package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

public class NotSupportedFraudEventTypeException extends RuntimeException{

    public NotSupportedFraudEventTypeException(FraudEventType eventType) {
        super(MessagesTranslator.toLocale("fraud.event_type.not_supported", eventType.name()));
    }
}

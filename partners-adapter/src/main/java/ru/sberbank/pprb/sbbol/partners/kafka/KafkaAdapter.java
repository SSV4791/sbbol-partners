package ru.sberbank.pprb.sbbol.partners.kafka;

import ru.sberbank.pprb.sbbol.partners.exception.KafkaProducerException;
import ru.sberbank.pprb.sbbol.partners.model.kafka.BasePartnerEvent;

public interface KafkaAdapter {

    <T extends BasePartnerEvent> void sendAsync(T event);

    <T extends BasePartnerEvent> boolean sendSync(T event) throws KafkaProducerException;

}

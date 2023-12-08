package ru.sberbank.pprb.sbbol.partners.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import ru.sberbank.pprb.sbbol.partners.exception.KafkaProducerException;
import ru.sberbank.pprb.sbbol.partners.model.kafka.BasePartnerEvent;

import java.util.Objects;

public class KafkaAdapterImpl implements KafkaAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdapterImpl.class);

    @Value("${app.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, BasePartnerEvent> kafkaTemplate;

    public KafkaAdapterImpl(KafkaTemplate<String, BasePartnerEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public <T extends BasePartnerEvent> void sendAsync(T event) {
        LOG.debug("Отправляем сообщение {} в топик {} кафки", event, topic);
        ListenableFuture<SendResult<String, BasePartnerEvent>> future = kafkaTemplate.send(topic, event.getDigitalId(), event);
        future.addCallback(new ProcessMessageCallback<>(event));
    }

    @Override
    public <T extends BasePartnerEvent> boolean sendSync(T event) throws KafkaProducerException {
        try {
            LOG.debug("Отправляем сообщение {} в топик {} кафки...", event, topic);
            ListenableFuture<SendResult<String, BasePartnerEvent>> future = kafkaTemplate.send(topic, event.getDigitalId(), event);
            var result = Objects.nonNull(future.get());
            LOG.debug("Сообщение {} успешно отправлено", event);
            return result;
        } catch (Exception ex) {
            throw new KafkaProducerException(event.toString(), ex);
        }
    }
}

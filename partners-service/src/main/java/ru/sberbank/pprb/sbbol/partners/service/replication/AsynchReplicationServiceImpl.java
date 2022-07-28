package ru.sberbank.pprb.sbbol.partners.service.replication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.config.props.ReplicationKafkaProducerProperties;
import ru.sberbank.pprb.sbbol.partners.exception.ReplicationJsonProcessingException;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.AsynchReplicationCounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.service.replication.dto.AsynchReplicationCounterparty;

import static java.lang.String.format;

@Loggable
public class AsynchReplicationServiceImpl implements AsynchReplicationService{

    private final ReplicationKafkaProducerProperties kafkaProperties;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AsynchReplicationCounterpartyMapper asynchReplicationCounterpartyMapper;
    private final ObjectMapper objectMapper;

    public AsynchReplicationServiceImpl(
        ReplicationKafkaProducerProperties kafkaProperties,
        KafkaTemplate<String, String> kafkaTemplate,
        AsynchReplicationCounterpartyMapper asynchReplicationCounterpartyMapper,
        ObjectMapper objectMapper
    ) {
        this.kafkaProperties = kafkaProperties;
        this.kafkaTemplate = kafkaTemplate;
        this.asynchReplicationCounterpartyMapper = asynchReplicationCounterpartyMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isEnable() {
        return kafkaProperties.isEnable();
    }

    @Override
    public void createCounterparty(String digitalId, Counterparty counterparty) {
        if (isEnable()) {
            var replicationCounterparty = asynchReplicationCounterpartyMapper.mapToAsynchReplicationCounterparty(
                AsynchReplicationCounterparty.Operation.CREATE_COUNTERPARTY,
                digitalId,
                counterparty,
                null);
            sendMessage(kafkaProperties.getTopic(), replicationCounterparty);
        }
    }

    @Override
    public void updateCounterparty(String digitalId, Counterparty counterparty) {
        if (isEnable()) {
            var replicationCounterparty = asynchReplicationCounterpartyMapper.mapToAsynchReplicationCounterparty(
                AsynchReplicationCounterparty.Operation.UPDATE_COUNTERPARTY,
                digitalId,
                counterparty,
                null);
            sendMessage(kafkaProperties.getTopic(), replicationCounterparty);
        }
    }

    @Override
    public void deleteCounterparty(String digitalId, String counterpartyId) {
        if (isEnable()) {
            var replicationCounterparty = asynchReplicationCounterpartyMapper.mapToAsynchReplicationCounterparty(
                AsynchReplicationCounterparty.Operation.DELETE_COUNTERPARTY,
                digitalId,
                counterpartyId);
            sendMessage(kafkaProperties.getTopic(), replicationCounterparty);
        }
    }

    @Override
    public void createSign(String digitalId, CounterpartySignData signData) {
        if (isEnable()) {
            var replicationCounterparty = asynchReplicationCounterpartyMapper.mapToAsynchReplicationCounterparty(
                AsynchReplicationCounterparty.Operation.CREATE_SIGN,
                digitalId,
                null,
                signData);
            sendMessage(kafkaProperties.getTopic(), replicationCounterparty);
        }
    }

    @Override
    public void deleteSign(String digitalId, String counterpartyId) {
        if (isEnable()) {
            var replicationCounterparty = asynchReplicationCounterpartyMapper.mapToAsynchReplicationCounterparty(
                AsynchReplicationCounterparty.Operation.DELETE_SIGN,
                digitalId,
                counterpartyId);
            sendMessage(kafkaProperties.getTopic(), replicationCounterparty);
        }
    }

    private void sendMessage(String topic, AsynchReplicationCounterparty replicationCounterparty) {
        try {
            var message = objectMapper.writeValueAsString(replicationCounterparty);
            kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException ex) {
            throw new ReplicationJsonProcessingException(
                format("Ошибка при сериализации в Json объекта: [%s]", replicationCounterparty), ex);
        }
    }
}

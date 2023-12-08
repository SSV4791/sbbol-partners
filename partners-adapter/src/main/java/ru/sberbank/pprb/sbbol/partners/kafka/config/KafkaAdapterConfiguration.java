package ru.sberbank.pprb.sbbol.partners.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.sberbank.pprb.sbbol.partners.kafka.KafkaAdapter;
import ru.sberbank.pprb.sbbol.partners.kafka.KafkaAdapterImpl;
import ru.sberbank.pprb.sbbol.partners.model.kafka.BasePartnerEvent;

import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@ConditionalOnProperty(prefix = "kafka", name = "enable", havingValue = "true")
@Configuration
@EnableConfigurationProperties(KafkaAdapterProperties.class)
public class KafkaAdapterConfiguration {

    private final KafkaAdapterProperties kafkaAdapterProperties;

    public KafkaAdapterConfiguration(KafkaAdapterProperties kafkaAdapterProperties) {
        this.kafkaAdapterProperties = kafkaAdapterProperties;
    }

    @Bean
    public ProducerFactory<String, BasePartnerEvent> kafkaProducerFactory() {
        DefaultKafkaProducerFactory<String, BasePartnerEvent> producerFactory =
            new DefaultKafkaProducerFactory<>(getKafkaProducerConfigs());
        ObjectMapper objectMapper = JacksonUtils.enhancedObjectMapper();
        objectMapper.setSerializationInclusion(NON_NULL);
        JsonSerializer<BasePartnerEvent> valueSerializer = new JsonSerializer<>(objectMapper);
        valueSerializer.setAddTypeInfo(true);
        producerFactory.setValueSerializer(valueSerializer);
        return producerFactory;
    }

    @Bean
    public KafkaTemplate<String, BasePartnerEvent> kafkaTemplate(ProducerFactory<String, BasePartnerEvent> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @Bean
    KafkaAdapter kafkaAdapter(KafkaTemplate<String, BasePartnerEvent> kafkaTemplate) {
        return new KafkaAdapterImpl(kafkaTemplate);
    }

    private Map<String, Object> getKafkaProducerConfigs() {
        return new HashMap<>(kafkaAdapterProperties.buildProducerProperties());
    }
}

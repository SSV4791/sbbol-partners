package ru.sberbank.pprb.sbbol.partners.kafka.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaAdapterProperties extends KafkaProperties {
}

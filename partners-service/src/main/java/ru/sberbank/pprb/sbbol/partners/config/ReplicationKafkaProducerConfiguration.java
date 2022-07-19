package ru.sberbank.pprb.sbbol.partners.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.sberbank.pprb.sbbol.partners.config.props.ReplicationKafkaProducerProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Configuration
@EnableConfigurationProperties(ReplicationKafkaProducerProperties.class)
public class ReplicationKafkaProducerConfiguration {

    private static final String SECURITY_PROTOCOL = "security.protocol";
    private static final String SECURITY_SSL_PROTOCOL = "ssl.protocol";
    private static final String SECURITY_SSL_ENABLED_PROTOCOLS = "ssl.enabled.protocols";
    private static final String SECURITY_SSL_KEYSTORE_LOCATION = "ssl.keystore.location";
    private static final String SECURITY_SSL_TRUSTSTORE_LOCATION = "ssl.truststore.location";
    private static final String SECURITY_SSL_KEYSTORE_TYPE = "ssl.keystore.type";
    private static final String SECURITY_SSL_TRUSTSTORE_TYPE = "ssl.truststore.type";

    private static final String INTERCEPTOR_VALIDATOR_CONFIG = "interceptor.validator.config";

    private final ReplicationKafkaProducerProperties properties;

    public ReplicationKafkaProducerConfiguration(ReplicationKafkaProducerProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, properties.getProducerId());

        var securityProperties = properties.getSecurityProperties();
        if (nonNull(securityProperties) && securityProperties.isEnable()) {
            props.put(SECURITY_PROTOCOL, securityProperties.getProtocol());
            props.put(SECURITY_SSL_PROTOCOL, securityProperties.getSslProtocol());
            props.put(SECURITY_SSL_ENABLED_PROTOCOLS, securityProperties.getSslEnabledProtocols());
            props.put(SECURITY_SSL_KEYSTORE_LOCATION, securityProperties.getSslKeystoreLocation());
            props.put(SECURITY_SSL_TRUSTSTORE_LOCATION, securityProperties.getSslTruststoreLocation());
            props.put(SECURITY_SSL_KEYSTORE_TYPE, securityProperties.getSslKeystoreType());
            props.put(SECURITY_SSL_TRUSTSTORE_TYPE, securityProperties.getSslTruststoreType());
        }

        List<String> interceptorClasses = new ArrayList<>();

        var validationInterceptorProperties = properties.getValidationInterceptorProperties();
        if (nonNull(validationInterceptorProperties) && validationInterceptorProperties.isEnable()) {
            interceptorClasses.addAll(validationInterceptorProperties.getClasses());
            props.put(INTERCEPTOR_VALIDATOR_CONFIG, validationInterceptorProperties.getConfig());
        }

        if (!isEmpty(interceptorClasses)) {
            props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptorClasses);
        }

        return props;
    }

    @Bean
    ProducerFactory<String, String> kafkaPoducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(kafkaPoducerFactory());
    }
}

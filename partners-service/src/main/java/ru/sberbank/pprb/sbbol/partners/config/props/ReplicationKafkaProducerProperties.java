package ru.sberbank.pprb.sbbol.partners.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@EnableConfigurationProperties({
    ReplicationKafkaSecurityProperties.class,
    ValidationInterceptorProperties.class
})
@ConfigurationProperties(prefix = "replication.kafka.producer")
public class ReplicationKafkaProducerProperties {

    private boolean enable;
    private String server;
    private String producerId;
    private String topic;

    private final ValidationInterceptorProperties validationInterceptorProperties;
    private final ReplicationKafkaSecurityProperties securityProperties;

    public ReplicationKafkaProducerProperties(
        ValidationInterceptorProperties validationInterceptorProperties,
        ReplicationKafkaSecurityProperties securityProperties
    ) {
        this.validationInterceptorProperties = validationInterceptorProperties;
        this.securityProperties = securityProperties;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ValidationInterceptorProperties getValidationInterceptorProperties() {
        return validationInterceptorProperties;
    }

    public ReplicationKafkaSecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        ReplicationKafkaProducerProperties that = (ReplicationKafkaProducerProperties) o;
        return enable == that.enable &&
            Objects.equals(server, that.server) &&
            Objects.equals(producerId, that.producerId) &&
            Objects.equals(topic, that.topic) &&
            Objects.equals(validationInterceptorProperties, that.validationInterceptorProperties) &&
            Objects.equals(securityProperties, that.securityProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enable, server, producerId, topic, validationInterceptorProperties, securityProperties);
    }
}

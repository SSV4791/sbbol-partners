package ru.sberbank.pprb.sbbol.partners.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@ConfigurationProperties(prefix = "replication.kafka.producer.security")
public class ReplicationKafkaSecurityProperties {

    private boolean enable;
    private String protocol;
    private String sslProtocol;
    private String sslEnabledProtocols;
    private String sslKeystoreLocation;
    private String sslTruststoreLocation;
    private String sslKeystoreType;
    private String sslTruststoreType;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSslKeystoreLocation() {
        return sslKeystoreLocation;
    }

    public void setSslKeystoreLocation(String sslKeystoreLocation) {
        this.sslKeystoreLocation = sslKeystoreLocation;
    }

    public String getSslTruststoreLocation() {
        return sslTruststoreLocation;
    }

    public void setSslTruststoreLocation(String sslTruststoreLocation) {
        this.sslTruststoreLocation = sslTruststoreLocation;
    }

    public String getSslKeystoreType() {
        return sslKeystoreType;
    }

    public void setSslKeystoreType(String sslKeystoreType) {
        this.sslKeystoreType = sslKeystoreType;
    }

    public String getSslTruststoreType() {
        return sslTruststoreType;
    }

    public void setSslTruststoreType(String sslTruststoreType) {
        this.sslTruststoreType = sslTruststoreType;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public String getSslEnabledProtocols() {
        return sslEnabledProtocols;
    }

    public void setSslEnabledProtocols(String sslEnabledProtocols) {
        this.sslEnabledProtocols = sslEnabledProtocols;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplicationKafkaSecurityProperties)) return false;
        ReplicationKafkaSecurityProperties that = (ReplicationKafkaSecurityProperties) o;
        return enable == that.enable &&
            Objects.equals(protocol, that.protocol) &&
            Objects.equals(sslProtocol, that.sslProtocol) &&
            Objects.equals(sslEnabledProtocols, that.sslEnabledProtocols) &&
            Objects.equals(sslKeystoreLocation, that.sslKeystoreLocation) &&
            Objects.equals(sslTruststoreLocation, that.sslTruststoreLocation) &&
            Objects.equals(sslKeystoreType, that.sslKeystoreType) &&
            Objects.equals(sslTruststoreType, that.sslTruststoreType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enable, protocol, sslProtocol, sslEnabledProtocols, sslKeystoreLocation, sslTruststoreLocation, sslKeystoreType, sslTruststoreType);
    }
}

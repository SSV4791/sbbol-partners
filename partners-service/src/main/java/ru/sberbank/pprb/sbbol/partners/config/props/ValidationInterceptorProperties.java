package ru.sberbank.pprb.sbbol.partners.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Objects;

@ConfigurationProperties(prefix = "replication.kafka.producer.interceptor.validator")
public class ValidationInterceptorProperties {

    private boolean enable;
    private List<String> classes;
    private String config;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationInterceptorProperties)) return false;
        ValidationInterceptorProperties that = (ValidationInterceptorProperties) o;
        return enable == that.enable &&
            Objects.equals(classes, that.classes) &&
            Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enable, classes, config);
    }
}

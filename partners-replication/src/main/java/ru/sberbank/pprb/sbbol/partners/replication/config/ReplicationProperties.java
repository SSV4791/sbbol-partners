package ru.sberbank.pprb.sbbol.partners.replication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "replication.sbbol")
public class ReplicationProperties {

    private boolean enable;

    private String cron;

    private int batchSize;

    private int retry;

    private ReplicationCleaner cleaner;

    public ReplicationProperties enable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public ReplicationProperties cron(String cron) {
        this.cron = cron;
        return this;
    }

    public ReplicationProperties batchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public ReplicationProperties retry(int retry) {
        this.retry = retry;
        return this;
    }

    public ReplicationProperties cleaner(ReplicationCleaner cleaner) {
        this.cleaner = cleaner;
        return this;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public ReplicationCleaner getCleaner() {
        return cleaner;
    }

    public void setCleaner(ReplicationCleaner cleaner) {
        this.cleaner = cleaner;
    }

    @Override
    public String toString() {
        return "ReplicationProperties{" +
            "enable=" + enable +
            ", cron='" + cron + '\'' +
            ", batchSize=" + batchSize +
            ", retry=" + retry +
            ", cleaner=" + cleaner +
            '}';
    }

    public static class ReplicationCleaner {

        private Boolean enable;

        private String cron;

        private Integer expiredPeriod;

        public ReplicationCleaner enable(Boolean enable) {
            this.enable = enable;
            return this;
        }

        public ReplicationCleaner cron(String cron) {
            this.cron = cron;
            return this;
        }

        public ReplicationCleaner expiredPeriod(Integer expiredPeriod) {
            this.expiredPeriod = expiredPeriod;
            return this;
        }

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public Integer getExpiredPeriod() {
            return expiredPeriod;
        }

        public void setExpiredPeriod(Integer expiredPeriod) {
            this.expiredPeriod = expiredPeriod;
        }

        @Override
        public String toString() {
            return "ReplicationCleaner{" +
                "enable=" + enable +
                ", cron='" + cron + '\'' +
                ", expiredPeriod=" + expiredPeriod +
                '}';
        }
    }
}

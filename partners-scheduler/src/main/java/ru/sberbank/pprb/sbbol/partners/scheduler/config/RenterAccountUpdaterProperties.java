package ru.sberbank.pprb.sbbol.partners.scheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scheduler.account-updater")
public class RenterAccountUpdaterProperties {

    private boolean enable;

    private int sleepTime;

    private int batchSize;

    public RenterAccountUpdaterProperties enable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public RenterAccountUpdaterProperties sleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public RenterAccountUpdaterProperties batchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public String toString() {
        return "RenterAccountUpdaterProperties{" +
            "enable=" + enable +
            ", sleepTime=" + sleepTime +
            ", batchSize=" + batchSize +
            '}';
    }
}

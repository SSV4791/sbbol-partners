package ru.sberbank.pprb.sbbol.partners.entity.partner.common;

public enum HashKeyResolver {
    STATIC("7352bf20-bab8-4177-9176-33147d0d31d2");

    private final String uuid;

    HashKeyResolver(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}

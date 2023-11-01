package ru.sberbank.pprb.sbbol.partners.replication.repository.model;

import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.UUID;

public class ReplicationFilter {

    private int maxRetry;
    private UUID sessionId;
    private Pagination pagination;

    public ReplicationFilter maxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
        return this;
    }

    public ReplicationFilter pagination(Pagination pagination) {
        this.pagination = pagination;
        return this;
    }

    public ReplicationFilter sessionId(UUID sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public UUID getSessionId() {
        return sessionId;
    }
}

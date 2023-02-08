package ru.sberbank.pprb.sbbol.partners.replication.repository.model;

import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import java.util.UUID;

public class ReplicationFilter {

    private String digitalId;

    private UUID entityId;

    private ReplicationEntityType entityType;

    private ReplicationEntityStatus entityStatus;

    private int partition;

    private Pagination pagination;

    public ReplicationFilter digitalId(String digitalId) {
        this.digitalId = digitalId;
        return this;
    }

    public ReplicationFilter entityId(UUID entityId) {
        this.entityId = entityId;
        return this;
    }

    public ReplicationFilter entityType(ReplicationEntityType entityType) {
        this.entityType = entityType;
        return this;
    }

    public ReplicationFilter entityStatus(ReplicationEntityStatus entityStatus) {
        this.entityStatus = entityStatus;
        return this;
    }

    public ReplicationFilter partition(int partition) {
        this.partition = partition;
        return this;
    }

    public ReplicationFilter pagination(Pagination pagination) {
        this.pagination = pagination;
        return this;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public ReplicationEntityType getEntityType() {
        return entityType;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public ReplicationEntityStatus getEntityStatus() {
        return entityStatus;
    }

    public int getPartition() {
        return partition;
    }
}

package ru.sberbank.pprb.sbbol.partners.replication.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.util.ObjectUtils.isEmpty;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "sbbol_replication")
public class ReplicationEntity extends BaseEntity {

    @Column(name = "digital_id", nullable = false)
    private String digitalId;

    @Column(name = "digital_user_id")
    private String digitalUserId;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 254)
    private ReplicationEntityType entityType;

    @Column(name = "entity_data", nullable = false)
    private String entityData;

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_status", nullable = false, length = 254)
    private ReplicationEntityStatus entityStatus;

    @Column(name = "retry")
    private int retry;

    @PrePersist
    private void initCreateDate() {
        if (isEmpty(createDate)) {
            this.createDate = OffsetDateTime.now();
        }
    }

    public ReplicationEntity digitalId(String digitalId) {
        this.digitalId = digitalId;
        return this;
    }

    public ReplicationEntity digitalUserId(String digitalUserId) {
        this.digitalUserId = digitalUserId;
        return this;
    }

    public ReplicationEntity entityId(UUID entityId) {
        this.entityId = entityId;
        return this;
    }

    public ReplicationEntity entityType(ReplicationEntityType entityType) {
        this.entityType = entityType;
        return this;
    }

    public ReplicationEntity entityData(String entityData) {
        this.entityData = entityData;
        return this;
    }

    public ReplicationEntity entityStatus(ReplicationEntityStatus entityStatus) {
        this.entityStatus = entityStatus;
        return this;
    }

    public ReplicationEntity retry(int retry) {
        this.retry = retry;
        return this;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public String getDigitalUserId() {
        return digitalUserId;
    }

    public void setDigitalUserId(String digitalUserId) {
        this.digitalUserId = digitalUserId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public ReplicationEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(ReplicationEntityType entityType) {
        this.entityType = entityType;
    }

    public String getEntityData() {
        return entityData;
    }

    public void setEntityData(String entityData) {
        this.entityData = entityData;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
    }

    public ReplicationEntityStatus getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(ReplicationEntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ReplicationEntity that = (ReplicationEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return getUuid() == null ? super.hashCode() : Objects.hash(getUuid());
    }

    @Override
    public String getHashKey() {
        return getDigitalId();
    }

    @Override
    public String toString() {
        return "ReplicationEntity{" +
            "digitalId='" + digitalId + '\'' +
            ", digitalUserId='" + digitalUserId + '\'' +
            ", entityId=" + entityId +
            ", entityType=" + entityType +
            ", entityData='" + entityData + '\'' +
            ", createDate=" + createDate +
            ", entityStatus=" + entityStatus +
            ", retry=" + retry +
            '}';
    }
}

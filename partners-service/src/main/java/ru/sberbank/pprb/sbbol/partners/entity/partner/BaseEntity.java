package ru.sberbank.pprb.sbbol.partners.entity.partner;

import com.sbt.pprb.integration.replication.HashKeyProvider;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity implements Serializable, HashKeyProvider {

    @Column(name = "uuid", updatable = false, nullable = false)
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID uuid;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @UpdateTimestamp
    @Column(name = "SYS_LASTCHANGEDATE", nullable = false)
    private OffsetDateTime lastModifiedDate;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public OffsetDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}

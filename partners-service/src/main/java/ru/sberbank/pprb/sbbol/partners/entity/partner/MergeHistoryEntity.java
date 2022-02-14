package ru.sberbank.pprb.sbbol.partners.entity.partner;

import com.sbt.pprb.integration.replication.HashKeyProvider;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Table(
    name = "merge_history",
    indexes = {
        @Index(name = "merge_history_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_merge_history_partner_uuid", columnList = "partner_uuid", unique = true)
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class MergeHistoryEntity implements Serializable, HashKeyProvider {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "uuid", nullable = false)
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID uuid;

    @Column(name = "partner_uuid", nullable = false)
    private UUID partnerUuid;

    @Column(name = "main_uuid", nullable = false)
    private UUID mainUuid;

    @Column(name = "sbbol_uuid", length = 36)
    private String sbbolUuid;

    public UUID getMainUuid() {
        return mainUuid;
    }

    public void setMainUuid(UUID mainUuid) {
        this.mainUuid = mainUuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getPartnerUuid() {
        return partnerUuid;
    }

    public void setPartnerUuid(UUID partnerUuid) {
        this.partnerUuid = partnerUuid;
    }

    public String getSbbolUuid() {
        return sbbolUuid;
    }

    public void setSbbolUuid(String sbbolUuid) {
        this.sbbolUuid = sbbolUuid;
    }

    @Override
    public int hashCode() {
        return getUuid() == null ? super.hashCode() : Objects.hash(getUuid());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MergeHistoryEntity that = (MergeHistoryEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getPartnerUuid().toString();
    }
}

package ru.sberbank.pprb.sbbol.partners.entity.partner;

import com.sbt.pprb.integration.replication.HashKeyProvider;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Table(name = "merge_history")
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
    private UUID id;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
    public String getHashKey() {
        return getPartnerUuid().toString();
    }
}

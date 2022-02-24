package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serial;
import java.util.UUID;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(
    name = "email",
    indexes = {
        @Index(name = "email_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_email_unified_uuid", columnList = "unified_uuid")
    }
)
public class EmailEntity extends EmailBaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "unified_uuid", nullable = false)
    private UUID unifiedUuid;

    public UUID getUnifiedUuid() {
        return unifiedUuid;
    }

    public void setUnifiedUuid(UUID unifiedUuid) {
        this.unifiedUuid = unifiedUuid;
    }

    @Override
    public String getHashKey() {
        return getUnifiedUuid().toString();
    }
}

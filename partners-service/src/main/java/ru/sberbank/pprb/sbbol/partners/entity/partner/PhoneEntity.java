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
    name = "phone",
    indexes = {
        @Index(name = "phone_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_phone_unified_uuid", columnList = "unified_uuid")
    }
)
public class PhoneEntity extends PhoneBaseEntity {

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

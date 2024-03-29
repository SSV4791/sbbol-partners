package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(
    name = "email",
    indexes = {
        @Index(name = "email_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_email_unified_uuid_digital_id", columnList = "unified_uuid,digital_id")
    }
)
public class PartnerEmailEntity extends EmailBaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unified_uuid")
    private PartnerEntity partner;

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
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
        PartnerEmailEntity that = (PartnerEmailEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return partner.getHashKey();
    }
}

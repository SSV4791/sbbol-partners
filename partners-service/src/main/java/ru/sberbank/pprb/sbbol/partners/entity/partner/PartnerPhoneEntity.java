package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "phone")
public class PartnerPhoneEntity extends PhoneBaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @ManyToOne
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
        return getId() == null ? super.hashCode() : Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PartnerPhoneEntity that = (PartnerPhoneEntity) obj;
        if (getId() == null || that.getId() == null) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public String getHashKey() {
        return partner.getHashKey();
    }
}

package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serial;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "email")
public class PartnerEmailEntity extends EmailBaseEntity {

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
    public String getHashKey() {
        return partner.getHashKey();
    }
}

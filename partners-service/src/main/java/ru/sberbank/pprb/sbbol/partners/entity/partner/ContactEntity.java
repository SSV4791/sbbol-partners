package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "contact", indexes = {
    @Index(name = "i_contact_partner_uuid", columnList = "partner_uuid")
})
@DynamicUpdate
@DynamicInsert
@Entity
public class ContactEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_uuid", nullable = false)
    private PartnerEntity partner;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "phone", length = 100)
    private String phone;

    @Column(name = "email", length = 320)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

    @Override
    public String getHashKey() {
        return partner.getId().toString();
    }
}

package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "email")
public class ContactEmailEntity extends EmailBaseEntity {

    @ManyToOne
    @JoinColumn(name = "unified_uuid")
    private ContactEntity contact;

    public ContactEntity getContact() {
        return contact;
    }

    public void setContact(ContactEntity contact) {
        this.contact = contact;
    }

    @Override
    public String getHashKey() {
        return contact.getHashKey();
    }

}

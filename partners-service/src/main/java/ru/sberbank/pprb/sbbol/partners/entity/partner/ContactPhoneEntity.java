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
public class ContactPhoneEntity extends PhoneBaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

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
        ContactPhoneEntity that = (ContactPhoneEntity) obj;
        if (getId() == null || that.getId() == null) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public String getHashKey() {
        return contact.getHashKey();
    }
}

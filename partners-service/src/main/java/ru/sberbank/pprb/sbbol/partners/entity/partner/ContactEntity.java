package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Table(
    name = "contact",
    indexes = {
        @Index(name = "contact_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_contact_partner_uuid", columnList = "partner_uuid"),
        @Index(name = "i_contact_digital_id", columnList = "digital_id")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class ContactEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "partner_uuid", nullable = false)
    private UUID partnerUuid;

    @Column(name = "digital_id", nullable = false)
    private String digitalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "legal_type", nullable = false, length = 254)
    private LegalType type;

    @Column(name = "org_name", length = 350)
    private String orgName;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "second_name", length = 50)
    private String secondName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Column(name = "position", length = 100)
    private String position;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactPhoneEntity> phones;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactEmailEntity> emails;

    public UUID getPartnerUuid() {
        return partnerUuid;
    }

    public void setPartnerUuid(UUID partnerUuid) {
        this.partnerUuid = partnerUuid;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public LegalType getType() {
        return type;
    }

    public void setType(LegalType type) {
        this.type = type;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<ContactPhoneEntity> getPhones() {
        if (phones == null) {
            phones = new ArrayList<>();
        }
        return phones;
    }

    public void setPhones(List<ContactPhoneEntity> phones) {
        this.phones = phones;
    }

    public List<ContactEmailEntity> getEmails() {
        if (emails == null) {
            emails = new ArrayList<>();
        }
        return emails;
    }

    public void setEmails(List<ContactEmailEntity> emails) {
        this.emails = emails;
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
        ContactEntity that = (ContactEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getPartnerUuid().toString();
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
            "partnerUuid=" + partnerUuid +
            ", digitalId='" + digitalId + '\'' +
            ", type=" + type +
            ", orgName='" + orgName + '\'' +
            ", firstName='" + firstName + '\'' +
            ", secondName='" + secondName + '\'' +
            ", middleName='" + middleName + '\'' +
            ", position='" + position + '\'' +
            ", phones=" + phones +
            ", emails=" + emails +
            '}';
    }
}

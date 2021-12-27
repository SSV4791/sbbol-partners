package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.util.UUID;

@Table(name = "contact"
//    , indexes = {
//    @Index(name = "i_contact_partner_uuid", columnList = "partner_uuid")
//}
)
@DynamicUpdate
@DynamicInsert
@Entity
public class ContactEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

//    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "partner_uuid", nullable = false)
    private UUID partnerUuid;

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

//    @Column(name = "phone", length = 100)
//    private String phone;
//
//    @Column(name = "email", length = 320)
//    private String email;

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public UUID getPartnerUuid() {
        return partnerUuid;
    }

    public void setPartnerUuid(UUID partnerUuid) {
        this.partnerUuid = partnerUuid;
    }

    @Override
    public String getHashKey() {
        return null;
    }
}

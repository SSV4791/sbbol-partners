package ru.sberbank.pprb.sbbol.migration.correspondents.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationLegalType;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationPartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationPartnerType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.Objects;

@Table(name = "partner", indexes = {
    @Index(name = "i_partner_uuid", columnList = "digital_id")
})
@DynamicUpdate
@DynamicInsert
@Entity
public class MigrationPartnerEntity extends MigrationBaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @CreationTimestamp
    @Column(name = "create_date", nullable = false)
    private OffsetDateTime createDate;

    @Column(name = "digital_id")
    private String digitalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 254)
    private MigrationPartnerType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "legal_type", nullable = false, length = 254)
    private MigrationLegalType legalType;

    @Column(name = "org_name", length = 50)
    private String orgName;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "inn", length = 12)
    private String inn;

    @Column(name = "kpp", length = 9)
    private String kpp;

    @Column(name = "comment")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "citizenship", length = 10)
    private MigrationPartnerCitizenshipType citizenship;

    @OneToOne(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    private MigrationPartnerPhoneEntity phone;

    @OneToOne(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    private MigrationPartnerEmailEntity email;

    @OneToOne(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    private MigrationPartnerAccountEntity account;

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public MigrationPartnerType getType() {
        return type;
    }

    public void setType(MigrationPartnerType type) {
        this.type = type;
    }

    public MigrationLegalType getLegalType() {
        return legalType;
    }

    public void setLegalType(MigrationLegalType migrationLegalType) {
        this.legalType = migrationLegalType;
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

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public MigrationPartnerCitizenshipType getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(MigrationPartnerCitizenshipType citizenship) {
        this.citizenship = citizenship;
    }

    public MigrationPartnerPhoneEntity getPhone() {
        return phone;
    }

    public void setPhone(MigrationPartnerPhoneEntity phone) {
        this.phone = phone;
    }

    public MigrationPartnerEmailEntity getEmail() {
        return email;
    }

    public void setEmail(MigrationPartnerEmailEntity email) {
        this.email = email;
    }

    public MigrationPartnerAccountEntity getAccount() {
        return account;
    }

    public void setAccount(MigrationPartnerAccountEntity account) {
        this.account = account;
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
        MigrationPartnerEntity that = (MigrationPartnerEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getDigitalId();
    }
}

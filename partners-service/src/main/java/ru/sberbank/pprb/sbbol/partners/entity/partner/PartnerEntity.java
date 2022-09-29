package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table(
    name = "partner",
    indexes = {
        @Index(name = "i_partner_uuid", columnList = "digital_id"),
        @Index(name = "idx_partner_digital_id_search", columnList = "digital_id, search", unique = true)
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class PartnerEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "create_date", nullable = false)
    private OffsetDateTime createDate;

    @Column(name = "digital_id")
    private String digitalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 254)
    private PartnerType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "legal_type", nullable = false, length = 254)
    private LegalType legalType;

    @Column(name = "org_name", length = 350)
    private String orgName;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "second_name", length = 50)
    private String secondName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Column(name = "inn", length = 12)
    private String inn;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inn", referencedColumnName = "inn", insertable = false, updatable = false)
    private GkuInnEntity gkuInnEntity;

    @Column(name = "kpp", length = 9)
    private String kpp;

    @Column(name = "ogrn", length = 15)
    private String ogrn;

    @Column(name = "okpo", length = 30)
    private String okpo;

    @Column(name = "comment")
    private String comment;

    @Column(name = "search", length = 1000)
    private String search;

    @Enumerated(EnumType.STRING)
    @Column(name = "citizenship", length = 20)
    private PartnerCitizenshipType citizenship;

    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnerPhoneEntity> phones;

    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnerEmailEntity> emails;


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

    public PartnerType getType() {
        return type;
    }

    public void setType(PartnerType type) {
        this.type = type;
    }

    public LegalType getLegalType() {
        return legalType;
    }

    public void setLegalType(LegalType legalType) {
        this.legalType = legalType;
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

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public String getOkpo() {
        return okpo;
    }

    public void setOkpo(String okpo) {
        this.okpo = okpo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PartnerCitizenshipType getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(PartnerCitizenshipType citizenship) {
        this.citizenship = citizenship;
    }

    public List<PartnerPhoneEntity> getPhones() {
        if (phones == null) {
            phones = new ArrayList<>();
        }
        return phones;
    }

    public void setPhones(List<PartnerPhoneEntity> phones) {
        this.phones = phones;
    }

    public List<PartnerEmailEntity> getEmails() {
        if (emails == null) {
            emails = new ArrayList<>();
        }
        return emails;
    }

    public void setEmails(List<PartnerEmailEntity> emails) {
        this.emails = emails;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public GkuInnEntity getGkuInnEntity() {
        return gkuInnEntity;
    }

    public void setGkuInnEntity(GkuInnEntity gkuInnEntity) {
        this.gkuInnEntity = gkuInnEntity;
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
        PartnerEntity that = (PartnerEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getDigitalId();
    }

    @PrePersist
    private void initCreateDate() {
        if (ObjectUtils.isEmpty(createDate)) {
            this.createDate = OffsetDateTime.now();
        }
    }
}

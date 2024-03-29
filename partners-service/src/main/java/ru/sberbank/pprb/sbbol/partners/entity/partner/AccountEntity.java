package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Table(
    name = "account",
    indexes = {
        @Index(name = "account_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_account_partner_uuid_digital_id", columnList = "partner_uuid, digital_id"),
        @Index(name = "idx_account_digital_id_partner_uuid_search", columnList = "digital_id, search", unique = true)
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class AccountEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "create_date", nullable = false)
    private OffsetDateTime createDate;

    @Column(name = "partner_uuid", nullable = false)
    private UUID partnerUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private PartnerEntity partner;

    @Column(name = "digital_id", nullable = false)
    private String digitalId;

    @Column(name = "account", length = 20)
    private String account;

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type", nullable = false, length = 254)
    private PartnerType partnerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 10)
    private AccountStateType state;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private BankEntity bank;

    @Column(name = "priority_account")
    private Boolean priorityAccount;

    @Column(name = "comment", length = 50)
    private String comment;

    @Column(name = "search", length = 500)
    private String search;

    public Boolean getPriorityAccount() {
        if (priorityAccount == null) {
            return Boolean.FALSE;
        }
        return priorityAccount;
    }

    public void setPriorityAccount(Boolean priorityAccount) {
        this.priorityAccount = priorityAccount;
    }

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

    public AccountStateType getState() {
        if (state == null) {
            return AccountStateType.NOT_SIGNED;
        }
        return state;
    }

    public void setState(AccountStateType state) {
        this.state = state;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public UUID getPartnerUuid() {
        return partnerUuid;
    }

    public void setPartnerUuid(UUID partnerUuid) {
        this.partnerUuid = partnerUuid;
    }

    public BankEntity getBank() {
        return bank;
    }

    public void setBank(BankEntity bank) {
        this.bank = bank;
    }

    public String getComment() {
        return comment;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

    public PartnerType getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(PartnerType partnerType) {
        this.partnerType = partnerType;
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
        AccountEntity that = (AccountEntity) obj;
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

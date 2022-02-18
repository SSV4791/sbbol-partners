package ru.sberbank.pprb.sbbol.migration.correspondents.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationAccountStateType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.Objects;

@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "account")
public class MigrationPartnerAccountEntity extends MigrationBaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @CreationTimestamp
    @Column(name = "create_date", nullable = false)
    private OffsetDateTime createDate;

    @UpdateTimestamp
    @Column(name = "last_modified_date", nullable = false)
    private OffsetDateTime lastModifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_uuid")
    private MigrationPartnerEntity partner;

    @Column(name = "digital_id", nullable = false)
    private String digitalId;

    @Column(name = "account", length = 20)
    private String account;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 10, columnDefinition = "varchar(10) default 'NOT_SIGN'")
    private MigrationAccountStateType state;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private MigrationBankEntity bank;

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
    }

    public OffsetDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public MigrationPartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(MigrationPartnerEntity partner) {
        this.partner = partner;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public MigrationAccountStateType getState() {
        return state;
    }

    public void setState(MigrationAccountStateType state) {
        this.state = state;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public MigrationBankEntity getBank() {
        return bank;
    }

    public void setBank(MigrationBankEntity bank) {
        this.bank = bank;
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
        MigrationPartnerAccountEntity that = (MigrationPartnerAccountEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getPartner().getHashKey();
    }
}

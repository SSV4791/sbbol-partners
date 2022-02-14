package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Table(
    name = "account",
    indexes = {
        @Index(name = "account_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_account_digital_id", columnList = "digital_id"),
        @Index(name = "i_account_digital_id_partner_uuid", columnList = "digital_id, partner_uuid"),
        @Index(name = "i_account_digital_id_partner_uuid_account", columnList = "digital_id, partner_uuid, account"),
        @Index(name = "i_account_partner_uuid", columnList = "partner_uuid")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class AccountEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @CreationTimestamp
    @Column(name = "create_date", nullable = false)
    private OffsetDateTime createDate;

    @UpdateTimestamp
    @Column(name = "last_modified_date", nullable = false)
    private OffsetDateTime lastModifiedDate;

    @Column(name = "partner_uuid", nullable = false)
    private UUID partnerUuid;

    @Column(name = "digital_id", nullable = false)
    private String digitalId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "account", length = 20)
    private String account;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 10, columnDefinition = "varchar(10) default 'NOT_SIGN'")
    private AccountStateType state;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankEntity> banks;

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

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public AccountStateType getState() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getPartnerUuid() {
        return partnerUuid;
    }

    public void setPartnerUuid(UUID partnerUuid) {
        this.partnerUuid = partnerUuid;
    }

    public List<BankEntity> getBanks() {
        if (banks == null) {
            banks = new ArrayList<>();
        }
        return banks;
    }

    public void setBanks(List<BankEntity> banks) {
        this.banks = banks;
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
        return getPartnerUuid().toString();
    }
}

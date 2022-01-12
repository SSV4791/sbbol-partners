package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Table(name = "account",
    indexes = {
        @Index(name = "i_account_digital_id", columnList = "digital_id"),
        @Index(name = "i_account_partner_uuid", columnList = "partner_uuid")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class AccountEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

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

    @Column(name = "sign_collection_id", length = 36)
    private String signCollectionId;

    public String getSignCollectionId() {
        return signCollectionId;
    }

    public void setSignCollectionId(String signCollectionId) {
        this.signCollectionId = signCollectionId;
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
        AddressEntity that = (AddressEntity) obj;
        if (getId() == null || that.getId() == null) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public String getHashKey() {
        return getId().toString();
    }
}

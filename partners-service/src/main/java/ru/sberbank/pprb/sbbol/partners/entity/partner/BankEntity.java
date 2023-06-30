package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;

@Table(
    name = "bank",
    indexes = {
        @Index(name = "bank_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_bank_account_uuid", columnList = "account_uuid")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class BankEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_uuid", nullable = false)
    private AccountEntity account;

    @Column(name = "name", length = 160)
    private String name;

    @Column(name = "bic", length = 9)
    private String bic;

    @Column(name = "intermediary")
    @ColumnDefault(value = "false")
    private Boolean intermediary;

    @OneToOne(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    private BankAccountEntity bankAccount;

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public BankAccountEntity getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccountEntity bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Boolean getIntermediary() {
        return intermediary;
    }

    public void setIntermediary(Boolean intermediary) {
        this.intermediary = intermediary;
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
        BankEntity that = (BankEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getAccount().getHashKey();
    }
}

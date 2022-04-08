package ru.sberbank.pprb.sbbol.migration.correspondents.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;

@Table(name = "bank", indexes = {
    @Index(name = "i_bank_account_uuid", columnList = "account_uuid")
})
@DynamicUpdate
@DynamicInsert
@Entity
public class MigrationBankEntity extends MigrationBaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_uuid", nullable = false)
    private MigrationPartnerAccountEntity account;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "bic", length = 9)
    private String bic;

    @OneToOne(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    private MigrationBankAccountEntity bankAccount;

    @Column(name = "intermediary")
    private Boolean intermediary;

    public Boolean getIntermediary() {
        return intermediary;
    }

    public void setIntermediary(Boolean intermediary) {
        this.intermediary = intermediary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public MigrationPartnerAccountEntity getAccount() {
        return account;
    }

    public void setAccount(MigrationPartnerAccountEntity account) {
        this.account = account;
    }

    public MigrationBankAccountEntity getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(MigrationBankAccountEntity bankAccount) {
        this.bankAccount = bankAccount;
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
        MigrationBankEntity that = (MigrationBankEntity) obj;
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

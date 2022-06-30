package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
    name = "bank_account",
    indexes = {
        @Index(name = "bank_account_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_bank_account_bank_uuid", columnList = "bank_uuid")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class BankAccountEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_uuid", nullable = false)
    private BankEntity bank;

    @Column(name = "account", nullable = false, length = 20)
    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BankEntity getBank() {
        return bank;
    }

    public void setBank(BankEntity bank) {
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
        BankAccountEntity that = (BankAccountEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getBank().getHashKey();
    }

    @Override
    public String toString() {
        return "BankAccountEntity{" +
            "bank=" + (bank == null ? null : bank.getUuid()) +
            ", account='" + account + '\'' +
            '}';
    }
}

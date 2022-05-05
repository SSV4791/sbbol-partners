package ru.sberbank.pprb.sbbol.migration.correspondents.entity;

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

@Table(name = "bank_account", indexes = {
    @Index(name = "i_bank_account_bank_uuid", columnList = "bank_uuid")
})
@DynamicUpdate
@DynamicInsert
@Entity
public class MigrationBankAccountEntity extends MigrationBaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_uuid", nullable = false)
    private MigrationBankEntity bank;

    @Column(name = "account", nullable = false, length = 20)
    private String account;

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
        MigrationBankAccountEntity that = (MigrationBankAccountEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getBank().getHashKey();
    }
}

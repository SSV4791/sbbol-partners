package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Table(name = "bank", indexes = {
    @Index(name = "i_bank_account_uuid", columnList = "account_uuid")
})
@DynamicUpdate
@DynamicInsert
@Entity
public class BankEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_uuid", nullable = false)
    private AccountEntity account;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "bic", length = 9)
    private String bic;

    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankAccountEntity> bankAccounts;

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

    public List<BankAccountEntity> getBankAccounts() {
        if (bankAccounts == null) {
            bankAccounts = new ArrayList<>();
        }
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccountEntity> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    @Override
    public String getHashKey() {
        return account.getPartner().getId().toString();
    }
}

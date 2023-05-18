package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BankType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

    @OneToOne(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    private BankAccountEntity bankAccount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "bank_type", nullable = false)
    private BankType type;

    @Column(name = "swift_code")
    private String swiftCode;

    @Column(name = "clearing_country_code")
    private String clearingCountryCode;

    @Column(name = "clearing_bank_code")
    private String clearingBankCode;

    @Column(name = "clearing_bank_symbol_code")
    private String clearingBankSymbolCode;

    @Column(name = "clearing_bank_code_name")
    private String clearingBankCodeName;

    @Column(name = "bank_option")
    private String bankOption;

    @Column(name = "filial")
    private String filial;

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

    public BankType getType() {
        return type;
    }

    public void setType(BankType type) {
        this.type = type;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getClearingCountryCode() {
        return clearingCountryCode;
    }

    public void setClearingCountryCode(String clearingCountryCode) {
        this.clearingCountryCode = clearingCountryCode;
    }

    public String getClearingBankCode() {
        return clearingBankCode;
    }

    public void setClearingBankCode(String clearingBankCode) {
        this.clearingBankCode = clearingBankCode;
    }

    public String getClearingBankSymbolCode() {
        return clearingBankSymbolCode;
    }

    public void setClearingBankSymbolCode(String clearingBankSymbolCode) {
        this.clearingBankSymbolCode = clearingBankSymbolCode;
    }

    public String getClearingBankCodeName() {
        return clearingBankCodeName;
    }

    public void setClearingBankCodeName(String clearingBankCodeName) {
        this.clearingBankCodeName = clearingBankCodeName;
    }

    public String getBankOption() {
        return bankOption;
    }

    public void setBankOption(String bankOption) {
        this.bankOption = bankOption;
    }

    public String getFilial() {
        return filial;
    }

    public void setFilial(String filial) {
        this.filial = filial;
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

package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;

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
import javax.persistence.Table;

@Table(name = "account", indexes = {
    @Index(name = "i_account_partner_uuid", columnList = "partner_uuid")
})
@DynamicUpdate
@DynamicInsert
@Entity
public class AccountEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_uuid", nullable = false)
    private PartnerEntity partner;

    @Column(name = "digital_id", nullable = false)
    private String digitalId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "account", length = 20)
    private String account;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 10, columnDefinition = "varchar(10) default 'NOT_SIGN'")
    private AccountStateType state;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private BankEntity bank;

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

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

    public BankEntity getBank() {
        return bank;
    }

    public void setBank(BankEntity bank) {
        this.bank = bank;
    }

    @Override
    public String getHashKey() {
        return partner.getId().toString();
    }
}

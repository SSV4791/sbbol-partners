package ru.sberbank.pprb.sbbol.partners.entity.partner;

import com.sbt.pprb.integration.replication.HashKeyProvider;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "replication_history")
public class ReplicationHistory implements Serializable, HashKeyProvider {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "uuid", nullable = false)
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID uuid;

    @Column(name = "partner_uuid", nullable = false)
    private UUID partnerUuid;

    @Column(name = "document_uuid")
    private UUID documentUuid;

    @Column(name = "account_uuid")
    private UUID accountUuid;

    @Column(name = "bank_uuid")
    private UUID bankUuid;

    @Column(name = "bank_account_uuid")
    private UUID bankAccountUuid;

    @Column(name = "email_uuid")
    private UUID emailUuid;

    @Column(name = "address_uuid")
    private UUID addressUuid;

    @Column(name = "phone_uuid")
    private UUID phoneUuid;

    @Column(name = "sbbol_guid", length = 36)
    private String sbbolGuid;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getSbbolGuid() {
        return sbbolGuid;
    }

    public void setSbbolGuid(String sbbolGuid) {
        this.sbbolGuid = sbbolGuid;
    }

    public UUID getPhoneUuid() {
        return phoneUuid;
    }

    public void setPhoneUuid(UUID phoneUuid) {
        this.phoneUuid = phoneUuid;
    }

    public UUID getAddressUuid() {
        return addressUuid;
    }

    public void setAddressUuid(UUID addressUuid) {
        this.addressUuid = addressUuid;
    }

    public UUID getEmailUuid() {
        return emailUuid;
    }

    public void setEmailUuid(UUID emailUuid) {
        this.emailUuid = emailUuid;
    }

    public UUID getBankAccountUuid() {
        return bankAccountUuid;
    }

    public void setBankAccountUuid(UUID bankAccountUuid) {
        this.bankAccountUuid = bankAccountUuid;
    }

    public UUID getBankUuid() {
        return bankUuid;
    }

    public void setBankUuid(UUID bankUuid) {
        this.bankUuid = bankUuid;
    }

    public UUID getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(UUID accountUuid) {
        this.accountUuid = accountUuid;
    }

    public UUID getDocumentUuid() {
        return documentUuid;
    }

    public void setDocumentUuid(UUID documentUuid) {
        this.documentUuid = documentUuid;
    }

    public UUID getPartnerUuid() {
        return partnerUuid;
    }

    public void setPartnerUuid(UUID partnerUuid) {
        this.partnerUuid = partnerUuid;
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
        ReplicationHistory that = (ReplicationHistory) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return null;
    }
}

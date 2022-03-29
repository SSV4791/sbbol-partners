package ru.sberbank.pprb.sbbol.migration.correspondents.entity;

import com.sbt.pprb.integration.replication.HashKeyProvider;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(
    name = "replication_history",
    indexes = {
        @Index(name = "replication_history_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_replication_history_account_uuid", columnList = "account_uuid"),
        @Index(name = "i_replication_history_address_uuid", columnList = "address_uuid"),
        @Index(name = "i_replication_history_bank_account_uuid", columnList = "bank_account_uuid"),
        @Index(name = "i_replication_history_bank_uuid", columnList = "bank_uuid"),
        @Index(name = "i_replication_history_document_uuid", columnList = "document_uuid"),
        @Index(name = "i_replication_history_email_uuid", columnList = "email_uuid"),
        @Index(name = "i_replication_history_partner_uuid", columnList = "partner_uuid"),
        @Index(name = "i_replication_history_phone_uuid", columnList = "phone_uuid"),
        @Index(name = "i_replication_history_sbbol_guid", columnList = "sbbol_guid")
    }
)
public class MigrationReplicationHistoryEntity implements Serializable, HashKeyProvider {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "uuid", updatable = false, nullable = false)
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
        MigrationReplicationHistoryEntity that = (MigrationReplicationHistoryEntity) obj;
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

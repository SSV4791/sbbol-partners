package ru.sberbank.pprb.sbbol.partners.entity.renter;

import com.sbt.pprb.integration.replication.HashKeyProvider;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Deprecated(forRemoval = true)
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "flat_renter")
public class FlatRenter implements Serializable, HashKeyProvider {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "uuid", updatable = false, nullable = false)
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID uuid;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "SYS_LASTCHANGEDATE", nullable = false)
    private OffsetDateTime lastModifiedDate;

    @Column(name = "partner_uuid", nullable = false)
    private UUID partnerUuid;

    @Column(name = "digital_id")
    private String digitalId;

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

    @Column(name = "legal_address_uuid")
    private UUID legalAddressUuid;

    @Column(name = "physical_address_uuid")
    private UUID physicalAddressUuid;

    @Column(name = "phone_uuid")
    private UUID phoneUuid;

    @PreUpdate
    @PrePersist
    public void updateSysLastChangeDate() {
        lastModifiedDate = OffsetDateTime.now();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public OffsetDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public UUID getPhoneUuid() {
        return phoneUuid;
    }

    public void setPhoneUuid(UUID phoneUuid) {
        this.phoneUuid = phoneUuid;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public UUID getLegalAddressUuid() {
        return legalAddressUuid;
    }

    public void setLegalAddressUuid(UUID legalAddressUuid) {
        this.legalAddressUuid = legalAddressUuid;
    }

    public UUID getPhysicalAddressUuid() {
        return physicalAddressUuid;
    }

    public void setPhysicalAddressUuid(UUID physicalAddressUuid) {
        this.physicalAddressUuid = physicalAddressUuid;
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
        FlatRenter that = (FlatRenter) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getDigitalId();
    }
}



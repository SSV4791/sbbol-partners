package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Table(
    name = "sign",
    indexes = {
        @Index(name = "sign_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_sign_account_uuid", columnList = "account_uuid")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class SignEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "entity_uuid", nullable = false)
    private UUID entityUuid;

    @Column(name = "digest", nullable = false, length = 4000)
    private String digest;

    @Column(name = "sign", nullable = false, length = 4000)
    private String sign;

    @Column(name = "partner_uuid", nullable = false)
    private UUID partnerUuid;

    @Column(name = "account_uuid", nullable = false)
    private UUID accountUuid;

    @Column(name = "external_data_file_id")
    private String externalDataFileId;

    @Column(name = "external_data_sign_file_id")
    private String externalDataSignFileId;

    @Column(name = "sign_profile_Id")
    private String signProfileId;

    @Column(name = "date_time_of_sign")
    private OffsetDateTime dateTimeOfSign;

    public OffsetDateTime getDateTimeOfSign() {
        return dateTimeOfSign;
    }

    public void setDateTimeOfSign(OffsetDateTime dateTimeOfSign) {
        this.dateTimeOfSign = dateTimeOfSign;
    }

    public String getExternalDataSignFileId() {
        return externalDataSignFileId;
    }

    public void setExternalDataSignFileId(String externalDataSignFileId) {
        this.externalDataSignFileId = externalDataSignFileId;
    }

    public String getExternalDataFileId() {
        return externalDataFileId;
    }

    public void setExternalDataFileId(String externalDataFileId) {
        this.externalDataFileId = externalDataFileId;
    }

    public UUID getPartnerUuid() {
        return partnerUuid;
    }

    public void setPartnerUuid(UUID partnerUuid) {
        this.partnerUuid = partnerUuid;
    }

    public UUID getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(UUID accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public void setEntityUuid(UUID entityUuid) {
        this.entityUuid = entityUuid;
    }

    public String getSignProfileId() {
        return signProfileId;
    }

    public void setSignProfileId(String signProfileId) {
        this.signProfileId = signProfileId;
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
        SignEntity that = (SignEntity) obj;
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

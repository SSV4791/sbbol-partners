package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;
import java.util.UUID;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(
    name = "account_additional",
    indexes = {
        @Index(name = "account_additional_pkey", columnList = "uuid", unique = true),
        @Index(name = "idx_account_additional_account_uuid", columnList = "account_uuid"),
        @Index(name = "idx_account_additional_digital_id_account_uuid", columnList = "digital_id, account_uuid"),
    }
)
public class AdditionalAccountEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "digital_id", nullable = false)
    private String digitalId;

    @Column(name = "account_uuid", nullable = false)
    private UUID accountUuid;

    @Column(name = "ground")
    private String ground;

    @Column(name = "operation_code")
    private String operationCode;

    @Column(name = "operation_name")
    private String operationName;

    @Column(name = "commission_type")
    private String commissionType;

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public UUID getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(UUID accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getGround() {
        return ground;
    }

    public void setGround(String ground) {
        this.ground = ground;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(String commissionType) {
        this.commissionType = commissionType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AdditionalAccountEntity that = (AdditionalAccountEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return getUuid() == null ? super.hashCode() : Objects.hash(getUuid());
    }

    @Override
    public String getHashKey() {
        return getDigitalId();
    }
}

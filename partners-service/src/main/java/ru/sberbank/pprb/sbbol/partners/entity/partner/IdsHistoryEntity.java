package ru.sberbank.pprb.sbbol.partners.entity.partner;

import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.ParentType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;
import java.util.UUID;

@Table(
    name = "ids_history",
    indexes = {
        @Index(name = "ids_history_pkey", columnList = "uuid", unique = true),
        @Index(name = "idx_ids_history_digital_id_external_id_parent_type", columnList = "digital_id, external_id, parent_type", unique = true),
        @Index(name = "idx_ids_history_pprb_entity_id_digital_id", columnList = "pprb_entity_id, digital_id"),
    }
)
@Entity
public class IdsHistoryEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "digital_id", nullable = false)
    private String digitalId;

    @Column(name = "external_id", nullable = false)
    private UUID externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "parent_type", nullable = false, length = 254)
    private ParentType parentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pprb_entity_id")
    private AccountEntity account;

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public void setExternalId(UUID externalId) {
        this.externalId = externalId;
    }

    public ParentType getParentType() {
        return parentType;
    }

    public void setParentType(ParentType parentType) {
        this.parentType = parentType;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    @PrePersist
    private void setExternalId() {
        if (Objects.isNull(externalId)) {
            setExternalId(account.getUuid());
        }
    }

    @Override
    public String getHashKey() {
        return getDigitalId();
    }
}

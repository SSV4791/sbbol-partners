package ru.sberbank.pprb.sbbol.partners.entity.partner;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serial;
import java.util.UUID;

@Table(
    name = "ids_history",
    indexes = {
        @Index(name = "idx_ids_history_digital_id_external_id", columnList = "digital_id, external_id", unique = true),
        @Index(name = "idx_ids_history_digital_id_pprb_entity_id", columnList = "digital_id, pprb_entity_id"),
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

    @Column(name = "pprb_entity_id", nullable = false)
    private UUID pprbEntityId;

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

    public UUID getPprbEntityId() {
        return pprbEntityId;
    }

    public void setPprbEntityId(UUID pprbEntityId) {
        this.pprbEntityId = pprbEntityId;
    }

    @Override
    public String getHashKey() {
        return getDigitalId();
    }
}




package ru.sberbank.pprb.sbbol.migration.correspondents.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "email")
public class MigrationPartnerEmailEntity extends MigrationBaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unified_uuid")
    private MigrationPartnerEntity partner;

    @Column(name = "digital_id")
    private String digitalId;

    @Column(name = "email", length = 100)
    private String email;

    public MigrationPartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(MigrationPartnerEntity partner) {
        this.partner = partner;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        MigrationPartnerEmailEntity that = (MigrationPartnerEmailEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getPartner().getHashKey();
    }
}

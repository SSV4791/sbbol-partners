package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.DocumentCertifierType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Table(name = "document",
    indexes = {
        @Index(name = "i_document_unified_uuid", columnList = "unified_uuid"),
        @Index(name = "i_document_digital_id", columnList = "digital_id")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class DocumentEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "unified_uuid", nullable = false)
    private UUID unifiedUuid;

    @Column(name = "digital_id", nullable = false)
    private String digitalId;

    @OneToOne
    @JoinColumn(name = "type_uuid", nullable = false, insertable = false, updatable = false)
    private DocumentTypeEntity type;

    @Column(name = "type_uuid", nullable = false)
    private UUID typeUuid;

    @Column(name = "series", length = 50)
    private String series;

    @Column(name = "number", length = 50)
    private String number;

    @Column(name = "date_issue")
    private LocalDate dateIssue;

    @Column(name = "division_issue", length = 250)
    private String divisionIssue;

    @Column(name = "division_code", length = 50)
    private String divisionCode;

    @Column(name = "certifier_name", length = 100)
    private String certifierName;

    @Column(name = "position_certifier", length = 100)
    private String positionCertifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "certifier_type", nullable = false, length = 10)
    private DocumentCertifierType certifierType;

    public UUID getUnifiedUuid() {
        return unifiedUuid;
    }

    public void setUnifiedUuid(UUID unifiedUuid) {
        this.unifiedUuid = unifiedUuid;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public DocumentTypeEntity getType() {
        return type;
    }

    public void setType(DocumentTypeEntity type) {
        this.type = type;
    }

    public UUID getTypeUuid() {
        return typeUuid;
    }

    public void setTypeUuid(UUID typeUuid) {
        this.typeUuid = typeUuid;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getDateIssue() {
        return dateIssue;
    }

    public void setDateIssue(LocalDate dateIssue) {
        this.dateIssue = dateIssue;
    }

    public String getDivisionIssue() {
        return divisionIssue;
    }

    public void setDivisionIssue(String divisionIssue) {
        this.divisionIssue = divisionIssue;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }

    public String getCertifierName() {
        return certifierName;
    }

    public void setCertifierName(String certifierName) {
        this.certifierName = certifierName;
    }

    public String getPositionCertifier() {
        return positionCertifier;
    }

    public void setPositionCertifier(String positionCertifier) {
        this.positionCertifier = positionCertifier;
    }

    public DocumentCertifierType getCertifierType() {
        return certifierType;
    }

    public void setCertifierType(DocumentCertifierType certifierType) {
        this.certifierType = certifierType;
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
        DocumentEntity that = (DocumentEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getUnifiedUuid().toString();
    }
}

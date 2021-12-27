package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "document"
//    , indexes = {
//    @Index(name = "i_document_partner_uuid", columnList = "partner_uuid")
//}
)
@DynamicUpdate
@DynamicInsert
@Entity
public class DocumentEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

//    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "unified_uuid", nullable = false)
    private UUID unifiedUuid;

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

    public String getPositionCertifier() {
        return positionCertifier;
    }

    public void setPositionCertifier(String positionCertifier) {
        this.positionCertifier = positionCertifier;
    }

    public String getCertifierName() {
        return certifierName;
    }

    public void setCertifierName(String certifierName) {
        this.certifierName = certifierName;
    }

    public UUID getTypeUuid() {
        return typeUuid;
    }

    public void setTypeUuid(UUID typeUuid) {
        this.typeUuid = typeUuid;
    }

    public UUID getUnifiedUuid() {
        return unifiedUuid;
    }

    public void setUnifiedUuid(UUID unifiedUuid) {
        this.unifiedUuid = unifiedUuid;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }

    public String getDivisionIssue() {
        return divisionIssue;
    }

    public void setDivisionIssue(String divisionIssue) {
        this.divisionIssue = divisionIssue;
    }

    public LocalDate getDateIssue() {
        return dateIssue;
    }

    public void setDateIssue(LocalDate dateIssue) {
        this.dateIssue = dateIssue;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    @Override
    public String getHashKey() {
        return null;
    }
}

package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.DocumentType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Table(name = "document", indexes = {
    @Index(name = "i_document_partner_uuid", columnList = "partner_uuid")
})
@DynamicUpdate
@DynamicInsert
@Entity
public class DocumentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_uuid", nullable = false)
    private PartnerEntity partner;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 254)
    private DocumentType type;

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

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

    @Override
    public String getHashKey() {
        return partner.getId().toString();
    }
}

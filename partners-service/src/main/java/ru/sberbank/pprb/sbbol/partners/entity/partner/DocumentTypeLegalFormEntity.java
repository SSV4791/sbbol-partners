package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;

@Table(
    name = "legal_form_document_type",
    indexes = {
        @Index(name = "legal_form_document_type_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_document_type_uuid", columnList = "document_type_uuid")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class DocumentTypeLegalFormEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "legal_form", nullable = false, length = 100)
    private String legalForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_uuid")
    private DocumentTypeEntity documentType;

    public String getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public DocumentTypeEntity getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentTypeEntity documentType) {
        this.documentType = documentType;
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
        DocumentTypeLegalFormEntity that = (DocumentTypeLegalFormEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getDocumentType().getHashKey();
    }
}

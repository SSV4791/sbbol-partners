package ru.sberbank.pprb.sbbol.partners.entity.partner;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table(
    name = "document_type_dictionary",
    indexes = {
        @Index(name = "document_type_dictionary_pkey", columnList = "uuid", unique = true),
        @Index(name = "i_document_type_dictionary_system_name", columnList = "system_name")
    }
)
@DynamicUpdate
@DynamicInsert
@Entity
public class DocumentTypeEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

    @Column(name = "system_name", nullable = false, length = 50)
    private String systemName;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "deleted")
    private Boolean deleted;

    @OneToMany(mappedBy = "documentType", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentTypeLegalFormEntity> legalForms;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public List<DocumentTypeLegalFormEntity> getLegalForms() {
        if (legalForms == null) {
            return new ArrayList<>();
        }
        return legalForms;
    }

    public void setLegalForms(List<DocumentTypeLegalFormEntity> legalForms) {
        this.legalForms = legalForms;
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
        DocumentTypeEntity that = (DocumentTypeEntity) obj;
        if (getUuid() == null || that.getUuid() == null) {
            return false;
        }
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public String getHashKey() {
        return getUuid().toString();
    }
}

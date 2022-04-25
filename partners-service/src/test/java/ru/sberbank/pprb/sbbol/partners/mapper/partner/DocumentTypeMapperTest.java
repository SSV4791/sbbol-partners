package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeCreate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.LEGAL_ENTITY;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.PHYSICAL_PERSON;

public class DocumentTypeMapperTest extends BaseConfiguration {

    private final DocumentTypeMapper mapper = new DocumentTypeMapperImpl(new LegalFormMapperImpl());

    @Test
    @AllureId("34107")
    void testToDocumentTypeEntity() {
        DocumentType expected = factory.manufacturePojo(DocumentType.class);
        DocumentTypeEntity actual = mapper.toDocumentType(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toDocumentType(actual));
    }

    @Test
    @AllureId("36690")
    void testToDocumentTypeCreateEntity() {
        DocumentTypeCreate expected = factory.manufacturePojo(DocumentTypeCreate.class);
        DocumentTypeEntity actual = mapper.toDocumentType(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapper.toDocumentType(actual));
    }

    @Test
    @AllureId("36694")
    void testUpdateDocumentWhenLegalFormsIsNotChanged() {
        DocumentTypeChange expected = factory.manufacturePojo(DocumentTypeChange.class);
        DocumentTypeEntity actual = mapper.toDocumentType(expected);
        mapper.updateDocument(expected, actual);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toDocumentType(actual));
    }

    @Test
    @AllureId("36693")
    void testUpdateDocumentWhenLegalFormsIsDeleted() {
        DocumentTypeChange expected = factory.manufacturePojo(DocumentTypeChange.class);
        expected.setLegalForms(List.of(LEGAL_ENTITY, PHYSICAL_PERSON));
        DocumentTypeEntity actual = mapper.toDocumentType(expected);
        expected.setLegalForms(List.of(LEGAL_ENTITY));
        mapper.updateDocument(expected, actual);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toDocumentType(actual));
    }

    @Test
    @AllureId("36695")
    void testUpdateDocumentWhenLegalFormsIsAdded() {
        DocumentTypeChange expected = factory.manufacturePojo(DocumentTypeChange.class);
        expected.setLegalForms(List.of(LEGAL_ENTITY));
        DocumentTypeEntity actual = mapper.toDocumentType(expected);
        expected.setLegalForms(List.of(LEGAL_ENTITY,  PHYSICAL_PERSON));
        mapper.updateDocument(expected, actual);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toDocumentType(actual));
    }
}

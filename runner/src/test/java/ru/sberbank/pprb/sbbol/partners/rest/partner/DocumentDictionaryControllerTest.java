package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.ENTREPRENEUR;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.PHYSICAL_PERSON;

class DocumentDictionaryControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/dictionary/documents";

    private static final DocumentTypeFilter defaultFilter = new DocumentTypeFilter()
        .deleted(false)
        .pagination(
            new Pagination()
                .offset(0)
                .count(20)
        );

    @Autowired
    private DocumentDictionaryRepository documentDictionaryRepository;
    private DocumentType saveDocument;

    @AfterEach
    void dropEntity() {
        if (isNotEmpty(saveDocument) && isNotEmpty(saveDocument.getDocumentType())) {
            documentDictionaryRepository.deleteById(
                UUID.fromString(saveDocument.getId())
            );
            saveDocument.setDocumentType(null);
        }
    }

    @Test
    void testGetDocumentsWhenLegalFormNotDefined() {
        var response = post(baseRoutePath + "/view", HttpStatus.OK, defaultFilter, DocumentsTypeResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getDocumentType())
            .isNotEmpty();
    }

    @Test
    void testGetDocumentsWhenLegalFormIsMatched() {
        var filter = new DocumentTypeFilter()
            .deleted(false)
            .pagination(new Pagination().offset(0).count(4))
            .legalForms(List.of(ENTREPRENEUR));
        var response = post(baseRoutePath + "/view", HttpStatus.OK, filter, DocumentsTypeResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getDocumentType())
            .isNotEmpty();
    }

    @Test
    void testCreateDocuments() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_CREATE_TYPE2")
            .description("Описание для создания")
            .legalForms(legalForms);
        saveDocument = post(baseRoutePath, HttpStatus.CREATED, documentTypeCreate, DocumentType.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapToDocumentType(documentTypeCreate));
        assertThat(saveDocument.getLegalForms())
            .containsAll(legalForms);
        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, defaultFilter, DocumentsTypeResponse.class);
        assertThat(searchDocument.getDocumentType())
            .contains(saveDocument);
    }

    @Test
    void testCreateDocumentsWhenLegalFormIsEmpty() {
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_CREATE_TYPE_WITH_LEGAL_FORM_IS_EMPTY")
            .description("Описание для создания")
            .legalForms(Collections.emptyList());
        var error = post(baseRoutePath, HttpStatus.BAD_REQUEST, documentTypeCreate, Error.class);
        assertThat(error.getCode())
            .isEqualTo("PPRB:PARTNER:MODEL_VALIDATION_EXCEPTION");
    }

    @Test
    void testCreateDocumentsWithLegalFormIsNull() {
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_CREATE_TYPE_WITH_LEGAL_FORM_IS_EMPTY")
            .description("Описание для создания");
        var error = post(baseRoutePath, HttpStatus.BAD_REQUEST, documentTypeCreate, Error.class);
        assertThat(error.getCode())
            .isEqualTo("PPRB:PARTNER:MODEL_VALIDATION_EXCEPTION");
    }

    @Test
    void testUpdateDocumentsWhenLegalFormNotChange() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_NOT_CHANGE")
            .description("Описание для обновления")
            .legalForms(legalForms);
        saveDocument = post(baseRoutePath, HttpStatus.CREATED, documentTypeCreate, DocumentType.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapToDocumentType(documentTypeCreate));

        var newSaveDocument = saveDocument;
        newSaveDocument.setDocumentType("SUPER_NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_NOT_CHANGE");

        var updateDocument = put(baseRoutePath, HttpStatus.OK, newSaveDocument, DocumentType.class);
        assertThat(updateDocument)
            .isNotNull()
            .isEqualTo(newSaveDocument);
        assertThat(updateDocument.getLegalForms())
            .containsAll(legalForms);

        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, defaultFilter, DocumentsTypeResponse.class);
        assertThat(searchDocument.getDocumentType())
            .contains(updateDocument);
    }

    @Test
    void testUpdateDocumentsWhenLegalFormIsDeleted() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_IS_DELETED")
            .description("Описание для обновления")
            .legalForms(legalForms);
        saveDocument = post(baseRoutePath, HttpStatus.CREATED, documentTypeCreate, DocumentType.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapToDocumentType(documentTypeCreate));

        var newSaveDocument = saveDocument;
        newSaveDocument.setDocumentType("SUPER_NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_IS_DELETED");
        newSaveDocument.getLegalForms().remove(ENTREPRENEUR);

        var updateDocument = put(baseRoutePath, HttpStatus.OK, newSaveDocument, DocumentType.class);
        assertThat(updateDocument)
            .isNotNull()
            .isEqualTo(newSaveDocument);
        assertThat(updateDocument.getLegalForms())
            .isEqualTo(List.of(PHYSICAL_PERSON));

        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, defaultFilter, DocumentsTypeResponse.class);
        assertThat(searchDocument.getDocumentType())
            .contains(updateDocument);
    }

    @Test
    void testUpdateDocumentsWhenLegalFormIsEmpty() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_IS_EMPTY")
            .description("Описание для обновления")
            .legalForms(legalForms);
        saveDocument = post(baseRoutePath, HttpStatus.CREATED, documentTypeCreate, DocumentType.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapToDocumentType(documentTypeCreate));

        var newSaveDocument = saveDocument;
        newSaveDocument.setDocumentType("SUPER_NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_IS_EMPTY");
        newSaveDocument.getLegalForms().clear();

        var updateDocument = put(baseRoutePath, HttpStatus.OK, newSaveDocument, DocumentType.class);
        assertThat(updateDocument)
            .isNotNull();

        assertThat(updateDocument)
            .usingRecursiveComparison()
            .ignoringFields("legalForms")
            .isEqualTo(newSaveDocument);
        assertThat(updateDocument.getLegalForms())
            .containsAll(legalForms);

        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, defaultFilter, DocumentsTypeResponse.class);
        assertThat(searchDocument.getDocumentType())
            .contains(updateDocument);
    }

    @Test
    void testDeleteDocuments() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_DELETE_TYPE")
            .description("Описание для удаления")
            .legalForms(legalForms);
        saveDocument = post(baseRoutePath, HttpStatus.CREATED, documentTypeCreate, DocumentType.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapToDocumentType(documentTypeCreate));

        delete(baseRoutePath, HttpStatus.NO_CONTENT, Map.of("ids", saveDocument.getId()));

        var filter = new DocumentTypeFilter()
            .deleted(true)
            .pagination(new Pagination().offset(0).count(4));
        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, filter, DocumentsTypeResponse.class);
        assertThat(searchDocument.getDocumentType().stream()
            .map(DocumentType::getDocumentType)
            .anyMatch(t -> t.equals(documentTypeCreate.getDocumentType())))
            .isEqualTo(true);
    }

    private DocumentType mapToDocumentType(DocumentTypeCreate documentTypeCreate) {
        return new DocumentType()
            .documentType(documentTypeCreate.getDocumentType())
            .description(documentTypeCreate.getDescription())
            .deleted(false)
            .legalForms(documentTypeCreate.getLegalForms());
    }
}

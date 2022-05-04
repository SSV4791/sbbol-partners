package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.ENTREPRENEUR;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.PHYSICAL_PERSON;

class DocumentDictionaryControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/dictionary/documents";

    private static final DocumentTypeFilter defaultFilter = new DocumentTypeFilter().deleted(false);

    @Test
    @AllureId("36481")
    void testGetDocumentsWhenLegalFormNotDefined() {
        var response = post(baseRoutePath + "/view", HttpStatus.OK, defaultFilter, DocumentsTypeResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getDocumentType())
            .isNotEmpty();
    }

    @Test
    @AllureId("36480")
    void testGetDocumentsWhenLegalFormIsMatched() {
        var filter = new DocumentTypeFilter()
            .deleted(false)
            .legalForms(List.of(ENTREPRENEUR));
        var response = post(baseRoutePath + "/view", HttpStatus.OK, filter, DocumentsTypeResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getDocumentType())
            .isNotEmpty();
    }

    @Test
    @AllureId("36482")
    void testCreateDocuments() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_CREATE_TYPE")
            .description("Описание для создания")
            .legalForms(legalForms);
        var saveDocument = post(baseRoutePath, HttpStatus.CREATED, documentTypeCreate, DocumentTypeResponse.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument.getDocumentType())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapToDocumentType(documentTypeCreate));
        assertThat(saveDocument.getDocumentType().getLegalForms())
            .containsAll(legalForms);
        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, defaultFilter, DocumentsTypeResponse.class);
        assertThat(searchDocument.getDocumentType())
            .contains(saveDocument.getDocumentType());
    }

    @Test
    @AllureId("34140")
    void testCreateDocumentsWhenLegalFormIsDuplicated() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON, ENTREPRENEUR);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_CREATE_TYPE_WHEN_LEGAL_FORM_IS_DUPLICATED")
            .description("Описание для создания")
            .legalForms(legalForms);
        var error = post(baseRoutePath, HttpStatus.BAD_REQUEST, documentTypeCreate, Error.class);
        assertThat(error.getCode())
            .isEqualTo("BAD_REQUEST");
    }

    @Test
    @AllureId("36484")
    void testCreateDocumentsWhenLegalFormIsEmpty() {
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_CREATE_TYPE_WITH_LEGAL_FORM_IS_EMPTY")
            .description("Описание для создания")
            .legalForms(Collections.emptyList());
        var error = post(baseRoutePath, HttpStatus.BAD_REQUEST, documentTypeCreate, Error.class);
        assertThat(error.getCode())
            .isEqualTo("BAD_REQUEST");
    }

    @Test
    @AllureId("36485")
    void testCreateDocumentsWithLegalFormIsNull() {
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_CREATE_TYPE_WITH_LEGAL_FORM_IS_EMPTY")
            .description("Описание для создания");
        var error = post(baseRoutePath, HttpStatus.BAD_REQUEST, documentTypeCreate, Error.class);
        assertThat(error.getCode())
            .isEqualTo("BAD_REQUEST");
    }

    @Test
    @AllureId("36478")
    void testUpdateDocumentsWhenLegalFormNotChange() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_NOT_CHANGE")
            .description("Описание для обновления")
            .legalForms(legalForms);
        var saveDocument = post(baseRoutePath, HttpStatus.CREATED, documentTypeCreate, DocumentTypeResponse.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument.getDocumentType())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapToDocumentType(documentTypeCreate));

        var newSaveDocument = saveDocument.getDocumentType();
        newSaveDocument.setDocumentType("SUPER_NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_NOT_CHANGE");

        var updateDocument = put(baseRoutePath, HttpStatus.OK, newSaveDocument, DocumentTypeResponse.class);
        assertThat(updateDocument)
            .isNotNull();

        assertThat(updateDocument.getDocumentType())
            .isEqualTo(newSaveDocument);
        assertThat(updateDocument.getDocumentType().getLegalForms())
            .containsAll(legalForms);

        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, defaultFilter, DocumentsTypeResponse.class);
        assertThat(searchDocument.getDocumentType())
            .contains(updateDocument.getDocumentType());
    }

    @Test
    @AllureId("36486")
    void testUpdateDocumentsWhenLegalFormIsDeleted() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_IS_DELETED")
            .description("Описание для обновления")
            .legalForms(legalForms);
        var saveDocument = post(baseRoutePath, HttpStatus.CREATED, documentTypeCreate, DocumentTypeResponse.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument.getDocumentType())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapToDocumentType(documentTypeCreate));

        var newSaveDocument = saveDocument.getDocumentType();
        newSaveDocument.setDocumentType("SUPER_NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_IS_DELETED");
        newSaveDocument.getLegalForms().remove(ENTREPRENEUR);

        var updateDocument = put(baseRoutePath, HttpStatus.OK, newSaveDocument, DocumentTypeResponse.class);
        assertThat(updateDocument)
            .isNotNull();

        assertThat(updateDocument.getDocumentType())
            .isEqualTo(newSaveDocument);
        assertThat(updateDocument.getDocumentType().getLegalForms())
            .isEqualTo(List.of(PHYSICAL_PERSON));

        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, defaultFilter, DocumentsTypeResponse.class);
        assertThat(searchDocument.getDocumentType())
            .contains(updateDocument.getDocumentType());
    }

    @Test
    @AllureId("36479")
    void testUpdateDocumentsWhenLegalFormIsEmpty() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_IS_EMPTY")
            .description("Описание для обновления")
            .legalForms(legalForms);
        var saveDocument = post(baseRoutePath, HttpStatus.CREATED, documentTypeCreate, DocumentTypeResponse.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument.getDocumentType())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapToDocumentType(documentTypeCreate));

        var newSaveDocument = saveDocument.getDocumentType();
        newSaveDocument.setDocumentType("SUPER_NEW_UPDATE_TYPE_WHEN_LEGAL_FORM_IS_EMPTY");
        newSaveDocument.getLegalForms().clear();

        var updateDocument = put(baseRoutePath, HttpStatus.OK, newSaveDocument, DocumentTypeResponse.class);
        assertThat(updateDocument)
            .isNotNull();

        assertThat(updateDocument.getDocumentType())
            .usingRecursiveComparison()
            .ignoringFields("legalForms")
            .isEqualTo(newSaveDocument);
        assertThat(updateDocument.getDocumentType().getLegalForms())
            .containsAll(legalForms);

        var searchDocument = post(baseRoutePath + "/view", HttpStatus.OK, defaultFilter, DocumentsTypeResponse.class);
        assertThat(searchDocument.getDocumentType())
            .contains(updateDocument.getDocumentType());
    }

    @Test
    @AllureId("36345")
    void testDeleteDocuments() {
        var legalForms = List.of(ENTREPRENEUR, PHYSICAL_PERSON);
        var documentTypeCreate = new DocumentTypeCreate()
            .documentType("NEW_DELETE_TYPE")
            .description("Описание для удаления")
            .legalForms(legalForms);
        var saveDocument = post(baseRoutePath, HttpStatus.CREATED, documentTypeCreate, DocumentTypeResponse.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument.getDocumentType())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(mapToDocumentType(documentTypeCreate));

        delete(baseRoutePath + "/{id}", HttpStatus.NO_CONTENT, saveDocument.getDocumentType().getId());

        var filter = new DocumentTypeFilter().deleted(true);
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

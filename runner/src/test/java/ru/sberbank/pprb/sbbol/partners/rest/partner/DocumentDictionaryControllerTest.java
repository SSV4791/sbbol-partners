package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentDictionaryControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/dictionary/documents";

    @Test
    @AllureId("34167")
    void testGetDocuments() {
        var response = get(baseRoutePath + "/{status}", HttpStatus.OK, DocumentsTypeResponse.class, false);
        assertThat(response)
            .isNotNull();
    }

    @Test
    @AllureId("34140")
    void testCreateDocuments() {
        var documentType = new DocumentType()
            .deleted(false)
            .documentType("NEW_CREATE_TYPE")
            .description("Описание для создания");
        var saveDocument =
            post(
                baseRoutePath,
                HttpStatus.CREATED,
                documentType,
                DocumentTypeResponse.class
            );
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument.getDocumentType())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(documentType);
        var searchDocument = get(baseRoutePath + "/{status}", HttpStatus.OK, DocumentsTypeResponse.class, false);
        assertThat(searchDocument.getDocumentType())
            .contains(saveDocument.getDocumentType());
    }

    @Test
    @AllureId("34168")
    void testUpdateDocuments() {
        var documentType = new DocumentType()
            .id(UUID.randomUUID().toString())
            .deleted(false)
            .documentType("NEW_UPDATE_TYPE")
            .description("Описание для обновления");
        var saveDocument =
            post(
                baseRoutePath,
                HttpStatus.CREATED,
                documentType,
                DocumentTypeResponse.class
            );
        assertThat(saveDocument)
            .isNotNull();

        var newSaveDocument = saveDocument.getDocumentType();
        newSaveDocument.setDocumentType("SUPER_NEW_UPDATE_TYPE");

        var updateDocument = put(baseRoutePath, HttpStatus.OK, newSaveDocument, DocumentTypeResponse.class);
        assertThat(updateDocument)
            .isNotNull();

        assertThat(updateDocument.getDocumentType())
            .isEqualTo(newSaveDocument);

        var searchDocument = get(baseRoutePath + "/{status}", HttpStatus.OK, DocumentsTypeResponse.class, false);
        assertThat(searchDocument.getDocumentType())
            .contains(updateDocument.getDocumentType());
    }
}

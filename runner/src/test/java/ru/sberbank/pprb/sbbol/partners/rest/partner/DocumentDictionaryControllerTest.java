package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentDictionaryControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/dictionary/documents";

    @Test
    void testGetDocuments() {
        var response = get(baseRoutePath + "/{status}", DocumentsTypeResponse.class, false);
        assertThat(response)
            .isNotNull();
    }

    @Test
    void testCreateDocuments() {
        var documentType = new DocumentType()
            .id(UUID.randomUUID().toString())
            .deleted(false)
            .documentType("NEW_CREATE_TYPE")
            .description("Описание для создания");
        var saveDocument = createPost(baseRoutePath, documentType, DocumentTypeResponse.class);
        assertThat(saveDocument)
            .isNotNull();
        assertThat(saveDocument.getDocumentType())
            .isEqualTo(documentType);
        var searchDocument = get(baseRoutePath + "/{status}", DocumentsTypeResponse.class, false);
        assertThat(searchDocument.getDocumentType())
            .contains(saveDocument.getDocumentType());

    }

    @Test
    void testUpdateDocuments() {
        var documentType = new DocumentType()
            .id(UUID.randomUUID().toString())
            .deleted(false)
            .documentType("NEW_UPDATE_TYPE")
            .description("Описание для обновления");
        var saveDocument = createPost(baseRoutePath, documentType, DocumentTypeResponse.class);
        assertThat(saveDocument)
            .isNotNull();

        var newSaveDocument = saveDocument.getDocumentType();
        newSaveDocument.setDocumentType("SUPER_NEW_UPDATE_TYPE");

        var updateDocument = put(baseRoutePath, newSaveDocument, DocumentTypeResponse.class);
        assertThat(updateDocument)
            .isNotNull();

        assertThat(updateDocument.getDocumentType())
            .isEqualTo(newSaveDocument);

        var searchDocument = get(baseRoutePath + "/{status}", DocumentsTypeResponse.class, false);
        assertThat(searchDocument.getDocumentType())
            .contains(updateDocument.getDocumentType());
    }
}

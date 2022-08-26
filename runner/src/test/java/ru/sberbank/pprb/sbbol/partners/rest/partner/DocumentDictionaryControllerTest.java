package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.ENTREPRENEUR;

class DocumentDictionaryControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/dictionary/documents";
    private static final DocumentTypeFilter defaultFilter = new DocumentTypeFilter()
        .deleted(false)
        .pagination(
            new Pagination()
                .offset(0)
                .count(20)
        );

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
}

package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

import static io.qameta.allure.Allure.step;
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
    @DisplayName("POST /dictionary/documents/view")
    void testGetDocumentsWhenLegalFormNotDefined() {
        var response = step("Выполнение post-запроса /dictionary/documents/view", () ->
            post(baseRoutePath + "/view",
                HttpStatus.OK,
                defaultFilter,
                DocumentsTypeResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getDocumentType())
                .isNotEmpty();
        });
    }

    @Test
    @DisplayName("POST /dictionary/documents/view + legalForms - ENTREPRENEUR")
    void testGetDocumentsWhenLegalFormIsMatched() {
        var filter = step("Подготовка тестовых данных", () ->
            new DocumentTypeFilter()
                .deleted(false)
                .pagination(new Pagination().offset(0).count(4))
                .legalForms(List.of(ENTREPRENEUR)));

        var response = step("Выполнение post-запроса /dictionary/documents/view", () ->
            post(
                baseRoutePath + "/view",
                HttpStatus.OK,
                filter,
                DocumentsTypeResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getDocumentType())
                .isNotEmpty();
        });
    }
}

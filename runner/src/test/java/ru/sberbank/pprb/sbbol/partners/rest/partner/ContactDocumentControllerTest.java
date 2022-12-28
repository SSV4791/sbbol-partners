package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.CertifierType;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactDocumentControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact";

    @Test
    @DisplayName("GET /partner/contact/documents/{digitalId}/{id} получение документа по идентификатору")
    void testGetContactDocument() {
        var document = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidContactDocument(contact.getId(), contact.getDigitalId());
        });
        var actualDocument = Allure.step("Выполнение get-запроса /partner/contact/documents/{digitalId}/{id}, код ответа 200", () ->
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Document.class,
                document.getDigitalId(), document.getId()
            ));
        Allure.step("Проверка корректности ответа", () -> assertThat(actualDocument)
            .isNotNull()
            .isEqualTo(document));
    }

    @Test
    @DisplayName("POST /partner/contact/documents/view получение списка документов")
    void testViewContactDocument() {
        Contact contact = Allure.step("Подготовка тестовых данных", () -> {
            Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            Contact partnerContact = createValidContact(partner.getId(), partner.getDigitalId());
            createValidContactDocument(partnerContact.getId(), partnerContact.getDigitalId());
            createValidContactDocument(partnerContact.getId(), partnerContact.getDigitalId());
            createValidContactDocument(partnerContact.getId(), partnerContact.getDigitalId());
            createValidContactDocument(partnerContact.getId(), partnerContact.getDigitalId());
            createValidContactDocument(partnerContact.getId(), partnerContact.getDigitalId());
            return partnerContact;
        });

        DocumentsFilter filter1 = Allure.step("Формирование списка документов", () -> new DocumentsFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            )
            .pagination(new Pagination()
                .count(4)
                .offset(0)));
        var response1 = Allure.step("Выполнение post-запроса /partner/contact/documents/view, код ответа 200", () -> post(
            baseRoutePath + "/documents/view",
            HttpStatus.OK,
            filter1,
            DocumentsResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response1)
                .isNotNull();
            assertThat(response1.getDocuments().size())
                .isEqualTo(4);
        });

        DocumentsFilter filter2 = Allure.step("Формирование списка документов (тип документа = паспорт РФ)", () -> new DocumentsFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(List.of(contact.getId()))
            .documentType("PASSPORT_OF_RUSSIA")
            .pagination(new Pagination()
                .count(4)
                .offset(0)));
        var response2 = Allure.step("Выполнение post-запроса /partner/contact/documents/view, код ответа 200", () -> post(
            baseRoutePath + "/documents/view",
            HttpStatus.OK,
            filter2,
            DocumentsResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response2)
                .isNotNull();
            assertThat(response2.getDocuments().size())
                .isEqualTo(4);
            assertThat(response2.getPagination().getHasNextPage())
                .isEqualTo(Boolean.TRUE);
        });
    }

    @Test
    @DisplayName("POST /partner/contact/documents/view с пустым телом запроса")
    void testViewContactDocumentWithEmptyBodyResponse() {
        DocumentsFilter filter = Allure.step("Подготовка тестовых данных", () -> new DocumentsFilter());
        var response = Allure.step("Выполнение post-запроса /partner/contact/documents/view, код ответа 400", () -> post (
            baseRoutePath + "/documents/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            List<Descriptions> errorTexts = List.of(
                new Descriptions()
                    .field("pagination")
                    .message(
                        List.of("Поле обязательно для заполнения")
                    ),
                new Descriptions()
                    .field("digitalId")
                    .message(
                        List.of("Поле обязательно для заполнения", "Поле обязательно для заполнения")
                    )
            );
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            for (var text : response.getDescriptions()) {
                assertThat(errorTexts.contains(text)).isTrue();
            }
        });
    }

    @Test
    @DisplayName("POST /partner/contact/documents/view негативные проверки digitalId")
    void negativeTestViewContactDocumentWithDigitalId() {
        DocumentsFilter filter = Allure.step("Формирование списка документов (digitalId равен null)", () -> new DocumentsFilter()
            .digitalId(null)
            .pagination(new Pagination()
                .count(4)
                .offset(0)));
        var response = Allure.step("Выполнение post-запроса /partner/contact/documents/view (digitalId равен null), код ответа 400", () -> post (
            baseRoutePath + "/documents/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            Optional<Descriptions> description = response.getDescriptions().stream()
                .filter(value -> value.getField().equals("digitalId")).findFirst();
            assertThat(description.isPresent()).isTrue();
            assertThat(description.get().getMessage()).contains("Поле обязательно для заполнения");
        });

        DocumentsFilter filter2 = Allure.step("Формирование списка документов (digitalId пуст)", () -> new DocumentsFilter()
            .digitalId("")
            .unifiedIds(List.of(""))
            .pagination(new Pagination()
                .count(4)
                .offset(0)));
        var response2 = Allure.step("Выполнение post-запроса /partner/contact/documents/view (digitalId пуст), код ответа 400", () -> post (
            baseRoutePath + "/documents/view",
            HttpStatus.BAD_REQUEST,
            filter2,
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response2)
                .isNotNull();
            assertThat(response2.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            Optional<Descriptions> description2 = response2.getDescriptions().stream()
                .filter(value -> value.getField().equals("digitalId")).findFirst();
            assertThat(description2.isPresent()).isTrue();
            assertThat(description2.get().getMessage()).contains("Поле обязательно для заполнения");
        });
    }

    @Test
    @DisplayName("POST /partner/contact/documents/view негативные проверки pagination")
    void testTest() {
        Partner partner = Allure.step("Подготовка тестовых данных", () -> {
            Partner partnerContact = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            return partnerContact;
        });

        DocumentsFilter filter = Allure.step("Подготовка фильтра (pagination равен null)", () -> new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .pagination(null));
        var response = Allure.step("Выполнение post-запроса /partner/contact/documents/view (pagination равен null), код ответа 400", () -> post (
            baseRoutePath + "/documents/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            Optional<Descriptions> description = response.getDescriptions().stream()
                .filter(value -> value.getField().equals("pagination")).findFirst();
            assertThat(description.isPresent()).isTrue();
            assertThat(description.get().getMessage()).contains("Поле обязательно для заполнения");
        });

        DocumentsFilter filter2 = Allure.step("Подготовка фильтра (count отсутсвует в теле запроса)", () -> new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .pagination(new Pagination()
                .offset(0)));
        var response2 = Allure.step("Выполнение post-запроса /partner/contact/documents/view (count отсутсвует в теле запроса), код ответа 400", () -> post (
            baseRoutePath + "/documents/view",
            HttpStatus.BAD_REQUEST,
            filter2,
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response2)
                .isNotNull();
            assertThat(response2.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            Optional<Descriptions> description2 = response2.getDescriptions().stream()
                .filter(value -> value.getField().equals("pagination.count")).findFirst();
            assertThat(description2.isPresent()).isTrue();
            assertThat(description2.get().getMessage()).contains("Поле обязательно для заполнения");
        });

        DocumentsFilter filter3 = Allure.step("Подготовка фильтра (offset отсутсвует в теле запроса)", () -> new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .pagination(new Pagination()
                .count(4)));
        var response3 = Allure.step("Выполнение post-запроса /partner/contact/documents/view (offset отсутсвует в теле запроса), код ответа 400", () -> post (
            baseRoutePath + "/documents/view",
            HttpStatus.BAD_REQUEST,
            filter3,
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response3)
                .isNotNull();
            assertThat(response3.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            Optional<Descriptions> description3 = response3.getDescriptions().stream()
                .filter(value -> value.getField().equals("pagination.offset")).findFirst();
            assertThat(description3.isPresent()).isTrue();
            assertThat(description3.get().getMessage()).contains("Поле обязательно для заполнения");
        });
    }

    @Test
    @DisplayName("POST /partner/contact/document создание документа")
    void testCreateContactDocument() {
        var expected = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return getValidContactDocument(contact.getId(), contact.getDigitalId());
        });
        var document = Allure.step("Выполнение post-запроса /partner/contact/document, код ответа 201", () ->
            createValidContactDocument(expected));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(document)
                .usingRecursiveComparison()
                .ignoringFields(
                    "id",
                    "version",
                    "documentType")
                .isEqualTo(expected);
        });
    }

    @Test
    @DisplayName("POST /partner/contact/document создание документа без реквизитова")
    void testCreateDocumentWithoutContact() {
        Partner partner = Allure.step("Подготовка тестовых данных", () -> createValidPartner(RandomStringUtils.randomAlphabetic(10)));
        var response1 = Allure.step("Выполнение post-запроса /partner/contact/document, код ответа 404", () -> post(
            baseRoutePath + "/document",
            HttpStatus.NOT_FOUND,
            getValidContactDocument(partner.getId(), partner.getDigitalId()),
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response1)
                .isNotNull();
            assertThat(response1.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("PUT /partner/contact/document редактирование документа")
    void testUpdateContactDocument() {
        var document = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidContactDocument(contact.getId(), contact.getDigitalId());
        });
        var newUpdateDocument = Allure.step("Выполнение put-запроса /partner/contact/document, код ответа 200", () ->
            put(baseRoutePath + "/document", HttpStatus.OK, updateDocument(document), Document.class));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(newUpdateDocument)
                .isNotNull();
            assertThat(newUpdateDocument.getNumber())
                .isEqualTo(newUpdateDocument.getNumber());
        });
    }

    @Test
    @DisplayName("PUT /partner/contact/document редактирование удаленного документа")
    void testUpdateNotFoundContactDocument() {
        var document = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidContactDocument(contact.getId(), contact.getDigitalId());
        });
        Allure.step("Выполнение delete-запроса /partner/contact/documents/{digitalId}, код ответа 204", () -> delete(
            baseRoutePath + "/documents" + "/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("ids", document.getId()),
            document.getDigitalId()
        ));
        var updateDocument = Allure.step("Выполнение put-запроса /partner/contact/document, код ответа 404", () -> put(
            baseRoutePath + "/document",
            HttpStatus.NOT_FOUND,
            updateDocument(document),
            Error.class));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(updateDocument)
                .isNotNull();
            assertThat(updateDocument.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(updateDocument.getMessage())
                .isEqualTo("Искомая сущность document с id: "+ document.getId() + ", digitalId: " + document.getDigitalId() + " не найдена");
        });
    }

    @Test
    @DisplayName("PUT /partner/contact/document редактирование документа с невалидной версией")
    void negativeTestUpdateDocumentVersion() {
        var documentWithNewVersion = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            var document = createValidContactDocument(contact.getId(), contact.getDigitalId());
            Long version = document.getVersion() + 1;
            document.setVersion(version);
            return document;
        });
        var documentError = Allure.step("Выполнение put-запроса /partner/contact/document, код ответа 400", () -> put(
            baseRoutePath + "/document",
            HttpStatus.BAD_REQUEST,
            updateDocument(documentWithNewVersion),
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(documentError.getCode())
                .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
            assertThat(documentError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
                .contains("Версия записи в базе данных " + (documentWithNewVersion.getVersion() - 1) +
                    " не равна версии записи в запросе version=" + documentWithNewVersion.getVersion());
        });
    }

    @Test
    @DisplayName("PUT /partner/contact/document редактирование документа с валидной версией")
    void positiveTestUpdateDocumentVersion() {
        var document = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidContactDocument(contact.getId(), contact.getDigitalId());
        });
        var documentUpdate = Allure.step("Выполнение put-запроса /partner/contact/document, код ответа 200", () -> put(
            baseRoutePath + "/document",
            HttpStatus.OK,
            updateDocument(document),
            Document.class
        ));
        var checkDocument = Allure.step("Выполнение get-запроса /partner/contact/documents/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Document.class,
            documentUpdate.getDigitalId(), documentUpdate.getId()));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(checkDocument)
                .isNotNull();
            assertThat(checkDocument.getVersion())
                .isEqualTo(document.getVersion() + 1);
        });
    }

    @Test
    @DisplayName("DELETE /partner/contact/documents/{digitalId} удаление документа")
    void testDeleteContactDocument() {
        var document = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidContactDocument(contact.getId(), contact.getDigitalId());
        });
        var actualDocument = Allure.step("Выполнение get-запроса /partner/contact/documents/{digitalId}/{id}, код ответа 200", () ->
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Document.class,
                document.getDigitalId(), document.getId()
            ));
        Allure.step("Проверка корректности ответа", () -> assertThat(actualDocument)
            .isNotNull()
            .isEqualTo(document));

        Allure.step("Выполнение delete-запроса /partner/contact/documents/{digitalId}, код ответа 204", () -> {
            var deleteDocument =
                delete(
                    baseRoutePath + "/documents" + "/{digitalId}",
                    HttpStatus.NO_CONTENT,
                    Map.of("ids", actualDocument.getId()),
                    actualDocument.getDigitalId()
                ).getBody();
            assertThat(deleteDocument)
                .isNotNull();
        });

        var searchDocument = Allure.step("Выполнение get-запроса /partner/contact/documents/{digitalId}/{id} (повторный поиск), код ответа 404", () ->
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                HttpStatus.NOT_FOUND,
                Error.class,
                document.getDigitalId(), document.getId()
            ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(searchDocument)
                .isNotNull();

            assertThat(searchDocument.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("DELETE /partner/contact/documents/{digitalId} удаление не найденного документа")
    void negativeTestDeleteContactDocument() {
        var document = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createValidContactDocument(contact.getId(), contact.getDigitalId());
        });
        Allure.step("Выполнение delete-запроса /partner/contact/documents/{digitalId}(удаление документа), код ответа 204", () -> delete(
                baseRoutePath + "/documents" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", document.getId()),
                document.getDigitalId()
            ));
        var searchDocument = Allure.step("Выполнение get-запроса /partner/contact/documents/{digitalId}/{id}, код ответа 404", () ->
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                HttpStatus.NOT_FOUND,
                Error.class,
                document.getDigitalId(), document.getId()
            ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(searchDocument)
                .isNotNull();

            assertThat(searchDocument.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
        Allure.step("Выполнение delete-запроса /partner/contact/documents/{digitalId}(повторное удаление документа), код ответа 404", () -> delete(
            baseRoutePath + "/documents" + "/{digitalId}",
            HttpStatus.NOT_FOUND,
            Map.of("ids", document.getId()),
            document.getDigitalId()
        ).then()
            .body("message", equalTo("Искомая сущность document с id: " + document.getId() + ", digitalId: " + document.getDigitalId() + " не найдена")));

    }

    public static DocumentCreate getValidContactDocument(String partnerUuid, String digitalId) {
        return new DocumentCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .certifierName("Имя")
            .certifierType(CertifierType.NOTARY)
            .dateIssue(LocalDate.now())
            .divisionCode("1111")
            .number("23")
            .documentTypeId("3422aec8-7f44-4089-9a43-f8e3c5b00722");
    }

    private static Document createValidContactDocument(String contactUuid, String digitalId) {
        return Allure.step("Создание валидного документа", () -> post(
            baseRoutePath + "/document",
            HttpStatus.CREATED,
            getValidContactDocument(contactUuid, digitalId),
            Document.class
        ));
    }

    private static Document createValidContactDocument(DocumentCreate document) {
        return Allure.step("Создание валидного документа", () -> post(
            baseRoutePath + "/document",
            HttpStatus.CREATED,
            document,
            Document.class
        ));
    }

    public static DocumentChange updateDocument(Document document) {
        return Allure.step("Редактирование документа", () -> new DocumentChange()
            .number(randomAlphanumeric(5))
            .id(document.getId())
            .version(document.getVersion())
            .unifiedId(document.getUnifiedId())
            .digitalId(document.getDigitalId())
            .certifierName(document.getCertifierName())
            .certifierType(document.getCertifierType())
            .dateIssue(document.getDateIssue())
            .divisionCode(document.getDivisionCode())
            .documentTypeId(document.getDocumentType().getId()));
    }
}

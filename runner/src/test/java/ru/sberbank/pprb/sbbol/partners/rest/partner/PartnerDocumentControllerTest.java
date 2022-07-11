package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.CertifierType;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class PartnerDocumentControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @AllureId("34184")
    void testCreatePartnerDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var expected = getValidPartnerDocument(partner.getId(), partner.getDigitalId());
        var document = createValidPartnerDocument(expected);
        assertThat(document)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version",
                "documentType"
            )
            .isEqualTo(expected);
    }

    @Test
    void testCreateEmptyPartnerDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var expected = getEmptyPartnerDocument(partner.getId(), partner.getDigitalId());
        var document = createValidPartnerDocument(expected);
        assertThat(document)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version",
                "documentType"
            )
            .isEqualTo(expected);
    }

    @Test
    void testCreatePartnerDocumentWithoutDigitalId() {
        var errorText ="Ошибка заполнения одного из возможных полей: digitalId поле обязательно для заполнения";
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var response = createPartnerDocumentWithErrors(partner.getId(), null);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(response.getDescriptionErrors().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains(errorText);
    }

    @Test
    void testCreatePartnerDocumentWithEmptyDigitalId() {
        var errorText ="Ошибка заполнения одного из возможных полей: digitalId поле обязательно для заполнения";
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var response = createPartnerDocumentWithErrors(partner.getId(), "");
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(response.getDescriptionErrors().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains(errorText);
    }

    @Test
    void testCreatePartnerDocumentWithBadDigitalId() {
        var errorText ="Проверьте заполненное значение на корректность. Максимальное количество символов 1-40";
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var response = createPartnerDocumentWithErrors(partner.getId(), RandomStringUtils.randomAlphanumeric(41));
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(response.getDescriptionErrors().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains(errorText);
    }

    @Test
    void testCreatePartnerDocumentWithoutUnifiedId() {
        var errorText ="Ошибка заполнения одного из возможных полей: id/partnerId/accountId/unifiedId поле обязательно для заполнения";
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var response = createPartnerDocumentWithErrors(null, partner.getDigitalId());
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(response.getDescriptionErrors().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains(errorText);
    }

    @Test
    void testCreatePartnerDocumentWithEmptyUnifiedId() {
        var errorText ="Ошибка заполнения одного из возможных полей: id/partnerId/accountId/unifiedId поле обязательно для заполнения";
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var response = createPartnerDocumentWithErrors("" ,partner.getDigitalId());
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(response.getDescriptionErrors().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains(errorText);
    }

    @Test
    void testCreatePartnerDocumentWithBadUnifiedId() {
        var errorText ="Проверьте заполненное значение на корректность. Максимальное количество символов 36";
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var response = createPartnerDocumentWithErrors(RandomStringUtils.randomAlphanumeric(37) ,partner.getDigitalId());
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(response.getDescriptionErrors().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains(errorText);
    }

    @Test
    void testCreatePartnerDocumentWithBadRequest() {
        List<Descriptions> errorTexts = List.of(
            new Descriptions()
                .field("positionCertifier")
                .message(
                    List.of("Проверьте заполненное значение на корректность. Максимальное количество символов 100")
                ),
            new Descriptions()
                .field("number")
                .message(
                    List.of("Проверьте заполненное значение на корректность. Максимальное количество символов 50")
                ),
            new Descriptions()
                .field("divisionCode")
                .message(
                    List.of("Проверьте заполненное значение на корректность. Максимальное количество символов 50")
                ),
            new Descriptions()
                .field("series")
                .message(
                    List.of("Проверьте заполненное значение на корректность. Максимальное количество символов 50")
                ),
            new Descriptions()
                .field("divisionIssue")
                .message(
                    List.of("Проверьте заполненное значение на корректность. Максимальное количество символов 250")
                ),
            new Descriptions()
                .field("certifierName")
                .message(
                    List.of("Проверьте заполненное значение на корректность. Максимальное количество символов 100")
                )
        );

        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var partnerDocument = getValidPartnerDocument(partner.getId(), partner.getDigitalId())
            .certifierName(RandomStringUtils.randomAlphabetic(101))
            .series(RandomStringUtils.randomAlphanumeric(51))
            .number(RandomStringUtils.randomAlphanumeric(51))
            .divisionIssue(RandomStringUtils.randomAlphabetic(251))
            .divisionCode(RandomStringUtils.randomAlphanumeric(51))
            .positionCertifier(RandomStringUtils.randomAlphabetic(101));
        var response = createPartnerWithErrors(partnerDocument);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        for (var text : errorTexts) {
            assertThat(errorTexts.contains(text)).isTrue();
        }
    }

    @Test
    @AllureId("34115")
    void testGetPartnerDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var document = createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        var actualDocument =
            get(
                baseRoutePath + "/document" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Document.class,
                document.getDigitalId(), document.getId()
            );
        assertThat(actualDocument)
            .isNotNull();
        assertThat(actualDocument)
            .isNotNull()
            .isEqualTo(document);
    }

    @Test
    @AllureId("34113")
    void testViewPartnerDocumentWithExpectedQuantity() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());

        var filterForQuantity = new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(List.of(partner.getId()))
            .pagination(new Pagination()
                .count(2)
                .offset(0));
        var responseForQuantity = post(
            baseRoutePath + "/documents/view",
            HttpStatus.OK,
            filterForQuantity,
            DocumentsResponse.class
        );
        assertThat(responseForQuantity)
            .isNotNull();
        assertThat(responseForQuantity.getDocuments().size())
            .isEqualTo(2);
    }

    @Test
    void testViewPartnerDocumentWithEmptyList() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));

        var filterForQuantity = new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(List.of(partner.getId()))
            .pagination(new Pagination()
                .count(2)
                .offset(0));
        var responseForQuantity = post(
            baseRoutePath + "/documents/view",
            HttpStatus.OK,
            filterForQuantity,
            DocumentsResponse.class
        );
        assertThat(responseForQuantity)
            .isNotNull();
        assertThat(responseForQuantity.getDocuments())
            .isEqualTo(null);
    }

    @Test
    void testViewPartnerDocumentWithExpectedDocumentType() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());

        var filterForDocumentType = new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(List.of(partner.getId()))
            .documentType("SEAMAN_PASSPORT")
            .pagination(new Pagination()
                .count(2)
                .offset(0));
        var responseForDocumentType = post(
            baseRoutePath + "/documents/view",
            HttpStatus.OK,
            filterForDocumentType,
            DocumentsResponse.class
        );
        assertThat(responseForDocumentType)
            .isNotNull();
        assertThat(responseForDocumentType.getDocuments().size())
            .isEqualTo(2);
        assertThat(responseForDocumentType.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }

    @Test
    void testViewPartnerDocumentWithNotExpectedDocumentType() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());

        var filterForDocumentType = new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(List.of(partner.getId()))
            .documentType("PASSPORT_OF_RUSSIA")
            .pagination(new Pagination()
                .count(2)
                .offset(0));
        var responseForDocumentType = post(
            baseRoutePath + "/documents/view",
            HttpStatus.OK,
            filterForDocumentType,
            DocumentsResponse.class
        );
        assertThat(responseForDocumentType)
            .isNotNull();
        assertThat(responseForDocumentType.getDocuments())
            .isEqualTo(null);
    }

    @Test
    @AllureId("34145")
    void testUpdatePartnerDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var document = createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        String newName = "Новое номер";
        var updateDocument = new DocumentChange();
        updateDocument.id(document.getId());
        updateDocument.digitalId(document.getDigitalId());
        updateDocument.unifiedId(document.getUnifiedId());
        updateDocument.number(newName);
        updateDocument.setVersion(document.getVersion());
        Document newUpdateDocument = put(baseRoutePath + "/document", HttpStatus.OK, updateDocument, Document.class);

        assertThat(newUpdateDocument)
            .isNotNull();
        assertThat(newUpdateDocument.getNumber())
            .isEqualTo(newName);
    }

    @Test
    @AllureId("36945")
    void negativeTestUpdateDocumentVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var document = createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        Long version = document.getVersion() + 1;
        document.setVersion(version);
        var documentError = put(
            baseRoutePath + "/document",
            HttpStatus.BAD_REQUEST,
            updateDocument(document),
            Error.class
        );
        assertThat(documentError.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(documentError.getDescriptionErrors().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains("Версия записи в базе данных " + (document.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    @AllureId("36946")
    void positiveTestUpdateDocumentVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var document = createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        var documentUpdate = put(
            baseRoutePath + "/document",
            HttpStatus.OK,
            updateDocument(document),
            Document.class
        );
        var checkDocument = get(
            baseRoutePath + "/document" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Document.class,
            documentUpdate.getDigitalId(), documentUpdate.getId());
        assertThat(checkDocument)
            .isNotNull();
        assertThat(checkDocument.getVersion())
            .isEqualTo(document.getVersion() + 1);
    }

    @Test
    @AllureId("34134")
    void testDeletePartnerDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var document = createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        var actualDocument =
            get(
                baseRoutePath + "/document" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Document.class,
                document.getDigitalId(), document.getId()
            );
        assertThat(actualDocument)
            .isNotNull();

        assertThat(actualDocument)
            .isNotNull()
            .isEqualTo(document);

        var deleteDocument =
            delete(
                baseRoutePath + "/document" + "/{digitalId}" + "/{id}",
                HttpStatus.NO_CONTENT,
                actualDocument.getDigitalId(), actualDocument.getId()
            ).getBody();
        assertThat(deleteDocument)
            .isNotNull();

        var searchDocument =
            get(
                baseRoutePath + "/document" + "/{digitalId}" + "/{id}",
                HttpStatus.NOT_FOUND,
                Error.class,
                document.getDigitalId(), document.getId()
            );

        assertThat(searchDocument)
            .isNotNull();

        assertThat(searchDocument.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    private static Document createValidPartnerDocument(String partnerUuid, String digitalId) {
        var response = post(baseRoutePath + "/document", HttpStatus.CREATED, getValidPartnerDocument(partnerUuid, digitalId), Document.class);
        assertThat(response)
            .isNotNull();
        return response;
    }

    private static Document createValidPartnerDocument(DocumentCreate document) {
        var response = post(baseRoutePath + "/document", HttpStatus.CREATED, document, Document.class);
        assertThat(response)
            .isNotNull();
        return response;
    }

    private static Error createPartnerDocumentWithErrors(String partnerUuid, String digitalId) {
        var response = post(baseRoutePath + "/document", HttpStatus.BAD_REQUEST, getValidPartnerDocument(partnerUuid, digitalId), Error.class);
        assertThat(response)
            .isNotNull();
        return response;
    }

    private static Error createPartnerWithErrors(DocumentCreate document) {
        var response = post(baseRoutePath + "/document", HttpStatus.BAD_REQUEST, document, Error.class);
        assertThat(response)
            .isNotNull();
        return response;
    }

    private static DocumentCreate getValidPartnerDocument(String partnerUuid, String digitalId) {
        return new DocumentCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .certifierName(RandomStringUtils.randomAlphabetic(100))
            .certifierType(CertifierType.NOTARY)
            .dateIssue(LocalDate.now())
            .divisionCode(RandomStringUtils.randomAlphanumeric(50))
            .divisionIssue(RandomStringUtils.randomAlphanumeric(250))
            .number(RandomStringUtils.randomAlphanumeric(50))
            .series(RandomStringUtils.randomAlphanumeric(50))
            .positionCertifier(RandomStringUtils.randomAlphanumeric(100))
            .documentTypeId("8a4d4464-64a1-4f3d-ab86-fd3be614f7a2");
    }

    private static DocumentCreate getEmptyPartnerDocument(String partnerUuid, String digitalId) {
        return new DocumentCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .documentTypeId("8a4d4464-64a1-4f3d-ab86-fd3be614f7a2");
    }

    public static Document updateDocument(Document document) {
        return new Document()
            .number(randomAlphanumeric(5))
            .id(document.getId())
            .version(document.getVersion())
            .unifiedId(document.getUnifiedId())
            .digitalId(document.getDigitalId())
            .certifierName(document.getCertifierName())
            .certifierType(document.getCertifierType())
            .dateIssue(document.getDateIssue())
            .divisionCode(document.getDivisionCode())
            .documentType(document.getDocumentType());
    }
}

package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.CertifierType;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class PartnerDocumentControllerTest extends AbstractIntegrationWithOutSbbolTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @AllureId("34115")
    void testGetPartnerDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var document = createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        var actualDocument =
            get(
                baseRoutePath + "/document" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                DocumentResponse.class,
                document.getDigitalId(), document.getId()
            );
        assertThat(actualDocument)
            .isNotNull();
        assertThat(actualDocument.getDocument())
            .isNotNull()
            .isEqualTo(document);
    }

    @Test
    @AllureId("34113")
    void testViewPartnerDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());
        createValidPartnerDocument(partner.getId(), partner.getDigitalId());

        var filter1 = new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(List.of(partner.getId()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response1 = post(
            baseRoutePath + "/documents/view",
            HttpStatus.OK,
            filter1,
            DocumentsResponse.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getDocuments().size())
            .isEqualTo(4);
        var filter2 = new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(List.of(partner.getId()))
            .documentType("SEAMAN_PASSPORT")
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response2 = post(
            baseRoutePath + "/documents/view",
            HttpStatus.OK,
            filter2,
            DocumentsResponse.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getDocuments().size())
            .isEqualTo(4);
        assertThat(response2.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }

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
        DocumentResponse newUpdateDocument = put(baseRoutePath + "/document", HttpStatus.OK, updateDocument, DocumentResponse.class);

        assertThat(newUpdateDocument)
            .isNotNull();
        assertThat(newUpdateDocument.getDocument().getNumber())
            .isEqualTo(newName);
        assertThat(newUpdateDocument.getErrors())
            .isNull();
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
        assertThat(documentError.getText())
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
            DocumentResponse.class
        );
        var checkDocument = get(
            baseRoutePath + "/document" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            DocumentResponse.class,
            documentUpdate.getDocument().getDigitalId(), documentUpdate.getDocument().getId());
        assertThat(checkDocument)
            .isNotNull();
        assertThat(checkDocument.getDocument().getVersion())
            .isEqualTo(document.getVersion() + 1);
        assertThat(checkDocument.getErrors())
            .isNull();
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
                DocumentResponse.class,
                document.getDigitalId(), document.getId()
            );
        assertThat(actualDocument)
            .isNotNull();

        assertThat(actualDocument.getDocument())
            .isNotNull()
            .isEqualTo(document);

        var deleteDocument =
            delete(
                baseRoutePath + "/document" + "/{digitalId}" + "/{id}",
                HttpStatus.NO_CONTENT,
                actualDocument.getDocument().getDigitalId(), actualDocument.getDocument().getId()
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
        var response = post(baseRoutePath + "/document", HttpStatus.CREATED, getValidPartnerDocument(partnerUuid, digitalId), DocumentResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getErrors())
            .isNull();
        return response.getDocument();
    }

    private static Document createValidPartnerDocument(DocumentCreate document) {
        var response = post(baseRoutePath + "/document", HttpStatus.CREATED, document, DocumentResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getErrors())
            .isNull();
        return response.getDocument();
    }

    private static DocumentCreate getValidPartnerDocument(String partnerUuid, String digitalId) {
        return new DocumentCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .certifierName("Имя")
            .certifierType(CertifierType.NOTARY)
            .dateIssue(LocalDate.now())
            .divisionCode("1111")
            .divisionIssue("444")
            .number("23")
            .series("111")
            .positionCertifier("12345")
            .documentTypeId("8a4d4464-64a1-4f3d-ab86-fd3be614f7a2")
            ;
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

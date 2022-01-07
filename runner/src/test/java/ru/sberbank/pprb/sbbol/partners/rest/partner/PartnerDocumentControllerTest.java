package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class PartnerDocumentControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetPartnerDocument() {
        var partner = createValidPartner();
        var document = createValidPartnerDocument(partner.getUuid());
        var actualDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                DocumentResponse.class,
                document.getDigitalId(), document.getUuid()
            );
        assertThat(actualDocument)
            .isNotNull();
        assertThat(actualDocument.getDocument())
            .isNotNull()
            .isEqualTo(document);
    }

    @Test
    void testViewPartnerDocument() {
        var partner = createValidPartner("7777");
        createValidPartnerDocument(partner.getUuid(), partner.getDigitalId());
        createValidPartnerDocument(partner.getUuid(), partner.getDigitalId());
        createValidPartnerDocument(partner.getUuid(), partner.getDigitalId());
        createValidPartnerDocument(partner.getUuid(), partner.getDigitalId());

        var filter1 = new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .unifiedUuid(List.of(partner.getUuid()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response1 = post(
            baseRoutePath + "/documents/view",
            filter1,
            DocumentsResponse.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getDocuments().size())
            .isEqualTo(4);
        var filter2 = new DocumentsFilter()
            .digitalId(partner.getDigitalId())
            .unifiedUuid(List.of(partner.getUuid()))
            .documentType("SEAMAN_PASSPORT")
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response2 = post(
            baseRoutePath + "/documents/view",
            filter2,
            DocumentsResponse.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getDocuments().size())
            .isEqualTo(4);
    }

    @Test
    void testCreatePartnerDocument() {
        var partner = createValidPartner();
        var document = createValidPartnerDocument(partner.getUuid());
        assertThat(document)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "unifiedUuid")
            .isEqualTo(document);
    }

    @Test
    void testUpdatePartnerDocument() {
        var partner = createValidPartner();
        var document = createValidPartnerDocument(partner.getUuid());
        String newName = "Новое номер";
        var updateDocument = new Document();
        updateDocument.uuid(document.getUuid());
        updateDocument.digitalId(document.getDigitalId());
        updateDocument.unifiedUuid(document.getUnifiedUuid());
        updateDocument.number(newName);
        DocumentResponse newUpdateDocument = put(baseRoutePath + "/document", updateDocument, DocumentResponse.class);

        assertThat(newUpdateDocument)
            .isNotNull();
        assertThat(newUpdateDocument.getDocument().getNumber())
            .isEqualTo(newName);
        assertThat(newUpdateDocument.getErrors())
            .isNull();
    }

    @Test
    void testDeletePartnerDocument() {
        var partner = createValidPartner();
        var document = createValidPartnerDocument(partner.getUuid());
        var actualDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                DocumentResponse.class,
                document.getDigitalId(),document.getUuid()
            );
        assertThat(actualDocument)
            .isNotNull();

        assertThat(actualDocument.getDocument())
            .isNotNull()
            .isEqualTo(document);

        var deleteDocument =
            delete(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                Error.class,
                actualDocument.getDocument().getDigitalId(), actualDocument.getDocument().getUuid()
            );
        assertThat(deleteDocument)
            .isNotNull();

        var searchDocument =
            getNotFound(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                Error.class,
                document.getDigitalId(), document.getUuid()
            );

        assertThat(searchDocument)
            .isNotNull();

        assertThat(searchDocument.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    private static Document createValidPartnerDocument(String partnerUuid, String digitalId) {
        var response = post(baseRoutePath + "/document", getValidPartnerDocument(partnerUuid, digitalId), DocumentResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getErrors())
            .isNull();
        return response.getDocument();
    }

    private static Document createValidPartnerDocument(String partnerUuid) {
        var response = post(baseRoutePath + "/document", getValidPartnerDocument(partnerUuid), DocumentResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getErrors())
            .isNull();
        return response.getDocument();
    }

    private static Document getValidPartnerDocument(String partnerUuid) {
        return getValidPartnerDocument(partnerUuid, "111111");
    }

    private static Document getValidPartnerDocument(String partnerUuid, String digitalId) {
        return new Document()
            .version(0L)
            .unifiedUuid(partnerUuid)
            .digitalId(digitalId)
            .certifierName("Имя")
            .certifierType(Document.CertifierTypeEnum.NOTARY)
            .dateIssue(LocalDate.now())
            .divisionCode("1111")
            .number("23")
            .documentType(
                new DocumentType()
                    .uuid("8a4d4464-64a1-4f3d-ab86-fd3be614f7a2")
                    .description("Паспорт моряка (удостоверение личности моряка)")
                    .status(false)
                    .documentType("SEAMAN_PASSPORT")
            )
            ;
    }
}

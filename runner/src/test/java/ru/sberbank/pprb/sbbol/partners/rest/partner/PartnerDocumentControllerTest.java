package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
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

public class PartnerDocumentControllerTest extends AbstractIntegrationWithOutSbbolTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetPartnerDocument() {
        var partner = createValidPartner();
        var document = createValidPartnerDocument(partner.getId());
        var actualDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
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
    void testViewPartnerDocument() {
        var partner = createValidPartner("7777");
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
        var document = createValidPartnerDocument(partner.getId());
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
        var document = createValidPartnerDocument(partner.getId());
        String newName = "Новое номер";
        var updateDocument = new Document();
        updateDocument.id(document.getId());
        updateDocument.digitalId(document.getDigitalId());
        updateDocument.unifiedId(document.getUnifiedId());
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
        var document = createValidPartnerDocument(partner.getId());
        var actualDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
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
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                actualDocument.getDocument().getDigitalId(), actualDocument.getDocument().getId()
            );
        assertThat(deleteDocument)
            .isNotNull();

        var searchDocument =
            getNotFound(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                Error.class,
                document.getDigitalId(), document.getId()
            );

        assertThat(searchDocument)
            .isNotNull();

        assertThat(searchDocument.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    private static Document createValidPartnerDocument(String partnerUuid, String digitalId) {
        var response = createPost(baseRoutePath + "/document", getValidPartnerDocument(partnerUuid, digitalId), DocumentResponse.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getErrors())
            .isNull();
        return response.getDocument();
    }

    private static Document createValidPartnerDocument(String partnerUuid) {
        var response = createPost(baseRoutePath + "/document", getValidPartnerDocument(partnerUuid), DocumentResponse.class);
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
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .certifierName("Имя")
            .certifierType(Document.CertifierTypeEnum.NOTARY)
            .dateIssue(LocalDate.now())
            .divisionCode("1111")
            .number("23")
            .documentType(
                new DocumentType()
                    .id("8a4d4464-64a1-4f3d-ab86-fd3be614f7a2")
                    .description("Паспорт моряка (удостоверение личности моряка)")
                    .deleted(false)
                    .documentType("SEAMAN_PASSPORT")
            )
            ;
    }
}

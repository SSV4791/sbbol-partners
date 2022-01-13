package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class ContactDocumentControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact";

    @Test
    void testGetContactDocument() {
        var partner = createValidPartner();
        var contact = createValidContact(partner.getId());
        var document = createValidContactDocument(contact.getId());
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
    void testViewContactDocument() {
        Partner partner = createValidPartner("6666");
        Contact contact = createValidContact(partner.getId(), partner.getDigitalId());
        createValidContactDocument(contact.getId(), contact.getDigitalId());
        createValidContactDocument(contact.getId(), contact.getDigitalId());
        createValidContactDocument(contact.getId(), contact.getDigitalId());
        createValidContactDocument(contact.getId(), contact.getDigitalId());

        DocumentsFilter filter1 = new DocumentsFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            )
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

        DocumentsFilter filter2 = new DocumentsFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(List.of(contact.getId()))
            .documentType("PASSPORT_OF_RUSSIA")
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
    void testCreateContactDocument() {
        var partner = createValidPartner();
        var contact = createValidContact(partner.getId());
        var document = createValidContactDocument(contact.getId());
        assertThat(document)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "unifiedUuid")
            .isEqualTo(document);
    }

    @Test
    void testUpdateContactDocument() {
        var partner = createValidPartner();
        var contact = createValidContact(partner.getId());
        var document = createValidContactDocument(contact.getId());
        String newName = "Новое номер";
        var updateDocument = new Document();
        updateDocument.id(document.getId());
        updateDocument.digitalId(document.getDigitalId());
        updateDocument.unifiedId(document.getUnifiedId());
        updateDocument.number(newName);
        var newUpdateDocument = put(baseRoutePath + "/document", updateDocument, DocumentResponse.class);

        assertThat(newUpdateDocument)
            .isNotNull();
        assertThat(newUpdateDocument.getDocument().getNumber())
            .isEqualTo(newName);
        assertThat(newUpdateDocument.getErrors())
            .isNull();
    }

    @Test
    void testDeleteContactDocument() {
        var partner = createValidPartner();
        var contact = createValidContact(partner.getId());
        var document = createValidContactDocument(contact.getId());
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

    public static Document getValidContactDocument(String partnerUuid) {
        return getValidContactDocument(partnerUuid, "111111");
    }

    public static Document getValidContactDocument(String partnerUuid, String digitalId) {
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
                    .id("3422aec8-7f44-4089-9a43-f8e3c5b00722")
                    .description("Паспорт РФ")
                    .deleted(false)
                    .documentType("PASSPORT_OF_RUSSIA")
            )
            ;
    }

    private static Document createValidContactDocument(String partnerUuid, String digitalId) {
        var documentResponse = createPost(baseRoutePath + "/document", getValidContactDocument(partnerUuid, digitalId), DocumentResponse.class);
        assertThat(documentResponse)
            .isNotNull();
        assertThat(documentResponse.getErrors())
            .isNull();
        return documentResponse.getDocument();
    }

    private static Document createValidContactDocument(String partnerUuid) {
        var documentResponse = createPost(baseRoutePath + "/document", getValidContactDocument(partnerUuid), DocumentResponse.class);
        assertThat(documentResponse)
            .isNotNull();
        assertThat(documentResponse.getErrors())
            .isNull();
        return documentResponse.getDocument();
    }
}

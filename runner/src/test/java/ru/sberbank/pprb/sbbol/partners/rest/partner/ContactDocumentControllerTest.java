package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class ContactDocumentControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact";

    @Test
    void testGetContactDocument() {
        var partner = createValidPartner();
        var contact = createValidContact(partner.getUuid());
        var document = createValidContactDocument(contact.getUuid());
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

//    @Test
//    void testViewContactDocument() {
//        Partner partner = createValidPartner("6666");
//        Contact contact = createValidContact(partner.getUuid(), partner.getDigitalId());
//        var document1 = createValidContactDocument(contact.getUuid(), contact.getDigitalId());
//        var document2 = createValidContactDocument(contact.getUuid(), contact.getDigitalId());
//        var document3 = createValidContactDocument(contact.getUuid(), contact.getDigitalId());
//        var document4 = createValidContactDocument(contact.getUuid(), contact.getDigitalId());
//
//        DocumentsFilter filter1 = new DocumentsFilter()
//            .digitalId(contact.getDigitalId())
//            .unifiedUuid(
//                List.of(
//                    contact.getUuid()
//                )
//            )
//            .pagination(new Pagination()
//                .count(4)
//                .offset(0));
//        var response1 = post(
//            baseRoutePath + "/documents/view",
//            filter1,
//            DocumentsResponse.class
//        );
//        assertThat(response1)
//            .isNotNull();
//        assertThat(response1.getDocuments().size())
//            .isEqualTo(4);
//
//        DocumentsFilter filter2 = new DocumentsFilter()
//            .digitalId(contact.getDigitalId())
//            .unifiedUuid(List.of(document1.getUuid()))
//            .documentType("PASSPORT_OF_RUSSIA")
//            .pagination(new Pagination()
//                .count(4)
//                .offset(0));
//        var response2 = post(
//            baseRoutePath + "/documents/view",
//            filter2,
//            ContactsResponse.class
//        );
//        assertThat(response2)
//            .isNotNull();
//        assertThat(response2.getContacts().size())
//            .isEqualTo(1);
//    }


    @Test
    void testCreateContactDocument() {
        var partner = createValidPartner();
        var contact = createValidContact(partner.getUuid());
        var document = createValidContactDocument(contact.getUuid());
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
        var contact = createValidContact(partner.getUuid());
        var document = createValidContactDocument(contact.getUuid());
        String newName = "Новое номер";
        var updateDocument = new Document();
        updateDocument.uuid(document.getUuid());
        updateDocument.digitalId(document.getDigitalId());
        updateDocument.unifiedUuid(document.getUnifiedUuid());
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
        var contact = createValidContact(partner.getUuid());
        var document = createValidContactDocument(contact.getUuid());
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

        var deleteDocument =
            delete(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                Error.class,
                actualDocument.getDocument().getDigitalId(), actualDocument.getDocument().getUuid()
            );
        assertThat(deleteDocument)
            .isNotNull();

        var searchDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                DocumentResponse.class,
                document.getDigitalId(), document.getUuid()
            );

        assertThat(searchDocument)
            .isNotNull();

        assertThat(searchDocument.getDocument())
            .isNull();
    }

    public static Document getValidContactDocument(String partnerUuid) {
        return getValidContactDocument(partnerUuid, "111111");
    }

    public static Document getValidContactDocument(String partnerUuid, String digitalId) {
        return new Document()
            .version(0L)
            .unifiedUuid(partnerUuid)
            .digitalId(digitalId)
            .certifierName("Имя")
            .certifierType(Document.CertifierTypeEnum.NOTARY)
            .dateIssue(LocalDate.now())
            .divisionCode("1111")
            .number("23")
            ;
    }

    private static Document createValidContactDocument(String partnerUuid, String digitalId) {
        var documentResponse = post(baseRoutePath + "/document", getValidContactDocument(partnerUuid, digitalId), DocumentResponse.class);
        assertThat(documentResponse)
            .isNotNull();
        assertThat(documentResponse.getErrors())
            .isNull();
        return documentResponse.getDocument();
    }

    private static Document createValidContactDocument(String partnerUuid) {
        var documentResponse = post(baseRoutePath + "/document", getValidContactDocument(partnerUuid), DocumentResponse.class);
        assertThat(documentResponse)
            .isNotNull();
        assertThat(documentResponse.getErrors())
            .isNull();
        return documentResponse.getDocument();
    }
}

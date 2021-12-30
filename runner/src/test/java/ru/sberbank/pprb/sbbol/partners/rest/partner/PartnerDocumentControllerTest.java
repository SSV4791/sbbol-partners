package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class PartnerDocumentControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetPartnerDocument() {
        Partner partner = createValidPartner();
        Document document = getValidPartnerDocument(partner.getUuid());

        DocumentResponse createDocument = post(baseRoutePath + "/document", document, DocumentResponse.class);
        assertThat(createDocument)
            .isNotNull();

        DocumentResponse actualDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                DocumentResponse.class,
                createDocument.getDocument().getDigitalId(), createDocument.getDocument().getUuid()
            );
        assertThat(actualDocument)
            .isNotNull();
        assertThat(actualDocument.getDocument())
            .isNotNull()
            .isEqualTo(createDocument.getDocument());
    }

    @Test
    void testCreatePartnerDocument() {
        Partner partner = createValidPartner();
        Document document = getValidPartnerDocument(partner.getUuid());

        DocumentResponse createDocument = post(baseRoutePath + "/document", document, DocumentResponse.class);
        assertThat(createDocument)
            .isNotNull();
        assertThat(createDocument.getDocument())
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "unifiedUuid")
            .isEqualTo(document);
        assertThat(createDocument.getErrors())
            .isNull();
    }

    @Test
    void testUpdatePartnerDocument() {
        Partner partner = createValidPartner();
        Document document = getValidPartnerDocument(partner.getUuid());

        DocumentResponse createDocument = post(baseRoutePath + "/document", document, DocumentResponse.class);

        assertThat(createDocument)
            .isNotNull();

        String newName = "Новое номер";
        Document updateDocument = new Document();
        updateDocument.uuid(createDocument.getDocument().getUuid());
        updateDocument.digitalId(createDocument.getDocument().getDigitalId());
        updateDocument.unifiedUuid(createDocument.getDocument().getUnifiedUuid());
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
        Partner partner = createValidPartner();
        Document document = getValidPartnerDocument(partner.getUuid());

        DocumentResponse createAddress = post(baseRoutePath + "/document", document, DocumentResponse.class);
        assertThat(createAddress)
            .isNotNull();

        DocumentResponse actualDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                DocumentResponse.class,
                createAddress.getDocument().getDigitalId(), createAddress.getDocument().getUuid()
            );
        assertThat(actualDocument)
            .isNotNull();

        assertThat(actualDocument.getDocument())
            .isNotNull()
            .isEqualTo(createAddress.getDocument());

        Error deleteDocument =
            delete(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                Error.class,
                actualDocument.getDocument().getDigitalId(), actualDocument.getDocument().getUuid()
            );
        assertThat(deleteDocument)
            .isNotNull();

        DocumentResponse searchDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                DocumentResponse.class,
                createAddress.getDocument().getDigitalId(), createAddress.getDocument().getUuid()
            );

        assertThat(searchDocument)
            .isNotNull();

        assertThat(searchDocument.getDocument())
            .isNull();
    }

    public static Document getValidPartnerDocument(String partnerUuid) {
        return new Document()
            .version(0L)
            .unifiedUuid(partnerUuid)
            .digitalId("111111")
            .certifierName("Имя")
            .certifierType(Document.CertifierTypeEnum.NOTARY)
            .dateIssue(LocalDate.now())
            .divisionCode("1111")
            .number("23")
            ;
    }
}

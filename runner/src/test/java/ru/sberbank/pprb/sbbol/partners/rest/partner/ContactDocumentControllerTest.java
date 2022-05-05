package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.CertifierType;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactDocumentControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact";

    @Test
    @AllureId("34112")
    void testGetContactDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var document = createValidContactDocument(contact.getId(), contact.getDigitalId());
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
    @AllureId("34163")
    void testViewContactDocument() {
        Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        Contact contact = createValidContact(partner.getId(), partner.getDigitalId());
        createValidContactDocument(contact.getId(), contact.getDigitalId());
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
            HttpStatus.OK,
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
    @AllureId("34195")
    void testCreateContactDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var expected = getValidContactDocument(contact.getId(), contact.getDigitalId());
        var document = createValidContactDocument(expected);
        assertThat(document)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version",
                "documentType")
            .isEqualTo(expected);
    }

    @Test
    @AllureId("34146")
    void testUpdateContactDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var document = createValidContactDocument(contact.getId(), contact.getDigitalId());
        var newUpdateDocument = put(baseRoutePath + "/document", HttpStatus.OK, updateDocument(document), DocumentResponse.class);
        assertThat(newUpdateDocument)
            .isNotNull();
        assertThat(newUpdateDocument.getDocument().getNumber())
            .isEqualTo(newUpdateDocument.getDocument().getNumber());
        assertThat(newUpdateDocument.getDocument().getNumber())
            .isNotEqualTo(document.getNumber());
        assertThat(newUpdateDocument.getErrors())
            .isNull();
    }

    @Test
    @AllureId("36937")
    void negativeTestUpdateDocumentVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var document = createValidContactDocument(contact.getId(), contact.getDigitalId());
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
    @AllureId("36935")
    void positiveTestUpdateDocumentVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var document = createValidContactDocument(contact.getId(), contact.getDigitalId());
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
    @AllureId("34123")
    void testDeleteContactDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var document = createValidContactDocument(contact.getId(), contact.getDigitalId());
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

    public static DocumentCreate getValidContactDocument(String partnerUuid, String digitalId) {
        return new DocumentCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .certifierName("Имя")
            .certifierType(CertifierType.NOTARY)
            .dateIssue(LocalDate.now())
            .divisionCode("1111")
            .number("23")
            .documentTypeId("3422aec8-7f44-4089-9a43-f8e3c5b00722")
            ;
    }

    private static Document createValidContactDocument(String contactUuid, String digitalId) {
        var documentResponse = post(
            baseRoutePath + "/document",
            HttpStatus.CREATED,
            getValidContactDocument(contactUuid, digitalId),
            DocumentResponse.class
        );
        assertThat(documentResponse)
            .isNotNull();
        assertThat(documentResponse.getErrors())
            .isNull();
        return documentResponse.getDocument();
    }

    private static Document createValidContactDocument(DocumentCreate document) {
        var documentResponse = post(
            baseRoutePath + "/document",
            HttpStatus.CREATED,
            document,
            DocumentResponse.class
        );
        assertThat(documentResponse)
            .isNotNull();
        assertThat(documentResponse.getErrors())
            .isNull();
        return documentResponse.getDocument();
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

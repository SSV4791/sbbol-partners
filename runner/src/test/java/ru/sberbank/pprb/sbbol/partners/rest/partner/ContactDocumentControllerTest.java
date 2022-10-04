package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
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

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactDocumentControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact";

    @Test
    void testGetContactDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var document = createValidContactDocument(contact.getId(), contact.getDigitalId());
        var actualDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Document.class,
                document.getDigitalId(), document.getId()
            );
        assertThat(actualDocument)
            .isNotNull()
            .isEqualTo(document);
    }

    @Test
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
    void testUpdateContactDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var document = createValidContactDocument(contact.getId(), contact.getDigitalId());
        var newUpdateDocument = put(baseRoutePath + "/document", HttpStatus.OK, updateDocument(document), Document.class);
        assertThat(newUpdateDocument)
            .isNotNull();
        assertThat(newUpdateDocument.getNumber())
            .isEqualTo(newUpdateDocument.getNumber());
    }

    @Test
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
            .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
        assertThat(documentError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains("Версия записи в базе данных " + (document.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    void positiveTestUpdateDocumentVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var document = createValidContactDocument(contact.getId(), contact.getDigitalId());
        var documentUpdate = put(
            baseRoutePath + "/document",
            HttpStatus.OK,
            updateDocument(document),
            Document.class
        );
        var checkDocument = get(
            baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Document.class,
            documentUpdate.getDigitalId(), documentUpdate.getId());

        assertThat(checkDocument)
            .isNotNull();
        assertThat(checkDocument.getVersion())
            .isEqualTo(document.getVersion() + 1);
    }

    @Test
    void testDeleteContactDocument() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var document = createValidContactDocument(contact.getId(), contact.getDigitalId());
        var actualDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Document.class,
                document.getDigitalId(), document.getId()
            );
        assertThat(actualDocument)
            .isNotNull()
            .isEqualTo(document);

        var deleteDocument =
            delete(
                baseRoutePath + "/documents" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", actualDocument.getId()),
                actualDocument.getDigitalId()
            ).getBody();
        assertThat(deleteDocument)
            .isNotNull();

        var searchDocument =
            get(
                baseRoutePath + "/documents" + "/{digitalId}" + "/{id}",
                HttpStatus.NOT_FOUND,
                Error.class,
                document.getDigitalId(), document.getId()
            );

        assertThat(searchDocument)
            .isNotNull();

        assertThat(searchDocument.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
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
        return post(
            baseRoutePath + "/document",
            HttpStatus.CREATED,
            getValidContactDocument(contactUuid, digitalId),
            Document.class
        );
    }

    private static Document createValidContactDocument(DocumentCreate document) {
        return post(
            baseRoutePath + "/document",
            HttpStatus.CREATED,
            document,
            Document.class
        );
    }

    public static DocumentChange updateDocument(Document document) {
        return new DocumentChange()
            .number(randomAlphanumeric(5))
            .id(document.getId())
            .version(document.getVersion())
            .unifiedId(document.getUnifiedId())
            .digitalId(document.getDigitalId())
            .certifierName(document.getCertifierName())
            .certifierType(document.getCertifierType())
            .dateIssue(document.getDateIssue())
            .divisionCode(document.getDivisionCode())
            .documentTypeId(document.getDocumentType().getId());
    }
}

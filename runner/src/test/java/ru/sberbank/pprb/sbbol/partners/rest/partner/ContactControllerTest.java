package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactResponse;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class ContactControllerTest extends AbstractIntegrationWithOutSbbolTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @AllureId("34164")
    void testGetContact() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var actualContact =
            get(
                baseRoutePath + "/contact" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                ContactResponse.class,
                contact.getDigitalId(), contact.getId()
            );
        assertThat(actualContact)
            .isNotNull();
        assertThat(actualContact.getContact())
            .isNotNull()
            .isEqualTo(contact);
    }

    @Test
    @AllureId("34144")
    void testViewContact() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact1 = createValidContact(partner.getId(), partner.getDigitalId());
        var contact2 = createValidContact(partner.getId(), partner.getDigitalId());
        var contact3 = createValidContact(partner.getId(), partner.getDigitalId());
        var contact4 = createValidContact(partner.getId(), partner.getDigitalId());
        var contact5 = createValidContact(partner.getId(), partner.getDigitalId());

        var filter1 = new ContactsFilter()
            .digitalId(partner.getDigitalId())
            .partnerId(partner.getId())
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response1 = post(
            baseRoutePath + "/contacts/view",
            HttpStatus.OK,
            filter1,
            ContactsResponse.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getContacts().size())
            .isEqualTo(4);

        var filter2 = new ContactsFilter()
            .digitalId(partner.getDigitalId())
            .partnerId(partner.getId())
            .ids(List.of(contact4.getId()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response2 = post(
            baseRoutePath + "/contacts/view",
            HttpStatus.OK,
            filter2,
            ContactsResponse.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getContacts().size())
            .isEqualTo(1);

        var filter3 = new ContactsFilter()
            .digitalId(partner.getDigitalId())
            .partnerId(partner.getId())
            .ids(
                List.of(
                    contact1.getId(),
                    contact2.getId(),
                    contact3.getId(),
                    contact4.getId(),
                    contact5.getId()
                )
            )
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response3 = post(
            baseRoutePath + "/contacts/view",
            HttpStatus.OK,
            filter3,
            ContactsResponse.class
        );
        assertThat(response3)
            .isNotNull();
        assertThat(response3.getContacts().size())
            .isEqualTo(4);
        assertThat(response3.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }

    @Test
    @AllureId("34143")
    void testCreateContact() {
        var partner = createValidPartner(randomAlphabetic(10));
        var expected = getValidContact(partner.getId(), partner.getDigitalId());
        var contact = createValidContact(expected);
        assertThat(contact)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version",
                "phones",
                "emails"
            )
            .isEqualTo(expected);
    }


    @Test
    @AllureId("34170")
    void testUpdateContact() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var newUpdateContact = put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            updateContact(contact),
            ContactResponse.class
        );
        assertThat(newUpdateContact)
            .isNotNull();
        assertThat(newUpdateContact.getContact().getFirstName())
            .isEqualTo(newUpdateContact.getContact().getFirstName());
        assertThat(newUpdateContact.getContact().getFirstName())
            .isNotEqualTo(contact.getFirstName());
        assertThat(newUpdateContact.getErrors())
            .isNull();
    }

    @Test
    @AllureId("36932")
    void negativeTestUpdateContactVersion() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        Long version = contact.getVersion() + 1;
        contact.setVersion(version);
        var contactError = put(
            baseRoutePath + "/contact",
            HttpStatus.BAD_REQUEST,
            updateContact(contact),
            Error.class
        );
        assertThat(contactError.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(contactError.getText())
            .contains("Версия записи в базе данных " + (contact.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    @AllureId("36934")
    void positiveTestUpdateContactVersion() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var contactUpdate = put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            updateContact(contact),
            ContactResponse.class
        );
        var checkContact = get(
            baseRoutePath + "/contact" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            ContactResponse.class,
            contactUpdate.getContact().getDigitalId(), contactUpdate.getContact().getId());
        assertThat(checkContact)
            .isNotNull();
        assertThat(checkContact.getContact().getVersion())
            .isEqualTo(contact.getVersion() + 1);
        assertThat(checkContact.getErrors())
            .isNull();
    }

    @Test
    @AllureId("34201")
    void testDeleteContact() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var actualContact =
            get(
                baseRoutePath + "/contact" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                ContactResponse.class,
                contact.getDigitalId(), contact.getId()
            );
        assertThat(actualContact)
            .isNotNull();
        assertThat(actualContact.getContact())
            .isNotNull()
            .isEqualTo(contact);

        var deleteContact =
            delete(
                baseRoutePath + "/contact" + "/{digitalId}" + "/{id}",
                HttpStatus.NO_CONTENT,
                actualContact.getContact().getDigitalId(), actualContact.getContact().getId()
            ).getBody();

        assertThat(deleteContact)
            .isNotNull();

        var searchContact =
            get(
                baseRoutePath + "/contact" + "/{digitalId}" + "/{id}",
                HttpStatus.NOT_FOUND,
                Error.class,
                contact.getDigitalId(), contact.getId()
            );
        assertThat(searchContact)
            .isNotNull();

        assertThat(searchContact.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    public static ContactCreate getValidContact(String partnerUuid, String digitalId) {
        return new ContactCreate()
            .partnerId(partnerUuid)
            .digitalId(digitalId)
            .legalForm(LegalForm.LEGAL_ENTITY)
            .orgName("Наименование компании")
            .firstName("Имя клиента")
            .secondName("Фамилия клиента")
            .middleName("Отчество клиента")
            .position("Должность")
            .phones(
                List.of(
                    "+79241111111"
                ))
            .emails(
                List.of(
                    "a.a.a@sberbank.ru"
                ))
            ;
    }

    protected static Contact createValidContact(String partnerUuid, String digitalId) {
        var createContact = post(
            baseRoutePath + "/contact",
            HttpStatus.CREATED,
            getValidContact(partnerUuid, digitalId),
            ContactResponse.class
        );
        assertThat(createContact)
            .isNotNull();
        assertThat(createContact.getErrors())
            .isNull();
        return createContact.getContact();
    }

    protected static Contact createValidContact(ContactCreate contact) {
        var createContact = post(
            baseRoutePath + "/contact",
            HttpStatus.CREATED,
            contact,
            ContactResponse.class
        );
        assertThat(createContact)
            .isNotNull();
        assertThat(createContact.getErrors())
            .isNull();
        return createContact.getContact();
    }

    public static Contact updateContact(Contact contact) {
        return new Contact()
            .partnerId(contact.getPartnerId())
            .digitalId(contact.getDigitalId())
            .legalForm(contact.getLegalForm())
            .orgName(contact.getOrgName())
            .secondName(contact.getSecondName())
            .middleName(contact.getMiddleName())
            .position(contact.getPosition())
            .phones(contact.getPhones())
            .emails(contact.getEmails())
            .firstName(randomAlphabetic(10))
            .version(contact.getVersion())
            .id(contact.getId());
    }
}

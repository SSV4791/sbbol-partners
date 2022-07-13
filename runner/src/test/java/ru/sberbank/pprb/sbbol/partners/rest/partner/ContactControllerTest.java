package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @AllureId("34164")
    void testGetContact() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var actualContact =
            get(
                baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Contact.class,
                contact.getDigitalId(), contact.getId()
            );
        assertThat(actualContact)
            .isNotNull()
            .isEqualTo(contact);
    }

    @Test
    @AllureId("")
    void testNegativeViewContact() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact1 = createValidContact(partner.getId(), partner.getDigitalId());
        var contact2 = createValidContact(partner.getId(), partner.getDigitalId());
        var contact3 = createValidContact(partner.getId(), partner.getDigitalId());
        var contact4 = createValidContact(partner.getId(), partner.getDigitalId());
        var contact5 = createValidContact(partner.getId(), partner.getDigitalId());

        var filter1 = new ContactsFilter()
            .digitalId(partner.getDigitalId())
            .partnerId(partner.getId());
        var response1 = post(
            baseRoutePath + "/contacts/view",
            HttpStatus.BAD_REQUEST,
            filter1,
            Error.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());

        var filter2 = new ContactsFilter()
            .digitalId(partner.getDigitalId())
            .partnerId(partner.getId())
            .ids(List.of(contact4.getId()))
            .pagination(new Pagination()
                .offset(0));
        var response2 = post(
            baseRoutePath + "/contacts/view",
            HttpStatus.BAD_REQUEST,
            filter2,
            Error.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response1.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());

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
                .count(4));
        var response3 = post(
            baseRoutePath + "/contacts/view",
            HttpStatus.BAD_REQUEST,
            filter3,
            Error.class
        );
        assertThat(response3)
            .isNotNull();
        assertThat(response1.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
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
    @AllureId("")
    void testCreateContact2() {
        var partner = createValidPartner(randomAlphabetic(10));
        var expected = getValidContact(partner.getId(), partner.getDigitalId());
        expected.setEmails(null);
        expected.setPhones(null);
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
    @AllureId("")
    void testNegativeUpdateChildContact() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        HashSet<Phone> newPhones = new HashSet<>();
        if (contact.getPhones() != null) {
            for (var phone : contact.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion());
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(12));
                newPhones.add(newPhone);
            }
        }
        HashSet<Email> newEmails = new HashSet<>();
        if (contact.getEmails() != null) {
            for (var email : contact.getEmails()) {
                var newEmail = new Email();
                newEmail.setVersion(email.getVersion());
                newEmail.setId(email.getId());
                newEmail.setUnifiedId(email.getUnifiedId());
                newEmail.setDigitalId(email.getDigitalId());
                newEmail.setEmail(email.getEmail());
                newEmails.add(newEmail);
            }
        }
        contact.setPhones(newPhones);
        contact.setEmails(newEmails);
        var newUpdateContact = put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            updateContact(contact),
            Contact.class
        );
        assertThat(newUpdateContact)
            .isNotNull();
        assertThat(newUpdateContact.getFirstName())
            .isEqualTo(newUpdateContact.getFirstName());
        assertThat(newUpdateContact.getFirstName())
            .isNotEqualTo(contact.getFirstName());

        HashSet<Phone> newPhones1 = new HashSet<>();
        if (contact.getPhones() != null) {
            for (var phone : contact.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion() + 1);
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(12));
                newPhones1.add(newPhone);
            }
        }
        HashSet<Email> newEmails1 = new HashSet<>();
        if (contact.getEmails() != null) {
            for (var email : contact.getEmails()) {
                var newEmail = new Email();
                newEmail.setVersion(email.getVersion() + 10);
                newEmail.setId(email.getId());
                newEmail.setUnifiedId(email.getUnifiedId());
                newEmail.setDigitalId(email.getDigitalId());
                newEmail.setEmail(email.getEmail());
                newEmails1.add(newEmail);
            }
        }
        contact.setPhones(newPhones1);
        contact.setEmails(newEmails1);
        contact.setVersion(newUpdateContact.getVersion() + 1);
        var newUpdateContact1 = put(
            baseRoutePath + "/contact",
            HttpStatus.BAD_REQUEST,
            updateContact(contact),
            Error.class
        );
        assertThat(newUpdateContact1)
            .isNotNull();
        assertThat(newUpdateContact1.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
    @AllureId("")
    void testUpdateChildContact() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        HashSet<Phone> newPhones = new HashSet<>();
        if (contact.getPhones() != null) {
            for (var phone : contact.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion());
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(12));
                newPhones.add(newPhone);
            }
        }
        HashSet<Email> newEmails = new HashSet<>();
        if (contact.getEmails() != null) {
            for (var email : contact.getEmails()) {
                var newEmail = new Email();
                newEmail.setVersion(email.getVersion());
                newEmail.setId(email.getId());
                newEmail.setUnifiedId(email.getUnifiedId());
                newEmail.setDigitalId(email.getDigitalId());
                newEmail.setEmail(email.getEmail());
                newEmails.add(newEmail);
            }
        }
        contact.setPhones(newPhones);
        contact.setEmails(newEmails);
        var newUpdateContact = put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            updateContact(contact),
            Contact.class
        );
        assertThat(newUpdateContact)
            .isNotNull();
        assertThat(newUpdateContact.getFirstName())
            .isEqualTo(newUpdateContact.getFirstName());
        assertThat(newUpdateContact.getFirstName())
            .isNotEqualTo(contact.getFirstName());

        HashSet<Phone> newPhones1 = new HashSet<>();
        if (contact.getPhones() != null) {
            for (var phone : contact.getPhones()) {
                var newPhone = new Phone();
                newPhone.setVersion(phone.getVersion() + 1);
                newPhone.setId(phone.getId());
                newPhone.setUnifiedId(phone.getUnifiedId());
                newPhone.setDigitalId(phone.getDigitalId());
                newPhone.setPhone(randomNumeric(12));
                newPhones1.add(newPhone);
            }
        }
        HashSet<Email> newEmails1 = new HashSet<>();
        if (contact.getEmails() != null) {
            for (var email : contact.getEmails()) {
                var newEmail1 = new Email();
                newEmail1.setVersion(email.getVersion() + 1);
                newEmail1.setId(email.getId());
                newEmail1.setUnifiedId(email.getUnifiedId());
                newEmail1.setDigitalId(email.getDigitalId());
                newEmail1.setEmail(randomAlphabetic(64) + "@mail.ru");
                newEmails1.add(newEmail1);
            }
        }
        contact.setPhones(newPhones1);
        contact.setEmails(newEmails1);
        contact.setVersion(contact.getVersion() + 1);
        var newUpdateContact1 = put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            updateContact(contact),
            Contact.class
        );
        assertThat(newUpdateContact1)
            .isNotNull();
        assertThat(newUpdateContact1.getFirstName())
            .isEqualTo(newUpdateContact1.getFirstName());
        assertThat(newUpdateContact1.getFirstName())
            .isNotEqualTo(contact.getFirstName());
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
            Contact.class
        );
        assertThat(newUpdateContact)
            .isNotNull();
        assertThat(newUpdateContact.getFirstName())
            .isEqualTo(newUpdateContact.getFirstName());
        assertThat(newUpdateContact.getFirstName())
            .isNotEqualTo(contact.getFirstName());
    }

    @Test
    @AllureId("")
    void testUpdateContact2() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var contactUpdate = updateContact(contact);
        contactUpdate.setEmails(null);
        contactUpdate.setPhones(null);
        var newUpdateContact = put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            contactUpdate,
            Contact.class
        );
        assertThat(newUpdateContact)
            .isNotNull();
        assertThat(newUpdateContact.getFirstName())
            .isEqualTo(newUpdateContact.getFirstName());
        assertThat(newUpdateContact.getFirstName())
            .isNotEqualTo(contact.getFirstName());
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
        assertThat(contactError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
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
            Contact.class
        );
        var checkContact = get(
            baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Contact.class,
            contactUpdate.getDigitalId(), contactUpdate.getId());
        assertThat(checkContact)
            .isNotNull();
        assertThat(checkContact.getVersion())
            .isEqualTo(contact.getVersion() + 1);
    }

    @Test
    @AllureId("34201")
    void testDeleteContact() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var actualContact =
            get(
                baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Contact.class,
                contact.getDigitalId(), contact.getId()
            );
        assertThat(actualContact)
            .isNotNull()
            .isEqualTo(contact);

        var deleteContact =
            delete(
                baseRoutePath + "/contacts" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", actualContact.getId()),
                actualContact.getDigitalId()
            ).getBody();

        assertThat(deleteContact)
            .isNotNull();

        var searchContact =
            get(
                baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
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
                Set.of(
                    "79241111111"
                ))
            .emails(
                Set.of(
                    "a.a.a@sberbank.ru"
                ))
            ;
    }

    protected static Contact createValidContact(String partnerUuid, String digitalId) {
        return post(
            baseRoutePath + "/contact",
            HttpStatus.CREATED,
            getValidContact(partnerUuid, digitalId),
            Contact.class
        );
    }

    protected static Contact createValidContact(ContactCreate contact) {
        return post(
            baseRoutePath + "/contact",
            HttpStatus.CREATED,
            contact,
            Contact.class
        );
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

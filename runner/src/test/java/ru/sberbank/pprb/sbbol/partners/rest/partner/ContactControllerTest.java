package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactResponse;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class ContactControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetContact() {
        var partner = createValidPartner();
        var contact = createValidContact(partner.getUuid());
        var actualContact =
            get(
                baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
                ContactResponse.class,
                contact.getDigitalId(), contact.getUuid()
            );
        assertThat(actualContact)
            .isNotNull();
        assertThat(actualContact.getContact())
            .isNotNull()
            .isEqualTo(contact);
    }

    @Test
    void testViewContact() {
        var partner = createValidPartner("2222");
        var contact1 = createValidContact(partner.getUuid(), partner.getDigitalId());
        var contact2 = createValidContact(partner.getUuid(), partner.getDigitalId());
        var contact3 = createValidContact(partner.getUuid(), partner.getDigitalId());
        var contact4 = createValidContact(partner.getUuid(), partner.getDigitalId());

        var filter1 = new ContactsFilter()
            .digitalId(partner.getDigitalId())
            .partnerUuid(partner.getUuid())
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response1 = post(
            baseRoutePath + "/contacts/view",
            filter1,
            ContactsResponse.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getContacts().size())
            .isEqualTo(4);

        var filter2 = new ContactsFilter()
            .digitalId(partner.getDigitalId())
            .partnerUuid(partner.getUuid())
            .uuid(List.of(contact4.getUuid()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response2 = post(
            baseRoutePath + "/contacts/view",
            filter2,
            ContactsResponse.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getContacts().size())
            .isEqualTo(1);

        var filter3 = new ContactsFilter()
            .digitalId(partner.getDigitalId())
            .partnerUuid(partner.getUuid())
            .uuid(
                List.of(
                    contact1.getUuid(),
                    contact2.getUuid(),
                    contact3.getUuid(),
                    contact4.getUuid()
                )
            )
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response3 = post(
            baseRoutePath + "/contacts/view",
            filter3,
            ContactsResponse.class
        );
        assertThat(response3)
            .isNotNull();
        assertThat(response3.getContacts().size())
            .isEqualTo(4);
    }

    @Test
    void testCreateContact() {
        var partner = createValidPartner();
        var contact = createValidContact(partner.getUuid());
        assertThat(contact)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "phones.uuid",
                "phones.unifiedUuid",
                "emails.uuid",
                "emails.unifiedUuid")
            .isEqualTo(contact);
    }


    @Test
    void testUpdateContact() {
        var partner = createValidPartner();
        var contact = createValidContact(partner.getUuid());
        String newName = "Новое наименование";
        var updateContact = new Contact();
        updateContact.uuid(contact.getUuid());
        updateContact.digitalId(contact.getDigitalId());
        updateContact.partnerUuid(contact.getPartnerUuid());
        updateContact.orgName(newName);
        var newUpdateContact = put(baseRoutePath + "/contact", updateContact, ContactResponse.class);

        assertThat(newUpdateContact)
            .isNotNull();
        assertThat(newUpdateContact.getContact().getOrgName())
            .isEqualTo(newName);
        assertThat(newUpdateContact.getErrors())
            .isNull();
    }

    @Test
    void testDeleteContact() {
        var partner = createValidPartner();
        var contact = createValidContact(partner.getUuid());
        var actualContact =
            get(
                baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
                ContactResponse.class,
                contact.getDigitalId(), contact.getUuid()
            );
        assertThat(actualContact)
            .isNotNull();
        assertThat(actualContact.getContact())
            .isNotNull()
            .isEqualTo(contact);

        var deleteContact =
            delete(
                baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
                Error.class,
                actualContact.getContact().getDigitalId(), actualContact.getContact().getUuid()
            );

        assertThat(deleteContact)
            .isNotNull();

        var searchContact =
            get(
                baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
                ContactResponse.class,
                contact.getDigitalId(), contact.getUuid()
            );
        assertThat(searchContact)
            .isNotNull();

        assertThat(searchContact.getContact())
            .isNull();
    }

    public static Contact getValidContact(String partnerUuid) {
        return getValidContact(partnerUuid, "111111");
    }

    public static Contact getValidContact(String partnerUuid, String digitalId) {
        return new Contact()
            .version(0L)
            .partnerUuid(partnerUuid)
            .digitalId(digitalId)
            .legalForm(Contact.LegalFormEnum.LEGAL_ENTITY)
            .orgName("Наименование компании")
            .firstName("Имя клиента")
            .secondName("Фамилия клиента")
            .middleName("Отчество клиента")
            .position("Должность")
            .phones(
                List.of(
                    new Phone().phone("+79241111111")
                        .version(0L)
                ))
            .emails(List.of(
                new Email().email("a.a.a@sberbank.ru")
                    .version(0L)
            ))
            ;
    }

    protected static Contact createValidContact(String partnerUuid, String digitalId) {
        var createContact = post(baseRoutePath + "/contact", getValidContact(partnerUuid, digitalId), ContactResponse.class);
        assertThat(createContact)
            .isNotNull();
        assertThat(createContact.getErrors())
            .isNull();
        return createContact.getContact();
    }

    protected static Contact createValidContact(String partnerUuid) {
        var createContact = post(baseRoutePath + "/contact", getValidContact(partnerUuid), ContactResponse.class);
        assertThat(createContact)
            .isNotNull();
        assertThat(createContact.getErrors())
            .isNull();
        return createContact.getContact();
    }
}

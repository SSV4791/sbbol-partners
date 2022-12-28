package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.DisplayName;
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
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @DisplayName("GET /partner/contacts/{digitalId}/{id} Получение адреса")
    void testGetContact() {
        var contact = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });
        var actualContact = Allure.step("Выполнение get-запроса /partner/contacts/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Contact.class,
            contact.getDigitalId(), contact.getId()
        ));
        Allure.step("Проверка корректности ответа", () -> assertThat(actualContact)
            .isNotNull()
            .isEqualTo(contact));
    }

    @Test
    @DisplayName("NEG POST /partner/contacts/view без параметра pagination")
    void testNegativeViewContactWithoutPagination() {
        var filter = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return new ContactsFilter()
                .digitalId(partner.getDigitalId())
                .partnerId(partner.getId());
        });
        var response = Allure.step("Выполнение post-запроса /partner/contacts/view, код ответа 400", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contacts/view без параметра pagination.count")
    void testNegativeViewContactWithoutPaginationCount() {
        var filter = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return new ContactsFilter()
                .digitalId(partner.getDigitalId())
                .partnerId(partner.getId())
                .ids(List.of(contact.getId()))
                .pagination(new Pagination()
                    .offset(0));
        });
        var response = Allure.step("Выполнение post-запроса /partner/contacts/view, код ответа 400", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contacts/view без параметра pagination.offset")
    void testNegativeViewContactWithoutPaginationOffset() {
        var filter = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact1 = createValidContact(partner.getId(), partner.getDigitalId());
            var contact2 = createValidContact(partner.getId(), partner.getDigitalId());
            var contact3 = createValidContact(partner.getId(), partner.getDigitalId());
            var contact4 = createValidContact(partner.getId(), partner.getDigitalId());
            var contact5 = createValidContact(partner.getId(), partner.getDigitalId());

            return new ContactsFilter()
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
        });
        var response = Allure.step("Выполнение post-запроса /partner/contacts/view, код ответа 400", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("POST /partner/contacts/view c пустым списком")
    void testViewContactWithEmptyList() {
        var filter = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return new ContactsFilter()
                .digitalId(partner.getDigitalId())
                .partnerId(partner.getId())
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });
        var response = Allure.step("Выполнение post-запроса /partner/contacts/view, код ответа 200", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.OK,
            filter,
            ContactsResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getPagination().getCount())
                .isEqualTo(4);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.FALSE);
        });
    }

    @Test
    @DisplayName("POST /partner/contacts/view с единственным контактом")
    void testViewContactWithOnlyContact() {
        var filter = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return new ContactsFilter()
                .digitalId(partner.getDigitalId())
                .partnerId(partner.getId())
                .ids(List.of(contact.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });
        var response = Allure.step("Выполнение post-запроса /partner/contacts/view, код ответа 200", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.OK,
            filter,
            ContactsResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getContacts().size())
                .isEqualTo(1);
        });
    }

    @Test
    @DisplayName("POST /partner/contacts/view несколько контактов")
    void testViewContactWithSeveralContacts() {
        var filter = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact1 = createValidContact(partner.getId(), partner.getDigitalId());
            var contact2 = createValidContact(partner.getId(), partner.getDigitalId());
            var contact3 = createValidContact(partner.getId(), partner.getDigitalId());
            var contact4 = createValidContact(partner.getId(), partner.getDigitalId());
            var contact5 = createValidContact(partner.getId(), partner.getDigitalId());
            return new ContactsFilter()
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
        });
        var response = Allure.step("Выполнение post-запроса /partner/contacts/view, код ответа 200", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.OK,
            filter,
            ContactsResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getContacts().size())
                .isEqualTo(4);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.TRUE);
        });
    }

    @Test
    @DisplayName("POST /partner/contact создание контакта")
    void testCreateContact() {
        var expected = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return getValidContact(partner.getId(), partner.getDigitalId());
        });
        var contact = Allure.step("Выполнение post-запроса /partner/contact, код ответа 200",
            () -> createValidContact(expected));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(contact)
                .usingRecursiveComparison()
                .ignoringFields(
                    "id",
                    "version",
                    "phones",
                    "emails"
                )
                .isEqualTo(expected);
        });
    }

    @Test
    @DisplayName("POST /partner/contact создание контакта с пустыми email и phone")
    void testCreateContact2() {
        var expected = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var expected1 = getValidContact(partner.getId(), partner.getDigitalId());
            expected1.setEmails(null);
            expected1.setPhones(null);
            return expected1;
        });
        var contact = Allure.step("Выполнение post-запроса /partner/contact, код ответа 200",
            () -> createValidContact(expected));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(contact)
                .usingRecursiveComparison()
                .ignoringFields(
                    "id",
                    "version",
                    "phones",
                    "emails"
                )
                .isEqualTo(expected);
        });
    }

    @Test
    @DisplayName("PUT /partner/contact редактирование контакта с невалидным phone")
    void testCreateInvalidContactButValidLength() {
        var contact = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var expected = getValidContact(partner.getId(), partner.getDigitalId());
            var contact1 = createValidContact(expected);
            contact1.getPhones()
                .forEach(value -> value.setPhone("ABC" + randomAlphabetic(10)));
            return contact1;
        });
        var createContact = Allure.step("Выполнение put-запроса /partner/contact, код ответа 400", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.BAD_REQUEST,
            contact,
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> assertThat(createContact)
            .isNotNull());
    }

    @Test
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
                newPhone.setPhone(randomNumeric(13));
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
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
    }

    @Test
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
                newPhone.setPhone(randomNumeric(13));
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
                newPhone.setPhone(randomNumeric(13));
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
            .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
        assertThat(contactError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains("Версия записи в базе данных " + (contact.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
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
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
    }

    @Test
    @DisplayName("NEG DELETE /partner/contacts/{digitalId} ненайденный документ")
    void testNegativeDeleteContact() {
        var contact = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact1 = createValidContact(partner.getId(), partner.getDigitalId());
            delete(
                baseRoutePath + "/contacts" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", contact1.getId()),
                contact1.getDigitalId()
            );
            return contact1;
        });
        var deleteContact = Allure.step("Выполнение delete-запроса /partner/contacts/{digitalId}, код ответа 404", () ->
            delete(
                baseRoutePath + "/contacts" + "/{digitalId}",
                HttpStatus.NOT_FOUND,
                Map.of("ids", contact.getId()),
                contact.getDigitalId()
            ));
        Allure.step("Проверка корректности ответа", () -> assertThat(deleteContact).isNotNull());
    }

    @Test
    @DisplayName("NEG PUT /partner/contact ненайденный документ")
    void testUpdateDeletedContact() {
        var contact = Allure.step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact1 = createValidContact(partner.getId(), partner.getDigitalId());
            delete(
                baseRoutePath + "/contacts" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", contact1.getId()),
                contact1.getDigitalId()
            ).getBody();
            return contact1;
        });
        var updateContact = Allure.step("Выполнение put-запроса /partner/contact, код ответа 404", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.NOT_FOUND,
            updateContact(contact),
            Error.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(updateContact).isNotNull();
            assertThat(updateContact.getCode()).isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(updateContact.getMessage()).isEqualTo("Искомая сущность contact с id: " +
                contact.getId() + ", digitalId: " + contact.getDigitalId() + " не найдена");
        });
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
                    "0079241111111"
                ))
            .emails(
                Set.of(
                    "a.a.a@sberbank.ru"
                ))
            ;
    }

    protected static Contact createValidContact(String partnerUuid, String digitalId) {
        return Allure.step("Создание валидных реквизитов", () -> post(
            baseRoutePath + "/contact",
            HttpStatus.CREATED,
            getValidContact(partnerUuid, digitalId),
            Contact.class
        ));
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

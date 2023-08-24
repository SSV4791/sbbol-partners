package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.UUID;

import static io.qameta.allure.Allure.step;
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
        var contact = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });

        var actualContact = step("Выполнение get-запроса /partner/contacts/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Contact.class,
            contact.getDigitalId(), contact.getId()));

        step("Проверка корректности ответа", () -> {
            assertThat(actualContact)
                .isNotNull()
                .isEqualTo(contact);
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contacts/view без параметра pagination")
    void testNegativeViewContactWithoutPagination() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return new ContactsFilter()
                .digitalId(partner.getDigitalId())
                .partnerId(partner.getId());
        });

        var response = step("Выполнение post-запроса /partner/contacts/view, код ответа 400", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contacts/view без параметра pagination.count")
    void testNegativeViewContactWithoutPaginationCount() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return new ContactsFilter()
                .digitalId(partner.getDigitalId())
                .partnerId(partner.getId())
                .ids(List.of(contact.getId()))
                .pagination(new Pagination()
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/contacts/view, код ответа 400", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contacts/view без параметра pagination.offset")
    void testNegativeViewContactWithoutPaginationOffset() {
        var filter = step("Подготовка тестовых данных", () -> {
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

        var response = step("Выполнение post-запроса /partner/contacts/view, код ответа 400", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("POST /partner/contacts/view c пустым списком")
    void testViewContactWithEmptyList() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return new ContactsFilter()
                .digitalId(partner.getDigitalId())
                .partnerId(partner.getId())
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/contacts/view, код ответа 200", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.OK,
            filter,
            ContactsResponse.class));

        step("Проверка корректности ответа", () -> {
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
        var filter = step("Подготовка тестовых данных", () -> {
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
        var response = step("Выполнение post-запроса /partner/contacts/view, код ответа 200", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.OK,
            filter,
            ContactsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getContacts())
                .hasSize(1);
        });
    }

    @Test
    @DisplayName("POST /partner/contacts/view несколько контактов")
    void testViewContactWithSeveralContacts() {
        var filter = step("Подготовка тестовых данных", () -> {
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

        var response = step("Выполнение post-запроса /partner/contacts/view, код ответа 200", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.OK,
            filter,
            ContactsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getContacts())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.TRUE);
        });
    }

    @Test
    @DisplayName("POST /partner/contacts/view Запрос чужих контактов")
    void testViewContactWithWrongContact() {
        var partner = step("Подготовка первого партнера без контактов", () ->
            createValidPartner(randomAlphabetic(10)));
        var contact = step("Подготовка второго партнера с контактами", () -> {
            var partnerWithContact = createValidPartner(randomAlphabetic(10));
            return createValidContact(partnerWithContact.getId(), partnerWithContact.getDigitalId());
        });

        var filter = step("Подготовка тестовых данных", () ->
            new ContactsFilter()
                .digitalId(partner.getDigitalId())
                .partnerId(partner.getId())
                .ids(
                    List.of(
                        contact.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0)));
        var response = step("Выполнение post-запроса /partner/contacts/view, код ответа 200", () -> post(
            baseRoutePath + "/contacts/view",
            HttpStatus.OK,
            filter,
            ContactsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getContacts())
                .isNull();
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.FALSE);
        });
    }

    @Test
    @DisplayName("POST /partner/contact создание контакта")
    void testCreateContact() {
        var expected = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return getValidContact(partner.getId(), partner.getDigitalId());
        });

        var contact = step("Выполнение post-запроса /partner/contact, код ответа 200", () ->
            createValidContact(expected));

        step("Проверка корректности ответа", () -> {
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
        var expected = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var expected1 = getValidContact(partner.getId(), partner.getDigitalId());
            expected1.setEmails(null);
            expected1.setPhones(null);
            return expected1;
        });

        var contact = step("Выполнение post-запроса /partner/contact, код ответа 200", () ->
            createValidContact(expected));

        step("Проверка корректности ответа", () -> {
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
    @DisplayName("POST /partner/contact создание контакта для не существующего контрагента")
    void testCreateContactForNonExistentCounterparty() {
        var contactWithoutCounterparty = step("Подготовка тестовых данных", () ->
            getValidContact(UUID.randomUUID(), RandomStringUtils.randomAlphabetic(10)));
        var contact = step("Выполнение post-запроса /partner/contact, код ответа 404", () ->
                createContactWithError(contactWithoutCounterparty));

        step("Проверка корректности ответа", () -> {
            assertThat(contact).isNotNull();
            assertThat(contact.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("PUT /partner/contact редактирование контакта с невалидным phone")
    void testCreateInvalidContactButValidLength() {
        var contact = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var expected = getValidContact(partner.getId(), partner.getDigitalId());
            var contact1 = createValidContact(expected);
            contact1.getPhones()
                .forEach(value -> value.setPhone("ABC" + randomAlphabetic(10)));
            return contact1;
        });

        var createContact = step("Выполнение put-запроса /partner/contact, код ответа 400", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.BAD_REQUEST,
            contact,
            Error.class));

        step("Проверка корректности ответа", () -> assertThat(createContact)
            .isNotNull());
    }

    @Test
    @DisplayName("PUT /partner/contact негативные попытки редактирования контактов")
    void testNegativeUpdateChildContact() {
        var contact = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });

        var newPhones = step("Подготовка новых номеров телефонов", () -> {
            Set<Phone> phones = new HashSet<>();
            if (contact.getPhones() != null) {
                for (var phone : contact.getPhones()) {
                    var newPhone = new Phone();
                    newPhone.setVersion(phone.getVersion());
                    newPhone.setId(phone.getId());
                    newPhone.setUnifiedId(phone.getUnifiedId());
                    newPhone.setDigitalId(phone.getDigitalId());
                    newPhone.setPhone(randomNumeric(13));
                    phones.add(newPhone);
                }
            }
            return phones;
        });

        var newEmails = step("Подготовка новых емайлов", () -> {
            Set<Email> emails = new HashSet<>();
            if (contact.getEmails() != null) {
                for (var email : contact.getEmails()) {
                    var newEmail = new Email();
                    newEmail.setVersion(email.getVersion());
                    newEmail.setId(email.getId());
                    newEmail.setUnifiedId(email.getUnifiedId());
                    newEmail.setDigitalId(email.getDigitalId());
                    newEmail.setEmail(email.getEmail());
                    emails.add(newEmail);
                }
            }
            return emails;
        });

        step("Подготовка тестовых данных", () -> {
            contact.setPhones(newPhones);
            contact.setEmails(newEmails);
        });

        var newUpdateContact = step("Выполнение put-запроса /partner/contact, код ответа 200", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            updateContact(contact),
            Contact.class));

        step("Проверка корректности ответа", () -> {
            assertThat(newUpdateContact)
                .isNotNull();
            assertThat(newUpdateContact.getFirstName())
                .isEqualTo(newUpdateContact.getFirstName());
            assertThat(newUpdateContact.getFirstName())
                .isNotEqualTo(contact.getFirstName());
        });

        var newPhones1 = step("Подготовка новых номеров телефонов", () -> {
            Set<Phone> phones1 = new HashSet<>();
            if (contact.getPhones() != null) {
                for (var phone : contact.getPhones()) {
                    var newPhone = new Phone();
                    newPhone.setVersion(phone.getVersion() + 1);
                    newPhone.setId(phone.getId());
                    newPhone.setUnifiedId(phone.getUnifiedId());
                    newPhone.setDigitalId(phone.getDigitalId());
                    newPhone.setPhone(randomNumeric(12));
                    phones1.add(newPhone);
                }
            }
            return phones1;
        });

        var newEmails1 = step("Подготовка новых емайлов", () -> {
            Set<Email> emails1 = new HashSet<>();
            if (contact.getEmails() != null) {
                for (var email : contact.getEmails()) {
                    var newEmail = new Email();
                    newEmail.setVersion(email.getVersion() + 10);
                    newEmail.setId(email.getId());
                    newEmail.setUnifiedId(email.getUnifiedId());
                    newEmail.setDigitalId(email.getDigitalId());
                    newEmail.setEmail(email.getEmail());
                    emails1.add(newEmail);
                }
            }
            return emails1;
        });

        step("Подготовка тестовых данных", () -> {
            contact.setPhones(newPhones1);
            contact.setEmails(newEmails1);
            contact.setVersion(newUpdateContact.getVersion() + 1);
        });
        var newUpdateContact1 = step("Выполнение put-запроса /partner/contact, код ответа 400", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.BAD_REQUEST,
            updateContact(contact),
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(newUpdateContact1)
                .isNotNull();
            assertThat(newUpdateContact1.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("PUT /partner/contact успешное редактирования дочерних контактов")
    void testUpdateChildContact() {
        var contact = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });

        var newPhones = step("Подготовка новых номеров телефонов", () -> {
            Set<Phone> phones = new HashSet<>();
            if (contact.getPhones() != null) {
                for (var phone : contact.getPhones()) {
                    var newPhone = new Phone();
                    newPhone.setVersion(phone.getVersion());
                    newPhone.setId(phone.getId());
                    newPhone.setUnifiedId(phone.getUnifiedId());
                    newPhone.setDigitalId(phone.getDigitalId());
                    newPhone.setPhone(randomNumeric(13));
                    phones.add(newPhone);
                }
            }
            return phones;
        });

        var newEmails = step("Подготовка новых емайлов", () -> {
            Set<Email> emails = new HashSet<>();
            if (contact.getEmails() != null) {
                for (var email : contact.getEmails()) {
                    var newEmail = new Email();
                    newEmail.setVersion(email.getVersion());
                    newEmail.setId(email.getId());
                    newEmail.setUnifiedId(email.getUnifiedId());
                    newEmail.setDigitalId(email.getDigitalId());
                    newEmail.setEmail(email.getEmail());
                    emails.add(newEmail);
                }
            }
            return emails;
        });

        step("Подготовка тестовых данных", () -> {
            contact.setPhones(newPhones);
            contact.setEmails(newEmails);
        });

        var newUpdateContact = step("Выполнение put-запроса /partner/contact, код ответа 200", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            updateContact(contact),
            Contact.class
        ));

        step("Проверка корректности ответа", () -> {
            assertThat(newUpdateContact)
                .isNotNull();
            assertThat(newUpdateContact.getFirstName())
                .isEqualTo(newUpdateContact.getFirstName());
            assertThat(newUpdateContact.getFirstName())
                .isNotEqualTo(contact.getFirstName());
        });

        var newPhones1 = step("Подготовка новых номеров телефонов", () -> {
            Set<Phone> phones1 = new HashSet<>();
            if (contact.getPhones() != null) {
                for (var phone : contact.getPhones()) {
                    var newPhone = new Phone();
                    newPhone.setVersion(phone.getVersion() + 1);
                    newPhone.setId(phone.getId());
                    newPhone.setUnifiedId(phone.getUnifiedId());
                    newPhone.setDigitalId(phone.getDigitalId());
                    newPhone.setPhone(randomNumeric(13));
                    phones1.add(newPhone);
                }
            }
            return phones1;
        });

        var newEmails1 = step("Подготовка новых емайлов", () -> {
            Set<Email> emails1 = new HashSet<>();
            if (contact.getEmails() != null) {
                for (var email : contact.getEmails()) {
                    var newEmail1 = new Email();
                    newEmail1.setVersion(email.getVersion() + 1);
                    newEmail1.setId(email.getId());
                    newEmail1.setUnifiedId(email.getUnifiedId());
                    newEmail1.setDigitalId(email.getDigitalId());
                    newEmail1.setEmail(randomAlphabetic(64) + "@mail.ru");
                    emails1.add(newEmail1);
                }
            }
            return emails1;
        });

        step("Подготовка тестовых данных", () -> {
            contact.setPhones(newPhones1);
            contact.setEmails(newEmails1);
            contact.setVersion(contact.getVersion() + 1);
        });

        var newUpdateContact1 = step("Выполнение put-запроса /partner/contact, код ответа 200", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            updateContact(contact),
            Contact.class));

        step("Проверка корректности ответа", () -> {
            assertThat(newUpdateContact1)
                .isNotNull();
            assertThat(newUpdateContact1.getFirstName())
                .isEqualTo(newUpdateContact1.getFirstName());
            assertThat(newUpdateContact1.getFirstName())
                .isNotEqualTo(contact.getFirstName());
        });
    }

    @Test
    @DisplayName("PUT /partner/contacts Обновление контактов")
    void testUpdateContact() {
        var contact = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });

        var newUpdateContact = step("Выполнение put-запроса /partner/contact, код ответа 200", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            updateContact(contact),
            Contact.class));

        step("Проверка корректности ответа", () -> {
            assertThat(newUpdateContact)
                .isNotNull();
            assertThat(newUpdateContact.getFirstName())
                .isEqualTo(newUpdateContact.getFirstName());
            assertThat(newUpdateContact.getFirstName())
                .isNotEqualTo(contact.getFirstName());
        });
    }

    @Test
    @DisplayName("PUT /partner/contacts Обновление контактов с пустым емейлом и телефоном")
    void testUpdateContactWithoutEmailsAndPhones() {
        var contact = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });
        var contactUpdate = step("Подготовка тестовых данных", () -> {
            var contactForUpdate = updateContact(contact);
            contactForUpdate.setEmails(null);
            contactForUpdate.setPhones(null);
            return contactForUpdate;
        });

        var newUpdateContact = step("Выполнение put-запроса /partner/contact, код ответа 200", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            contactUpdate,
            Contact.class));

        step("Проверка корректности ответа", () -> {
            assertThat(newUpdateContact)
                .isNotNull();
            assertThat(newUpdateContact.getFirstName())
                .isEqualTo(newUpdateContact.getFirstName());
            assertThat(newUpdateContact.getFirstName())
                .isNotEqualTo(contact.getFirstName());
        });
    }

    @Test
    @DisplayName("NEG PUT /partner/contacts Не успешное повышение версии контактов")
    void negativeTestUpdateContactVersion() {
        var contact = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });
        long version = step("Подготовка тестовых данных", () -> {
            Long versionForUpdate = contact.getVersion() + 1;
            contact.setVersion(versionForUpdate);
            return versionForUpdate;
        });

        var contactError = step("Выполнение put-запроса /partner/contact, код ответа 400", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.BAD_REQUEST,
            updateContact(contact),
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(contactError.getCode())
                .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
            assertThat(contactError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
                .contains("Версия записи в базе данных " + (contact.getVersion() - 1) +
                    " не равна версии записи в запросе version=" + version);
        });
    }

    @Test
    @DisplayName("PUT /partner/contacts Повышение версии контактов")
    void positiveTestUpdateContactVersion() {
        var contact = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });

        var contactUpdate = step("Выполнение put-запроса /partner/contact, код ответа 200", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.OK,
            updateContact(contact),
            Contact.class));

        var checkContact = step("Выполнение get-запроса /partner/contacts, код ответа 200", () -> get(
            baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Contact.class,
            contactUpdate.getDigitalId(), contactUpdate.getId()));

        step("Проверка корректности ответа", () -> {
            assertThat(checkContact)
                .isNotNull();
            assertThat(checkContact.getVersion())
                .isEqualTo(contact.getVersion() + 1);
        });
    }

    @Test
    @DisplayName("DELETE /partner/contacts/{digitalId} Удаление контактов")
    void testDeleteContact() {
        var contact = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });

        var actualContact = step("Выполнение get-запроса /partner/contacts/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Contact.class,
            contact.getDigitalId(), contact.getId()));

        step("Проверка корректности ответа", () -> {
            assertThat(actualContact)
                .isNotNull()
                .isEqualTo(contact);
        });
        var deleteContact = step("Выполнение delete-запроса /partner/contacts/{digitalId}, код ответа 204", () -> delete(
            baseRoutePath + "/contacts" + "/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("ids", actualContact.getId()),
            actualContact.getDigitalId()
        ).getBody());

        step("Проверка корректности ответа", () -> {
            assertThat(deleteContact)
                .isNotNull();
        });

        var searchContact = step("Выполнение get-запроса /partner/contacts/{digitalId}/{id}, код ответа 400", () -> get(
            baseRoutePath + "/contacts" + "/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            Error.class,
            contact.getDigitalId(), contact.getId()));

        step("Проверка корректности ответа", () -> {
            assertThat(searchContact)
                .isNotNull();
            assertThat(searchContact.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("NEG DELETE /partner/contacts/{digitalId} ненайденный документ")
    void testNegativeDeleteContact() {
        var contact = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact1 = createValidContact(partner.getId(), partner.getDigitalId());
            delete(
                baseRoutePath + "/contacts" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", contact1.getId()),
                contact1.getDigitalId());
            return contact1;
        });

        var deleteContact = step("Выполнение delete-запроса /partner/contacts/{digitalId}, код ответа 404", () ->
            delete(
                baseRoutePath + "/contacts" + "/{digitalId}",
                HttpStatus.NOT_FOUND,
                Map.of("ids", contact.getId()),
                contact.getDigitalId()));

        step("Проверка корректности ответа", () ->
            assertThat(deleteContact).isNotNull());
    }

    @Test
    @DisplayName("NEG PUT /partner/contact ненайденный документ")
    void testUpdateDeletedContact() {
        var contact = step("Подготовка тестовых данных", () -> {
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

        var updateContact = step("Выполнение put-запроса /partner/contact, код ответа 404", () -> put(
            baseRoutePath + "/contact",
            HttpStatus.NOT_FOUND,
            updateContact(contact),
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(updateContact).isNotNull();
            assertThat(updateContact.getCode()).isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(updateContact.getMessage()).isEqualTo("Искомая сущность contact с id: " +
                contact.getId() + ", digitalId: " + contact.getDigitalId() + " не найдена");
        });
    }

    public static ContactCreate getValidContact(UUID partnerUuid, String digitalId) {
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
                Set.of("0079241111111"))
            .emails(
                Set.of("a.a.a@sberbank.ru"))
            ;
    }

    protected static Contact createValidContact(UUID partnerUuid, String digitalId) {
        return step("Создание валидных реквизитов", () -> post(
            baseRoutePath + "/contact",
            HttpStatus.CREATED,
            getValidContact(partnerUuid, digitalId),
            Contact.class));
    }

    protected static Contact createValidContact(ContactCreate contact) {
        return post(
            baseRoutePath + "/contact",
            HttpStatus.CREATED,
            contact,
            Contact.class);
    }

    protected static Error createContactWithError(ContactCreate contact) {
        return post(
            baseRoutePath + "/contact",
            HttpStatus.NOT_FOUND,
            contact,
            Error.class);
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

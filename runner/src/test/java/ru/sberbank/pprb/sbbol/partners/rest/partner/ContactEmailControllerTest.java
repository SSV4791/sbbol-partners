package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_DUPLICATE_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactEmailControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact/email";

    @Test
    @DisplayName("POST /partner/contact/emails/view просмотр списка")
    void testViewContactEmail() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            return new EmailsFilter()
                .digitalId(contact.getDigitalId())
                .unifiedIds(
                    List.of(
                        contact.getId()
                    )
                )
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });
        var response = step("Выполнение post-запроса /partner/contact/emails/view, код ответа 200", () -> post(
            "/partner/contact/emails/view",
            HttpStatus.OK,
            filter,
            EmailsResponse.class
        ));
        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getEmails())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.TRUE);
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contact/emails/view параметр pagination = null")
    void testNegativeViewContactEmailNullPagination() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            return new EmailsFilter()
                .digitalId(contact.getDigitalId())
                .unifiedIds(
                    List.of(
                        contact.getId()
                    )
                );
        });
        var response1 = step("Выполнение post-запроса /partner/contact/emails/view, код ответа 400", () -> post(
            "/partner/contact/emails/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        ));
        step("Проверка корректности ответа", () -> {
            assertThat(response1)
                .isNotNull();
            assertThat(response1.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contact/emails/view параметр pagination.offset = null")
    void testViewContactEmailPaginationOffsetIsNull() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            return new EmailsFilter()
                .digitalId(contact.getDigitalId())
                .unifiedIds(
                    List.of(
                        contact.getId()
                    )
                )
                .pagination(new Pagination()
                    .count(0));
        });
        var response = step("Выполнение post-запроса /partner/contact/emails/view, код ответа 400", () -> post(
            "/partner/contact/emails/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        ));
        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            assertThat(response.getMessage()).isEqualTo("При прохождении проверок возникли ошибки");
            assertMessage(response, "pagination.offset", "Поле обязательно для заполнения");
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contact/emails/view параметр pagination.count = null")
    void testNegativeViewContactEmailPaginationCountIsNull() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            createEmail(contact.getId(), contact.getDigitalId());
            return new EmailsFilter()
                .digitalId(contact.getDigitalId())
                .unifiedIds(
                    List.of(
                        contact.getId()
                    )
                )
                .pagination(new Pagination()
                    .offset(0));
        });
        var response = step("Выполнение post-запроса /partner/contact/emails/view, код ответа 400", () -> post(
            "/partner/contact/emails/view",
            HttpStatus.BAD_REQUEST,
            filter,
            Error.class
        ));
        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            assertThat(response.getMessage()).isEqualTo("При прохождении проверок возникли ошибки");
            assertMessage(response, "pagination.count", "Поле обязательно для заполнения");
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contact/emails/view параметр digitalId = null")
    void testNegativeViewContactEmailDigitalIdIsNull() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return new EmailsFilter()
                .digitalId(null)
                .unifiedIds(
                    List.of(
                        contact.getId()
                    )
                )
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });
        var response = step("Выполнение post-запроса /partner/contact/emails/view, код ответа 400", () ->
            post(
                "/partner/contact/emails/view",
                HttpStatus.BAD_REQUEST,
                filter,
                Error.class
            ));
        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            assertThat(response.getMessage()).isEqualTo("При прохождении проверок возникли ошибки");
            assertMessage(response, "digitalId", "Поле обязательно для заполнения");
        });
    }

    @Test
    @DisplayName("POST /partner/contact/emails/view параметр unifiedIds пуст")
    void testNegativeViewContactEmailUnifieldIdsIsEmpty() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return new EmailsFilter()
                .digitalId(contact.getDigitalId())
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });
        var response = step("Выполнение post-запроса /partner/contact/emails/view, код ответа 400", () ->
            post(
                "/partner/contact/emails/view",
                HttpStatus.OK,
                filter,
                EmailsResponse.class
            ));
        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.FALSE);
        });
    }

    @Test
    @DisplayName("POST /partner/contact/email создание email")
    void testCreateContactEmail() {
        var expected = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return getEmail(contact.getId(), contact.getDigitalId());
        });
        var email = step("Выполнение post-запроса /partner/contact/email, код ответа 200", () ->
            createEmail(expected));
        step("Проверка корректности ответа", () -> assertThat(email)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version"
            )
            .isEqualTo(expected));
    }

    @DisplayName("POST /partner/contact/email валидация email")
    @Test
    void testNegativeCreateContactEmail() {
        var expected = step("Подготовка тестовых данных",
            () -> {
                var partner = createValidPartner(randomAlphabetic(10));
                var contact = createValidContact(partner.getId(), partner.getDigitalId());
                return getEmail(contact.getId(), contact.getDigitalId());
            });

        step("Выполнение post-запроса на /partner/contact/email",
            () -> {
                expected.setEmail(randomAlphabetic(64) + "@" + randomAlphabetic(256));
                var emailCreate = post(
                    baseRoutePath,
                    HttpStatus.BAD_REQUEST,
                    expected,
                    Error.class);
                assertThat(emailCreate.getCode())
                    .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
                assertMessage(emailCreate, "email", "должно иметь формат адреса электронной почты");
            });

        step("Выполнение post-запроса на /partner/contact/email",
            () -> {
                expected.setEmail(randomAlphabetic(64) + "@" + randomAlphabetic(254) + "@");
                var emailCreate1 = post(
                    baseRoutePath,
                    HttpStatus.BAD_REQUEST,
                    expected,
                    Error.class);
                assertThat(emailCreate1.getCode())
                    .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
                assertMessage(emailCreate1, "email", "должно иметь формат адреса электронной почты");
            });

        step("Выполнение post-запроса на /partner/contact/email",
            () -> {
                expected.setEmail(randomAlphabetic(65) + "@" + randomAlphabetic(250));
                var emailCreate2 = post(
                    baseRoutePath,
                    HttpStatus.BAD_REQUEST,
                    expected,
                    Error.class);
                assertThat(emailCreate2.getCode())
                    .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
                assertMessage(emailCreate2, "email", "должно иметь формат адреса электронной почты");
            });
    }

    @Test
    @DisplayName("NEG POST /partner/contact/email digitalId = null")
    void testNegativeCreateContactEmailDigitalIdIsNull() {
        var expected = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            var expected1 = getEmail(contact.getId(), contact.getDigitalId());
            expected1.digitalId(null);
            return expected1;
        });
        var emailCreate = step("Выполнение post-запроса /partner/contact/email, код ответа 400", () -> post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            expected,
            Error.class));
        step("Проверка корректности ответа", () -> {
            assertThat(emailCreate.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            assertMessage(emailCreate, "digitalId", "Поле обязательно для заполнения");
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contact/email unifiedId = null")
    void testNegativeCreateContactEmailUnifieldIdIsNull() {
        var expected = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            var expected1 = getEmail(contact.getId(), contact.getDigitalId());
            expected1.unifiedId(null);
            return expected1;
        });
        var emailCreate = step("Выполнение post-запроса /partner/contact/email, код ответа 400", () -> post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            expected,
            Error.class));
        step("Проверка корректности ответа", () -> {
            assertThat(emailCreate.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            assertMessage(emailCreate, "unifiedId", "Поле обязательно для заполнения");
        });
    }

    @Test
    @DisplayName("NEG POST /partner/contact/email без созданного contact")
    void testNegativeCreateContactEmailIsNull() {
        var partner = step("Подготовка тестовых данных", () -> createValidPartner(randomAlphabetic(10)));
        var emailCreate = step("Выполнение post-запроса /partner/contact/email, код ответа 400", () -> post(
            baseRoutePath,
            HttpStatus.NOT_FOUND,
            getEmail(partner.getId(), partner.getDigitalId()),
            Error.class));
        step("Проверка корректности ответа", () -> {
            assertThat(emailCreate.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(emailCreate.getMessage())
                .contains("Искомая сущность contact c id: " + partner.getId() + " не найдена");
        });
    }

    @Test
    void testUpdateContactEmail() {
        var email = step("Подготовка тестовых данных",
            () -> {
                var partner = createValidPartner(randomAlphabetic(10));
                var contact = createValidContact(partner.getId(), partner.getDigitalId());
                return createEmail(contact.getId(), contact.getDigitalId());
            });

        var newUpdateEmail = step("Выполнение put-запроса /partner/contact/email",
            () -> put(
                baseRoutePath,
                HttpStatus.OK,
                updateEmail(email),
                Email.class
            ));

        step("Проверка корректности ответа",
            () -> {
                assertThat(newUpdateEmail)
                    .isNotNull();
                assertThat(newUpdateEmail.getEmail())
                    .isNotEqualTo(email.getEmail());
            });
    }

    @Test
    void testNegativeUpdateContactEmail() {
        var contact = step("Подготовка тестовых данных. Contact", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });

        step("Выполнене put-запроса /partner/contact/email. Проверка email",
            () -> {
                var email = createEmail(contact.getId(), contact.getDigitalId());
                updateEmail(email);
                email.setEmail(randomAlphabetic(64) + "@" + randomAlphabetic(256));
                var emailError = put(
                    baseRoutePath,
                    HttpStatus.BAD_REQUEST,
                    email,
                    Error.class
                );
                assertThat(emailError.getCode())
                    .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            });

        step("Выполнене put-запроса /partner/contact/email. Проверка email",
            () -> {
                var email1 = createEmail(contact.getId(), contact.getDigitalId());

                updateEmail(email1);
                email1.setEmail(randomAlphabetic(64) + "@" + randomAlphabetic(254) + "@");
                var emailError1 = put(
                    baseRoutePath,
                    HttpStatus.BAD_REQUEST,
                    email1,
                    Error.class
                );
                assertThat(emailError1.getCode())
                    .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            });

        step("Выполнене put-запроса /partner/contact/email. Проверка email",
            () -> {
                var email2 = createEmail(contact.getId(), contact.getDigitalId());

                updateEmail(email2);
                email2.setEmail(randomAlphabetic(65) + "@" + randomAlphabetic(250));
                var emailError2 = put(
                    baseRoutePath,
                    HttpStatus.BAD_REQUEST,
                    email2,
                    Error.class
                );
                assertThat(emailError2.getCode())
                    .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            });
    }

    @Test
    void negativeTestUpdateEmailVersion() {
        var email = step("Подготовка тестовых данных. Email",
            () -> {
                var partner = createValidPartner(randomAlphabetic(10));
                var contact = createValidContact(partner.getId(), partner.getDigitalId());
                return createEmail(contact.getId(), contact.getDigitalId());
            });

        var version = step("Подготовка тестовых данных. Version",
            () -> {
                Long versionTest = email.getVersion() + 1;
                email.setVersion(versionTest);
                return versionTest;
            });

        var emailError = step("Выполнене put-запроса /partner/contact/email",
            () -> put(
                baseRoutePath,
                HttpStatus.BAD_REQUEST,
                updateEmail(email),
                Error.class
            ));

        step("Проверка корректности ответа", () -> {
            assertThat(emailError.getCode())
                .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
            assertThat(emailError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
                .contains("Версия записи в базе данных " + (email.getVersion() - 1) +
                    " не равна версии записи в запросе version=" + version);
        });
    }

    @Test
    void positiveTestUpdateEmailVersion() {
        var email = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            return createEmail(contact.getId(), contact.getDigitalId());
        });

        var updateEmail = step("Выполнение put-запроса /partner/contact/email",
            () -> put(
                baseRoutePath,
                HttpStatus.OK,
                updateEmail(email),
                Email.class));

        var checkEmail = step("Подготовка тестовых данных", () -> {
            var emailTest = new EmailsFilter();
            emailTest.digitalId(updateEmail.getDigitalId());
            emailTest.unifiedIds(Collections.singletonList(updateEmail.getUnifiedId()));
            emailTest.pagination(new Pagination()
                .count(4)
                .offset(0));
            return emailTest;
        });

        var response = step("Выполнение post-запроса /partner/contact/emails/view",
            () -> post(
                "/partner/contact/emails/view",
                HttpStatus.OK,
                checkEmail,
                EmailsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getEmails())
                .isNotNull();
            assertThat(response.getEmails()
                .stream()
                .filter(curEmail -> curEmail.getId()
                    .equals(email.getId()))
                .map(Email::getVersion)
                .findAny()
                .orElse(null))
                .isEqualTo(email.getVersion() + 1);
        });
    }

    @Test
    @DisplayName("NEG PUT /partner/contact/email digitalId = null")
    void testNegativeUpdateContactEmailDigitalIdIsNull() {
        var updateEmail = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            var email = createEmail(contact.getId(), contact.getDigitalId());
            return email.digitalId(null);
        });
        var newUpdateEmail = step("Выполнение put-запроса /partner/contact/email, код ответа 404", () -> put(
            baseRoutePath,
            HttpStatus.NOT_FOUND,
            updateEmail,
            Error.class
        ));
        step("Проверка корректности ответа", () -> {
            assertThat(newUpdateEmail)
                .isNotNull();
            assertThat(newUpdateEmail.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(newUpdateEmail.getMessage())
                .contains("Искомая сущность email c id: " + updateEmail.getId() + " не найдена");
        });
    }

    @Test
    @DisplayName("NEG PUT /partner/contact/email unifiedId = null")
    void testNegativeUpdateContactEmailUnifieldIdIsNull() {
        var updateEmail = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            var contact = createValidContact(partner.getId(), partner.getDigitalId());
            var email = createEmail(contact.getId(), contact.getDigitalId());
            return email.unifiedId(null);
        });
        var newUpdateEmail = step("Выполнение put-запроса /partner/contact/email, код ответа 404", () -> put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            updateEmail,
            Error.class
        ));
        step("Проверка корректности ответа", () -> {
            assertThat(newUpdateEmail)
                .isNotNull();
            assertThat(newUpdateEmail.getCode())
                .isEqualTo(MODEL_DUPLICATE_EXCEPTION.getValue());
            assertThat(newUpdateEmail.getMessage())
                .contains("При прохождении проверок возникли ошибки");
        });
    }

    @Test
    void testDeleteContactEmail() {
        var contact = step("Подготовка тестовых данных. Contact", () -> {
            var partner = createValidPartner(randomAlphabetic(10));
            return createValidContact(partner.getId(), partner.getDigitalId());
        });

        var filter1 = step("Подготовка тестовых данных. Filter",
            () -> new EmailsFilter()
                .digitalId(contact.getDigitalId())
                .unifiedIds(
                    List.of(
                        contact.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0)));

        var actualEmail = step("Проверка post-запроса /partner/contact/emails/view",
            () -> {
                var actualEmailTest = post(
                    "/partner/contact/emails/view",
                    HttpStatus.OK,
                    filter1,
                    EmailsResponse.class
                );
                assertThat(actualEmailTest)
                    .isNotNull();
                return actualEmailTest;
            });

        step("Проверка delete-запроса /partner/contact/emails/{digitalId}",
            () -> {
                var deleteEmail =
                    delete(
                        "/partner/contact/emails/{digitalId}",
                        HttpStatus.NO_CONTENT,
                        Map.of("ids", actualEmail.getEmails().get(0).getId()),
                        contact.getDigitalId()
                    ).getBody();
                assertThat(deleteEmail)
                    .isNotNull();
            });

        step("Проверка повторного delete-запроса /partner/contact/emails/{digitalId}",
            () -> {
                var deleteEmail2 =
                    delete(
                        "/partner/contact/emails/{digitalId}",
                        HttpStatus.NOT_FOUND,
                        Map.of("ids", actualEmail.getEmails().get(0).getId()),
                        contact.getDigitalId()
                    ).getBody();
                assertThat(deleteEmail2)
                    .isNotNull();
            });

        step("Проверка после удаления post-запроса /partner/contact/emails/view",
            () -> {
                var searchEmail = post(
                    "/partner/contact/emails/view",
                    HttpStatus.OK,
                    filter1,
                    EmailsResponse.class
                );
                assertThat(searchEmail.getEmails())
                    .isNull();
            });
    }

    private static EmailCreate getEmail(UUID contactUuid, String digitalId) {
        return new EmailCreate()
            .unifiedId(contactUuid)
            .digitalId(digitalId)
            .email(randomAlphabetic(10) + "@mail.ru");
    }

    private static Email createEmail(UUID contactUuid, String digitalId) {
        return post(baseRoutePath, HttpStatus.CREATED, getEmail(contactUuid, digitalId), Email.class);
    }

    private static Email createEmail(EmailCreate email) {
        return post(baseRoutePath, HttpStatus.CREATED, email, Email.class);
    }

    public static Email updateEmail(Email email) {
        return new Email()
            .email(randomAlphabetic(64) + "@mail.ru")
            .id(email.getId())
            .version(email.getVersion())
            .unifiedId(email.getUnifiedId())
            .digitalId(email.getDigitalId());
    }

    private void assertMessage(Error response, String field, String message) {
        Optional<Descriptions> description = response.getDescriptions().stream()
            .filter(value -> value.getField().equals(field)).findFirst();
        assertThat(description).isPresent();
        assertThat(description.get().getMessage()).contains(message);
    }
}

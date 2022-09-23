package ru.sberbank.pprb.sbbol.partners.rest.partner;

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

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactEmailControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact/email";

    @Test
    void testNegativeViewContactEmail() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        createEmail(contact.getId(), contact.getDigitalId());
        createEmail(contact.getId(), contact.getDigitalId());
        createEmail(contact.getId(), contact.getDigitalId());
        createEmail(contact.getId(), contact.getDigitalId());

        var filter1 = new EmailsFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            )
            .pagination(new Pagination()
                .count(4));
        var response = post(
            "/partner/contact/emails/view",
            HttpStatus.BAD_REQUEST,
            filter1,
            Error.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var filter2 = new EmailsFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            );
        var response1 = post(
            "/partner/contact/emails/view",
            HttpStatus.BAD_REQUEST,
            filter2,
            Error.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var filter3 = new EmailsFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            )
            .pagination(new Pagination()
                .offset(0));
        var response2 = post(
            "/partner/contact/emails/view",
            HttpStatus.BAD_REQUEST,
            filter3,
            Error.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
    }

    @Test
    void testViewContactEmail() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        createEmail(contact.getId(), contact.getDigitalId());
        createEmail(contact.getId(), contact.getDigitalId());
        createEmail(contact.getId(), contact.getDigitalId());
        createEmail(contact.getId(), contact.getDigitalId());

        var filter1 = new EmailsFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            )
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            "/partner/contact/emails/view",
            HttpStatus.OK,
            filter1,
            EmailsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getEmails().size())
            .isEqualTo(4);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }


    @Test
    void testCreateContactEmail() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var expected = getEmail(contact.getId(), contact.getDigitalId());
        var email = createEmail(expected);
        assertThat(email)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "version"
            )
            .isEqualTo(expected);
    }

    @Test
    void testNegativeCreateContactEmail() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var expected = getEmail(contact.getId(), contact.getDigitalId());
        expected.setEmail(randomAlphabetic(64) + "@" + randomAlphabetic(256));
        var emailCreate = post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            expected,
            Error.class);
        assertThat(emailCreate.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        expected.setEmail(randomAlphabetic(64) + "@" + randomAlphabetic(254) + "@");
        var emailCreate1 = post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            expected,
            Error.class);
        assertThat(emailCreate1.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        expected.setEmail(randomAlphabetic(65) + "@" + randomAlphabetic(250));
        var emailCreate2 = post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            expected,
            Error.class);
        assertThat(emailCreate2.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
    }

    @Test
    void testUpdateContactEmail() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var email = createEmail(contact.getId(), contact.getDigitalId());
        var newUpdateEmail = put(
            baseRoutePath,
            HttpStatus.OK,
            updateEmail(email),
            Email.class
        );

        assertThat(newUpdateEmail)
            .isNotNull();
        assertThat(newUpdateEmail.getEmail())
            .isEqualTo(newUpdateEmail.getEmail());
        assertThat(newUpdateEmail.getEmail())
            .isNotEqualTo(email.getEmail());
    }

    @Test
    void testNegativeUpdateContactEmail() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
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
    }

    @Test
    void negativeTestUpdateEmailVersion() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var email = createEmail(contact.getId(), contact.getDigitalId());
        Long version = email.getVersion() + 1;
        email.setVersion(version);
        var emailError = put(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            updateEmail(email),
            Error.class
        );
        assertThat(emailError.getCode())
            .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
        assertThat(emailError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains("Версия записи в базе данных " + (email.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    void positiveTestUpdateEmailVersion() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var email = createEmail(contact.getId(), contact.getDigitalId());
        var updateEmail = put(
            baseRoutePath,
            HttpStatus.OK,
            updateEmail(email),
            Email.class
        );
        var checkEmail = new EmailsFilter();
        checkEmail.digitalId(updateEmail.getDigitalId());
        checkEmail.unifiedIds(Collections.singletonList(updateEmail.getUnifiedId()));
        checkEmail.pagination(new Pagination()
            .count(4)
            .offset(0));
        var response = post(
            "/partner/contact/emails/view",
            HttpStatus.OK,
            checkEmail,
            EmailsResponse.class);
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
    }

    @Test
    void testDeleteContactEmail() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());

        var filter1 = new EmailsFilter()
            .digitalId(contact.getDigitalId())
            .unifiedIds(
                List.of(
                    contact.getId()
                )
            )
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var actualEmail = post(
            "/partner/contact/emails/view",
            HttpStatus.OK,
            filter1,
            EmailsResponse.class
        );
        assertThat(actualEmail)
            .isNotNull();

        var deleteEmail =
            delete(
                "/partner/contact/emails/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", actualEmail.getEmails().get(0).getId()),
                contact.getDigitalId()
            ).getBody();
        assertThat(deleteEmail)
            .isNotNull();

        var searchEmail = post(
            "/partner/contact/emails/view",
            HttpStatus.OK,
            filter1,
            EmailsResponse.class
        );
        assertThat(searchEmail.getEmails())
            .isNull();
    }

    private static EmailCreate getEmail(String contactUuid, String digitalId) {
        return new EmailCreate()
            .unifiedId(contactUuid)
            .digitalId(digitalId)
            .email(randomAlphabetic(10) + "@mail.ru");
    }

    private static Email createEmail(String contactUuid, String digitalId) {
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
}

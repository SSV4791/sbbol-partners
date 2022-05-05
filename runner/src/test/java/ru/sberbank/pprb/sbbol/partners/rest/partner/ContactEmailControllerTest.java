package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.ContactControllerTest.createValidContact;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class ContactEmailControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/contact/email";

    @Test
    @AllureId("34150")
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
            baseRoutePath + "/view",
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
    @AllureId("34181")
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
    @AllureId("34130")
    void testUpdateContactEmail() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var email = createEmail(contact.getId(), contact.getDigitalId());
        var newUpdateEmail = put(
            baseRoutePath,
            HttpStatus.OK,
            updateEmail(email),
            EmailResponse.class
        );

        assertThat(newUpdateEmail)
            .isNotNull();
        assertThat(newUpdateEmail.getEmail().getEmail())
            .isEqualTo(newUpdateEmail.getEmail().getEmail());
        assertThat(newUpdateEmail.getEmail().getEmail())
            .isNotEqualTo(email.getEmail());
        assertThat(newUpdateEmail.getErrors())
            .isNull();
    }

    @Test
    @AllureId("36936")
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
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(emailError.getText())
            .contains("Версия записи в базе данных " + (email.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    @AllureId("36938")
    void positiveTestUpdateEmailVersion() {
        var partner = createValidPartner(randomAlphabetic(10));
        var contact = createValidContact(partner.getId(), partner.getDigitalId());
        var email = createEmail(contact.getId(), contact.getDigitalId());
        var updateEmail = put(
            baseRoutePath,
            HttpStatus.OK,
            updateEmail(email),
            EmailResponse.class
        );
        var checkEmail = new EmailsFilter();
        checkEmail.digitalId(updateEmail.getEmail().getDigitalId());
        checkEmail.unifiedIds(Collections.singletonList(updateEmail.getEmail().getUnifiedId()));
        checkEmail.pagination(new Pagination()
            .count(4)
            .offset(0));
        var response = post(
            baseRoutePath + "/view",
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
        assertThat(response.getErrors())
            .isNull();
    }

    @Test
    @AllureId("34137")
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
            baseRoutePath + "/view",
            HttpStatus.OK,
            filter1,
            EmailsResponse.class
        );
        assertThat(actualEmail)
            .isNotNull();

        var deleteEmail =
            delete(
                baseRoutePath + "/{digitalId}" + "/{id}",
                HttpStatus.NO_CONTENT,
                contact.getDigitalId(), actualEmail.getEmails().get(0).getId()
            ).getBody();
        assertThat(deleteEmail)
            .isNotNull();

        var searchEmail = post(
            baseRoutePath + "/view",
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
            .email(randomAlphabetic(10));
    }

    private static Email createEmail(String contactUuid, String digitalId) {
        var emailResponse = post(baseRoutePath, HttpStatus.CREATED, getEmail(contactUuid, digitalId), EmailResponse.class);
        assertThat(emailResponse)
            .isNotNull();
        assertThat(emailResponse.getErrors())
            .isNull();
        return emailResponse.getEmail();
    }

    private static Email createEmail(EmailCreate email) {
        var emailResponse = post(baseRoutePath, HttpStatus.CREATED, email, EmailResponse.class);
        assertThat(emailResponse)
            .isNotNull();
        assertThat(emailResponse.getErrors())
            .isNull();
        return emailResponse.getEmail();
    }

    public static Email updateEmail(Email email) {
        return new Email()
            .email(randomAlphabetic(10) + "@.ru")
            .id(email.getId())
            .version(email.getVersion())
            .unifiedId(email.getUnifiedId())
            .digitalId(email.getDigitalId());
    }
}

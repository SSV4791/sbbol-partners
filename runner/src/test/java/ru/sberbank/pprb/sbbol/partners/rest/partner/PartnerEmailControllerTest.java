package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class PartnerEmailControllerTest extends AbstractIntegrationWithOutSbbolTest {

    public static final String baseRoutePath = "/partner/email";

    @Test
    @AllureId("34136")
    void testViewPartnerEmail() {
        Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createEmail(partner.getId(), partner.getDigitalId());
        createEmail(partner.getId(), partner.getDigitalId());
        createEmail(partner.getId(), partner.getDigitalId());
        createEmail(partner.getId(), partner.getDigitalId());

        var filter1 = new EmailsFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(
                List.of(
                    partner.getId()
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
    @AllureId("34133")
    void testCreatePartnerEmail() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var expected = getEmail(partner.getId(), partner.getDigitalId());
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
    @AllureId("34139")
    void testUpdatePartnerEmail() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var email = createEmail(partner.getId(), partner.getDigitalId());
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
    @AllureId("36947")
    void negativeTestUpdateEmailVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var email = createEmail(partner.getId(), partner.getDigitalId());
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
    @AllureId("36948")
    void positiveTestUpdateEmailVersion() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var email = createEmail(partner.getId(), partner.getDigitalId());
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
    @AllureId("34192")
    void testDeletePartnerEmail() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));

        var filter1 = new EmailsFilter()
            .digitalId(partner.getDigitalId())
            .unifiedIds(
                List.of(
                    partner.getId()
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
                partner.getDigitalId(), actualEmail.getEmails().get(0).getId()
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

    private static EmailCreate getEmail(String partnerUuid, String digitalId) {
        return new EmailCreate()
            .unifiedId(partnerUuid)
            .digitalId(digitalId)
            .email(RandomStringUtils.randomAlphabetic(10));
    }

    private static Email createEmail(String partnerUuid, String digitalId) {
        var emailResponse = post(baseRoutePath, HttpStatus.CREATED, getEmail(partnerUuid, digitalId), EmailResponse.class);
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

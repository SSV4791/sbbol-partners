package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import java.util.List;

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
        String newEmail = "bbbb@sber.ru";

        var updateEmail = new Email();
        updateEmail.id(email.getId());
        updateEmail.unifiedId(email.getUnifiedId());
        updateEmail.digitalId(email.getDigitalId());
        updateEmail.email(newEmail);
        updateEmail.setVersion(email.getVersion() + 1);
        var newUpdateEmail = put(baseRoutePath, updateEmail, EmailResponse.class);

        assertThat(newUpdateEmail)
            .isNotNull();
        assertThat(newUpdateEmail.getEmail().getEmail())
            .isEqualTo(newEmail);
        assertThat(newUpdateEmail.getErrors())
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
            filter1,
            EmailsResponse.class
        );
        assertThat(actualEmail)
            .isNotNull();

        var deleteEmail =
            delete(
                baseRoutePath + "/{digitalId}" + "/{id}",
                partner.getDigitalId(), actualEmail.getEmails().get(0).getId()
            );
        assertThat(deleteEmail)
            .isNotNull();

        var searchEmail = post(
            baseRoutePath + "/view",
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
        var emailResponse = createPost(baseRoutePath, getEmail(partnerUuid, digitalId), EmailResponse.class);
        assertThat(emailResponse)
            .isNotNull();
        assertThat(emailResponse.getErrors())
            .isNull();
        return emailResponse.getEmail();
    }

    private static Email createEmail(EmailCreate email) {
        var emailResponse = createPost(baseRoutePath, email, EmailResponse.class);
        assertThat(emailResponse)
            .isNotNull();
        assertThat(emailResponse.getErrors())
            .isNull();
        return emailResponse.getEmail();
    }
}

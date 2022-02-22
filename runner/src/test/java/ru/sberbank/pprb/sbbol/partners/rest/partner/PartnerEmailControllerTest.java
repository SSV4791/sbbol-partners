package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class PartnerEmailControllerTest extends AbstractIntegrationWithOutSbbolTest {

    public static final String baseRoutePath = "/partner/email";

    @Test
    void testViewPartnerEmail() {
        Partner partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createEmail(partner.getId());
        createEmail(partner.getId());
        createEmail(partner.getId());
        createEmail(partner.getId());

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
    void testCreatePartnerEmail() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var email = createEmail(partner.getId());
        assertThat(email)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid")
            .isEqualTo(email);
    }

    @Test
    void testUpdatePartnerEmail() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var email = createEmail(partner.getId());
        String newEmail = "bbbb@sber.ru";

        var updateEmail = new Email();
        updateEmail.id(email.getId());
        updateEmail.unifiedId(email.getUnifiedId());
        updateEmail.email(newEmail);
        var newUpdateEmail = put(baseRoutePath, updateEmail, EmailResponse.class);

        assertThat(newUpdateEmail)
            .isNotNull();
        assertThat(newUpdateEmail.getEmail().getEmail())
            .isEqualTo(newEmail);
        assertThat(newUpdateEmail.getErrors())
            .isNull();
    }

    @Test
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

    private static Email getEmail(String partnerUuid) {
        return new Email()
            .version(0L)
            .id(UUID.randomUUID().toString())
            .unifiedId(partnerUuid)
            .email(RandomStringUtils.randomAlphabetic(10));
    }

    private static Email createEmail(String partnerUuid) {
        var emailResponse = createPost(baseRoutePath, getEmail(partnerUuid), EmailResponse.class);
        assertThat(emailResponse)
            .isNotNull();
        assertThat(emailResponse.getErrors())
            .isNull();
        return emailResponse.getEmail();
    }
}

package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class ContactMapperTest extends BaseConfiguration {

    private ContactMapper mapper;

    @Mock
    private ContactPhoneMapper contactPhoneMapper;

    @Mock
    private ContactEmailMapper contactEmailMapper;

    @BeforeEach
    void before() {
        mapper = new ContactMapperImpl(contactEmailMapper, contactPhoneMapper);
    }

    @Test
    @AllureId("34384")
    void testToContact() {
        Contact expected = factory.manufacturePojo(Contact.class);
        for (Email email : expected.getEmails()) {
            email.setUnifiedId(expected.getId());
        }
        for (Phone phone : expected.getPhones()) {
            phone.setUnifiedId(expected.getId());
        }
        ContactEntity actual = mapper.toContact(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "phones",
                "emails"
            )
            .isEqualTo(mapper.toContact(actual));
    }

    @Test
    @AllureId("34384")
    void testToContactPhoneString() {
        List<String> phones = factory.manufacturePojo(ArrayList.class, String.class);
        var digitalId = RandomStringUtils.randomAlphanumeric(10);
        List<ContactPhoneEntity> actual = mapper.toPhone(phones, digitalId);
        for (ContactPhoneEntity contactPhone : actual) {
            assertThat(contactPhone.getDigitalId())
                .isEqualTo(digitalId);
            assertThat(phones)
                .contains(contactPhone.getPhone());
        }
    }

    @Test
    @AllureId("34379")
    void testToContactEmailString() {
        List<String> emails = factory.manufacturePojo(ArrayList.class, String.class);
        var digitalId = RandomStringUtils.randomAlphanumeric(10);
        List<ContactEmailEntity> actual = mapper.toEmail(emails, digitalId);
        for (ContactEmailEntity contactEmail : actual) {
            assertThat(contactEmail.getDigitalId())
                .isEqualTo(digitalId);
            assertThat(emails)
                .contains(contactEmail.getEmail());
        }
    }
}

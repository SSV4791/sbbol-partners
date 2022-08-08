package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactEmailMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactPhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ContactMapperTest extends BaseUnitConfiguration {

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
    void testToContacts() {
        Set<ContactCreateFullModel> expected = factory.manufacturePojo(HashSet.class, ContactCreateFullModel.class);
        var digitalId = factory.manufacturePojo(String.class);
        var unifiedUuid = factory.manufacturePojo(UUID.class);
        var actual = mapper.toContacts(expected, digitalId, unifiedUuid);
        assertThat(actual)
            .isNotNull();
        assertThat(expected)
            .hasSameSizeAs(actual);
        for(var actualEntity: actual) {
            assertThat(digitalId)
                .isEqualTo(actualEntity.getDigitalId());
            assertThat(unifiedUuid)
                .isEqualTo(actualEntity.getPartnerUuid());
        }
    }

    @Test
    void testToContractWithContactCreateFullModel() {
        var expected = factory.manufacturePojo(ContactCreateFullModel.class);
        var digitalId = factory.manufacturePojo(String.class);
        var unifiedUuid = factory.manufacturePojo(UUID.class);
        var actual = mapper.toContact(expected, digitalId, unifiedUuid);

        assertThat(actual)
            .isNotNull();
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "emails",
                "phones",
                "legalForm"
            )
            .isEqualTo(actual);
        assertThat(digitalId)
            .isEqualTo(actual.getDigitalId());
        assertThat(unifiedUuid)
            .isEqualTo(actual.getPartnerUuid());
        assertThat(ContactMapper.toLegalType(expected.getLegalForm()))
            .isEqualTo(actual.getType());

        assertThat(actual.getEmails())
            .isNotNull();
        for(var email: actual.getEmails()) {
            assertThat(expected.getEmails())
                .contains(email.getEmail());
        }

        assertThat(actual.getPhones())
            .isNotNull();
        for(var phone: actual.getPhones()) {
            assertThat(expected.getPhones())
                .contains(phone.getPhone());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testToContactPhoneString() {
        Set<String> phones = factory.manufacturePojo(HashSet.class, String.class);
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
    @SuppressWarnings("unchecked")
    void testToContactEmailString() {
        Set<String> emails = factory.manufacturePojo(HashSet.class, String.class);
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

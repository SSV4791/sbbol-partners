package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactEmailMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapperImpl_;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactPhoneMapperImpl;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneChangeFullModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = {
        ContactMapperImpl.class,
        ContactMapperImpl_.class,
        ContactPhoneMapperImpl.class,
        ContactEmailMapperImpl.class
    }
)
class ContactMapperTest extends BaseUnitConfiguration {

    @Autowired
    private ContactMapper mapper;

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
        for (var actualEntity : actual) {
            assertThat(digitalId)
                .isEqualTo(actualEntity.getDigitalId());
            assertThat(unifiedUuid)
                .isEqualTo(actualEntity.getPartnerId());
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
            .isEqualTo(actual.getPartnerId());
        assertThat(actual.getLegalForm())
            .isEqualTo(expected.getLegalForm());
        assertThat(actual.getEmails())
            .isEqualTo(expected.getEmails());
        assertThat(actual.getPhones())
            .isEqualTo(expected.getPhones());
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

    @Test
    void mapContactChangeFullModelToContact() {
        var contactChangeFullModel = factory.manufacturePojo(ContactChangeFullModel.class);
        var digitalId = factory.manufacturePojo(String.class);
        var partnerId = UUID.randomUUID();
        var actualContact = mapper.toContact(contactChangeFullModel, digitalId, partnerId);
        var expectedContact = new Contact()
            .id(contactChangeFullModel.getId())
            .digitalId(digitalId)
            .partnerId(partnerId)
            .legalForm(contactChangeFullModel.getLegalForm())
            .firstName(contactChangeFullModel.getFirstName())
            .middleName(contactChangeFullModel.getMiddleName())
            .secondName(contactChangeFullModel.getSecondName())
            .orgName(contactChangeFullModel.getOrgName())
            .position(contactChangeFullModel.getPosition())
            .version(contactChangeFullModel.getVersion());
        Optional.ofNullable(contactChangeFullModel.getPhones())
            .ifPresent(phones -> {
                expectedContact.setPhones(
                    phones.stream()
                        .map(phoneChangeFullModel ->
                            new Phone()
                                .id(phoneChangeFullModel.getId())
                                .version(phoneChangeFullModel.getVersion())
                                .phone(phoneChangeFullModel.getPhone())
                                .digitalId(digitalId)
                                .unifiedId(contactChangeFullModel.getId())
                        )
                        .collect(Collectors.toSet())
                );
            });
        Optional.ofNullable(contactChangeFullModel.getEmails())
            .ifPresent(emails -> {
                expectedContact.setEmails(
                    emails.stream()
                        .map(phoneChangeFullModel ->
                            new Email()
                                .id(phoneChangeFullModel.getId())
                                .version(phoneChangeFullModel.getVersion())
                                .email(phoneChangeFullModel.getEmail())
                                .digitalId(digitalId)
                                .unifiedId(contactChangeFullModel.getId())
                        )
                        .collect(Collectors.toSet())
                );
            });
        assertThat(actualContact)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedContact);
    }

    @Test
    void mapContactChangeFullModelToContactCreate() {
        var contactChangeFullModel = factory.manufacturePojo(ContactChangeFullModel.class);
        var digitalId = factory.manufacturePojo(String.class);
        var partnerId = UUID.randomUUID();
        var actualContactCreate = mapper.toContactCreate(contactChangeFullModel, digitalId, partnerId);
        var expectedContactCreate = new ContactCreate()
            .digitalId(digitalId)
            .partnerId(partnerId)
            .legalForm(contactChangeFullModel.getLegalForm())
            .firstName(contactChangeFullModel.getFirstName())
            .middleName(contactChangeFullModel.getMiddleName())
            .secondName(contactChangeFullModel.getSecondName())
            .orgName(contactChangeFullModel.getOrgName())
            .position(contactChangeFullModel.getPosition());
        Optional.ofNullable(contactChangeFullModel.getPhones())
            .ifPresent(phones ->
                expectedContactCreate.setPhones(
                    phones.stream()
                        .map(PhoneChangeFullModel::getPhone)
                        .collect(Collectors.toSet())
                ));
        Optional.ofNullable(contactChangeFullModel.getEmails())
            .ifPresent(emails ->
                expectedContactCreate.setEmails(
                    emails.stream()
                        .map(EmailChangeFullModel::getEmail)
                        .collect(Collectors.toSet())
                ));
        assertThat(actualContactCreate)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedContactCreate);
    }

    @Test
    void patchContactWithoutMergedPhonesAndEmails() {
        var contact = new Contact()
            .firstName("Updated first name");
        var actualContactEntity = factory.manufacturePojo(ContactEntity.class);
        mapper.patchContact(contact, actualContactEntity);
        var expectedContactEntity = new ContactEntity();
        expectedContactEntity.setUuid(actualContactEntity.getUuid());
        expectedContactEntity.setDigitalId(actualContactEntity.getDigitalId());
        expectedContactEntity.setPartnerUuid(actualContactEntity.getPartnerUuid());
        expectedContactEntity.setFirstName(contact.getFirstName());
        expectedContactEntity.setMiddleName(actualContactEntity.getMiddleName());
        expectedContactEntity.setSecondName(actualContactEntity.getSecondName());
        expectedContactEntity.setOrgName(actualContactEntity.getOrgName());
        expectedContactEntity.setPosition(actualContactEntity.getPosition());
        expectedContactEntity.setVersion(actualContactEntity.getVersion());
        expectedContactEntity.setPhones(actualContactEntity.getPhones());
        expectedContactEntity.setEmails(actualContactEntity.getEmails());
        expectedContactEntity.setType(actualContactEntity.getType());
        assertThat(actualContactEntity)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .ignoringFields(
                "lastModifiedDate"
            )
            .isEqualTo(expectedContactEntity);
    }

    @Test
    void patchContactWithMergedPhonesAndEmails() {
        var updatedPhone = factory.manufacturePojo(Phone.class);
        updatedPhone.setId(UUID.randomUUID());
        var updatedEmail = factory.manufacturePojo(Email.class);
        updatedEmail.setId(UUID.randomUUID());
        var updatedPhones = Set.of(updatedPhone);
        var updatedEmails = Set.of(updatedEmail);
        var contact = new Contact()
            .phones(updatedPhones)
            .emails(updatedEmails);
        var actualContactEntity = factory.manufacturePojo(ContactEntity.class);
        var initialPhones = new ArrayList<>(actualContactEntity.getPhones());
        var initialEmails = new ArrayList<>(actualContactEntity.getEmails());
        mapper.patchContact(contact, actualContactEntity);
        var expectedContactEntity = new ContactEntity();
        expectedContactEntity.setUuid(actualContactEntity.getUuid());
        expectedContactEntity.setDigitalId(actualContactEntity.getDigitalId());
        expectedContactEntity.setPartnerUuid(actualContactEntity.getPartnerUuid());
        expectedContactEntity.setFirstName(actualContactEntity.getFirstName());
        expectedContactEntity.setMiddleName(actualContactEntity.getMiddleName());
        expectedContactEntity.setSecondName(actualContactEntity.getSecondName());
        expectedContactEntity.setOrgName(actualContactEntity.getOrgName());
        expectedContactEntity.setPosition(actualContactEntity.getPosition());
        expectedContactEntity.setVersion(actualContactEntity.getVersion());
        expectedContactEntity.setType(actualContactEntity.getType());
        expectedContactEntity.setPhones(
            Stream.concat(
                initialPhones.stream().peek(phone -> phone.setContact(actualContactEntity)),
                updatedPhones.stream().map(phone -> {
                    var contactPhoneEntity = new ContactPhoneEntity();
                    contactPhoneEntity.setUuid(phone.getId());
                    contactPhoneEntity.setDigitalId(phone.getDigitalId());
                    contactPhoneEntity.setVersion(phone.getVersion());
                    contactPhoneEntity.setContact(actualContactEntity);
                    contactPhoneEntity.setPhone(phone.getPhone());
                    return contactPhoneEntity;
                })
            ).collect(Collectors.toList())
        );
        expectedContactEntity.setEmails(
            Stream.concat(
                initialEmails.stream().peek(email -> email.setContact(actualContactEntity)),
                updatedEmails.stream().map(email -> {
                    var contactEmailEntity = new ContactEmailEntity();
                    contactEmailEntity.setUuid(email.getId());
                    contactEmailEntity.setDigitalId(email.getDigitalId());
                    contactEmailEntity.setVersion(email.getVersion());
                    contactEmailEntity.setContact(actualContactEntity);
                    contactEmailEntity.setEmail(email.getEmail());
                    return contactEmailEntity;
                })
            ).collect(Collectors.toList())
        );
        expectedContactEntity.setEmails(actualContactEntity.getEmails());
        assertThat(actualContactEntity)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .ignoringFields(
                "lastModifiedDate",
                "phones.lastModifiedDate",
                "emails.lastModifiedDate"
            )
            .isEqualTo(expectedContactEntity);
    }
}

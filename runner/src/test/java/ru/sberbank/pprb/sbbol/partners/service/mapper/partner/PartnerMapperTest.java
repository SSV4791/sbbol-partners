package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerEmailMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerEmailMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapperImpl_;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerPhoneMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerPhoneMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.StringMapperImpl;
import ru.sberbank.pprb.sbbol.partners.model.Citizenship;
import ru.sberbank.pprb.sbbol.partners.model.EmailChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PhoneChangeFullModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = {
        PartnerMapperImpl.class,
        PartnerMapperImpl_.class,
        PartnerEmailMapperImpl.class,
        PartnerPhoneMapperImpl.class,
        StringMapperImpl.class
    }
)
class PartnerMapperTest extends BaseUnitConfiguration {

    @Autowired
    private PartnerMapper mapper;

    @Autowired
    private PartnerPhoneMapper partnerPhoneMapper;

    @Autowired
    private PartnerEmailMapper partnerEmailMapper;

    @Test
    void mapUuid() {
        var expected = UUID.randomUUID();
        var value = "[[[" + expected + "]]]";
        var actual = BaseMapper.mapUuid(value);
        assertThat(expected)
            .isEqualTo(actual);
    }

    @Test
    void toPartner() {
        Partner expected = factory.manufacturePojo(Partner.class);
        PartnerEntity partnerEntity = mapper.toPartner(expected);
        Partner actual = mapper.toPartner(partnerEntity);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "changeDate",
                "phones",
                "emails"
            )
            .isEqualTo(actual);
    }

    @Test
    void toPartnerWithPartnerCreateFullModel() {
        var expected = factory.manufacturePojo(PartnerCreateFullModel.class);
        var actual = mapper.toPartner(expected);
        assertThat(actual)
            .isNotNull();
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "address",
                "documents",
                "legalForm",
                "accounts",
                "contacts",
                "emails",
                "phones"
            )
            .isEqualTo(actual);
        assertThat(PartnerMapper.toLegalType(expected.getLegalForm()))
            .isEqualTo(actual.getLegalType());

        assertThat(actual.getEmails())
            .isNotNull();
        for (var email : actual.getEmails()) {
            assertThat(expected.getEmails())
                .contains(email.getEmail());
        }

        assertThat(actual.getPhones())
            .isNotNull();
        for (var phone : actual.getPhones()) {
            assertThat(expected.getPhones())
                .contains(phone.getPhone());
        }
    }

    @Test
    void testToPartnerMullResponse() {
        var expected = factory.manufacturePojo(PartnerEntity.class);
        var actual = mapper.toPartnerMullResponse(expected);
        assertThat(actual)
            .isNotNull();
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "legalType",
                "lastModifiedDate",
                "type",
                "gkuInnEntity",
                "uuid",
                "search",
                "createDate",
                "countryCode",
                "countryName",
                "phones",
                "emails",
                "accounts"
            )
            .isEqualTo(actual);
        assertThat(PartnerMapper.toLegalType(expected.getLegalType()))
            .isEqualTo(actual.getLegalForm());

        assertThat(actual.getPhones())
            .isNotNull();
        for (var phoneEntity : expected.getPhones()) {
            assertThat(actual.getPhones())
                .contains(partnerPhoneMapper.toPhone(phoneEntity));
        }

        assertThat(actual.getEmails())
            .isNotNull();
        for (var email : expected.getEmails()) {
            assertThat(actual.getEmails())
                .contains(partnerEmailMapper.toEmail(email));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testToPartnerPhoneString() {
        Set<String> phones = factory.manufacturePojo(HashSet.class, String.class);
        var digitalId = RandomStringUtils.randomAlphanumeric(10);
        List<PartnerPhoneEntity> actual = mapper.toPhone(phones, digitalId);
        for (PartnerPhoneEntity partnerPhone : actual) {
            assertThat(partnerPhone.getDigitalId())
                .isEqualTo(digitalId);
            assertThat(phones)
                .contains(partnerPhone.getPhone());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testToPartnerEmailString() {
        Set<String> emails = factory.manufacturePojo(HashSet.class, String.class);
        var digitalId = RandomStringUtils.randomAlphanumeric(10);
        List<PartnerEmailEntity> actual = mapper.toEmail(emails, digitalId);
        for (PartnerEmailEntity partnerEmail : actual) {
            assertThat(partnerEmail.getDigitalId())
                .isEqualTo(digitalId);
            assertThat(emails)
                .contains(partnerEmail.getEmail());
        }
    }

    @Test
    void toPartnerCreate() {
        PartnerCreate expected = factory.manufacturePojo(PartnerCreate.class);
        PartnerEntity partnerEntity = mapper.toPartner(expected);
        Partner actual = mapper.toPartner(partnerEntity);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "phones",
                "emails"
            )
            .isEqualTo(actual);
    }

    @Test
    void toPartnerCreateFullModel() {
        PartnerCreateFullModel expected = factory.manufacturePojo(PartnerCreateFullModel.class);
        PartnerEntity partnerEntity = mapper.toPartner(expected);
        Partner actual = mapper.toPartner(partnerEntity);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "phones",
                "emails",
                "address",
                "documents",
                "accounts",
                "contacts"
            )
            .isEqualTo(actual);
    }

    @Test
    void toLegalType() {
        LegalForm typeEnum = factory.manufacturePojo(LegalForm.class);
        LegalType legalType = PartnerMapper.toLegalType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(PartnerMapper.toLegalType(legalType));
    }

    @Test
    void toPartnerCitizenshipType() {
        Citizenship typeEnum = factory.manufacturePojo(Citizenship.class);
        PartnerCitizenshipType citizenshipType = PartnerMapper.toCitizenshipType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(PartnerMapper.toCitizenshipType(citizenshipType));
    }

    @Test
    void patchPartnerWithoutMergedPhonesAndEmails() {
        var partnerChangeFullModel = new PartnerChangeFullModel()
            .orgName("Updated orgName")
            .comment("Updated comment");
        var actualPartnerEntity = factory.manufacturePojo(PartnerEntity.class);
        actualPartnerEntity.getPhones().forEach(phoneEntity -> {
            phoneEntity.setPartner(actualPartnerEntity);
            phoneEntity.setDigitalId(actualPartnerEntity.getDigitalId());
        });
        actualPartnerEntity.getEmails().forEach(emailEntity -> {
            emailEntity.setPartner(actualPartnerEntity);
            emailEntity.setDigitalId(actualPartnerEntity.getDigitalId());
        });
        var expectedPhones = new ArrayList<>(actualPartnerEntity.getPhones());
        var expectedEmails = new ArrayList<>(actualPartnerEntity.getEmails());
        mapper.patchPartner(partnerChangeFullModel, actualPartnerEntity);
        var expectedPartnerEntity = new PartnerEntity();
        expectedPartnerEntity.setUuid(actualPartnerEntity.getUuid());
        expectedPartnerEntity.setVersion(actualPartnerEntity.getVersion());
        expectedPartnerEntity.setDigitalId(actualPartnerEntity.getDigitalId());
        expectedPartnerEntity.setFirstName(actualPartnerEntity.getFirstName());
        expectedPartnerEntity.setMiddleName(actualPartnerEntity.getMiddleName());
        expectedPartnerEntity.setSecondName(actualPartnerEntity.getSecondName());
        expectedPartnerEntity.setOrgName(partnerChangeFullModel.getOrgName());
        expectedPartnerEntity.setInn(actualPartnerEntity.getInn());
        expectedPartnerEntity.setKpp(actualPartnerEntity.getKpp());
        expectedPartnerEntity.setOgrn(actualPartnerEntity.getOgrn());
        expectedPartnerEntity.setOkpo(actualPartnerEntity.getOkpo());
        expectedPartnerEntity.setCitizenship(actualPartnerEntity.getCitizenship());
        expectedPartnerEntity.setComment(partnerChangeFullModel.getComment());
        expectedPartnerEntity.setLegalType(actualPartnerEntity.getLegalType());
        expectedPartnerEntity.setPhones(expectedPhones);
        expectedPartnerEntity.setEmails(expectedEmails);
        assertThat(actualPartnerEntity)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .ignoringFields(
                "uuid",
                "createDate",
                "gkuInnEntity",
                "lastModifiedDate",
                "phones.lastModifiedDate",
                "emails.lastModifiedDate",
                "search",
                "type"
            )
            .isEqualTo(expectedPartnerEntity);
    }

    @Test
    void patchPartnerWithMergedPhonesAndEmails() {
        var updatedPhone = new PhoneChangeFullModel()
            .phone(factory.manufacturePojo(String.class));
        var updatedEmail = new EmailChangeFullModel()
            .email(factory.manufacturePojo(String.class));
        var partnerChangeFullModel = new PartnerChangeFullModel()
            .phones(Set.of(updatedPhone))
            .emails(Set.of(updatedEmail));
        var actualPartnerEntity = factory.manufacturePojo(PartnerEntity.class);
        actualPartnerEntity.getPhones().forEach(phoneEntity -> {
            phoneEntity.setPartner(actualPartnerEntity);
            phoneEntity.setDigitalId(actualPartnerEntity.getDigitalId());
        });
        actualPartnerEntity.getEmails().forEach(emailEntity -> {
            emailEntity.setPartner(actualPartnerEntity);
            emailEntity.setDigitalId(actualPartnerEntity.getDigitalId());
        });
        var expectedPhones = Stream.concat(
                actualPartnerEntity.getPhones().stream(),
                partnerChangeFullModel.getPhones().stream().map(phoneChangeFullModel -> {
                    var partnerPhoneEntity = new PartnerPhoneEntity();
                    partnerPhoneEntity.setPhone(phoneChangeFullModel.getPhone());
                    partnerPhoneEntity.setPartner(actualPartnerEntity);
                    partnerPhoneEntity.setDigitalId(actualPartnerEntity.getDigitalId());
                    return partnerPhoneEntity;
                }))
            .collect(Collectors.toList());
        var expectedEmails = Stream.concat(
                actualPartnerEntity.getEmails().stream(),
                partnerChangeFullModel.getEmails().stream().map(emailChangeFullModel -> {
                    var partnerEmailEntity = new PartnerEmailEntity();
                    partnerEmailEntity.setEmail(emailChangeFullModel.getEmail());
                    partnerEmailEntity.setPartner(actualPartnerEntity);
                    partnerEmailEntity.setDigitalId(actualPartnerEntity.getDigitalId());
                    return partnerEmailEntity;
                }))
            .collect(Collectors.toList());
        mapper.patchPartner(partnerChangeFullModel, actualPartnerEntity);
        var expectedPartnerEntity = new PartnerEntity();
        expectedPartnerEntity.setUuid(actualPartnerEntity.getUuid());
        expectedPartnerEntity.setVersion(actualPartnerEntity.getVersion());
        expectedPartnerEntity.setDigitalId(actualPartnerEntity.getDigitalId());
        expectedPartnerEntity.setFirstName(actualPartnerEntity.getFirstName());
        expectedPartnerEntity.setMiddleName(actualPartnerEntity.getMiddleName());
        expectedPartnerEntity.setSecondName(actualPartnerEntity.getSecondName());
        expectedPartnerEntity.setOrgName(actualPartnerEntity.getOrgName());
        expectedPartnerEntity.setInn(actualPartnerEntity.getInn());
        expectedPartnerEntity.setKpp(actualPartnerEntity.getKpp());
        expectedPartnerEntity.setOgrn(actualPartnerEntity.getOgrn());
        expectedPartnerEntity.setOkpo(actualPartnerEntity.getOkpo());
        expectedPartnerEntity.setCitizenship(actualPartnerEntity.getCitizenship());
        expectedPartnerEntity.setComment(actualPartnerEntity.getComment());
        expectedPartnerEntity.setLegalType(actualPartnerEntity.getLegalType());
        expectedPartnerEntity.setPhones(expectedPhones);
        expectedPartnerEntity.setEmails(expectedEmails);
        assertThat(actualPartnerEntity)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .ignoringFields(
                "uuid",
                "createDate",
                "gkuInnEntity",
                "lastModifiedDate",
                "phones.lastModifiedDate",
                "emails.lastModifiedDate",
                "search",
                "type"
            )
            .isEqualTo(expectedPartnerEntity);
    }
}

package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerEmailMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerPhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Citizenship;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PartnerMapperTest extends BaseUnitConfiguration {

    private PartnerMapper mapper;

    @Mock
    private PartnerPhoneMapper partnerPhoneMapper;

    @Mock
    private PartnerEmailMapper partnerEmailMapper;

    @BeforeEach
    void before() {
        mapper = new PartnerMapperImpl(partnerEmailMapper, partnerPhoneMapper);
    }

    @Test
    void mapUuid() {
        var expected = UUID.randomUUID();
        var value = "[[[" + expected + "]]]";
        var actual = mapper.mapUuid(value);
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
                "phones",
                "emails",
                "accounts"
            )
            .isEqualTo(actual);
        assertThat(PartnerMapper.toLegalType(expected.getLegalType()))
            .isEqualTo(actual.getLegalForm());

        assertThat(actual.getPhones())
            .isNotNull();
        for(var phoneEntity: expected.getPhones()) {
            assertThat(actual.getPhones())
                .contains(partnerPhoneMapper.toPhone(phoneEntity));
        }

        assertThat(actual.getEmails())
            .isNotNull();
        for(var email: expected.getEmails()) {
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
}

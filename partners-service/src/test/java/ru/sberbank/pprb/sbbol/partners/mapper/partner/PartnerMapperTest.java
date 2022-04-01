package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class PartnerMapperTest extends BaseConfiguration {

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
    @AllureId("34381")
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
    @AllureId("34102")
    void testToPartnerPhoneString() {
        List<String> phones = factory.manufacturePojo(ArrayList.class, String.class);
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
    @AllureId("34382")
    void testToPartnerEmailString() {
        List<String> emails = factory.manufacturePojo(ArrayList.class, String.class);
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
    @AllureId("34381")
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
    @AllureId("34102")
    void toLegalType() {
        LegalForm typeEnum = factory.manufacturePojo(LegalForm.class);
        LegalType legalType = PartnerMapper.toLegalType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(PartnerMapper.toLegalType(legalType));
    }

    @Test
    @AllureId("34071")
    void toPartnerCitizenshipType() {
        Partner.CitizenshipEnum typeEnum = factory.manufacturePojo(Partner.CitizenshipEnum.class);
        PartnerCitizenshipType citizenshipType = PartnerMapper.toCitizenshipType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(PartnerMapper.toCitizenshipType(citizenshipType));
    }
}

package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

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
    void toPartnerType() {
        Partner.PartnerTypeEnum typeEnum = factory.manufacturePojo(Partner.PartnerTypeEnum.class);
        PartnerType partnerType = PartnerMapper.toPartnerType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(PartnerMapper.toPartnerType(partnerType));
    }

    @Test
    void toLegalType() {
        Partner.LegalFormEnum typeEnum = factory.manufacturePojo(Partner.LegalFormEnum.class);
        LegalType legalType = PartnerMapper.toLegalType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(PartnerMapper.toLegalType(legalType));
    }

    @Test
    void toPartnerCitizenshipType() {
        Partner.CitizenshipEnum typeEnum = factory.manufacturePojo(Partner.CitizenshipEnum.class);
        PartnerCitizenshipType citizenshipType = PartnerMapper.toCitizenshipType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(PartnerMapper.toCitizenshipType(citizenshipType));
    }
}

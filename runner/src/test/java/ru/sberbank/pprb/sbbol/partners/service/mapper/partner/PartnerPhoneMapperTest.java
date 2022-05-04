package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerPhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import static org.assertj.core.api.Assertions.assertThat;


class PartnerPhoneMapperTest extends BaseUnitConfiguration {

    private static final PartnerPhoneMapper mapper = Mappers.getMapper(PartnerPhoneMapper.class);

    @Test
    @AllureId("34077")
    void testToPartnerPhone() {
        Phone expected = factory.manufacturePojo(Phone.class);
        PartnerPhoneEntity actual = mapper.toPhone(expected);
        actual.setPartner(factory.manufacturePojo(PartnerEntity.class));
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("unifiedId")
            .isEqualTo(mapper.toPhone(actual));
    }

    @Test
    @AllureId("34077")
    void testToPartnerPhoneReverse() {
        PartnerPhoneEntity expected = factory.manufacturePojo(PartnerPhoneEntity.class);
        expected.setPartner(factory.manufacturePojo(PartnerEntity.class));
        Phone actual = mapper.toPhone(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("partner")
            .isEqualTo(mapper.toPhone(actual));
    }
}

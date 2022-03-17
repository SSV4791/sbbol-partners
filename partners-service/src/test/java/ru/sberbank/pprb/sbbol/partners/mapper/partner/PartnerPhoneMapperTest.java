package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import static org.assertj.core.api.Assertions.assertThat;


class PartnerPhoneMapperTest extends BaseConfiguration {

    private static final PartnerPhoneMapper mapper = Mappers.getMapper(PartnerPhoneMapper.class);

    @Test
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
    void testToPartnerPhoneString() {
        var expected = RandomStringUtils.randomAlphanumeric(10);
        PartnerPhoneEntity actual = mapper.toPhone(expected);
        actual.setPartner(factory.manufacturePojo(PartnerEntity.class));
        var phone = mapper.toPhone(actual);
        assertThat(expected)
            .isEqualTo(phone.getPhone());
    }

    @Test
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

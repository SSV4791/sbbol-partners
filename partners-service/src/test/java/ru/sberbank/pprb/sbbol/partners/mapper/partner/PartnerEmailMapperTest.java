package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Email;

import static org.assertj.core.api.Assertions.assertThat;

class PartnerEmailMapperTest extends BaseConfiguration {

    private static final PartnerEmailMapper mapper = Mappers.getMapper(PartnerEmailMapper.class);

    @Test
    @AllureId("34094")
    void testToEmail() {
        Email expected = factory.manufacturePojo(Email.class);
        PartnerEmailEntity actual = mapper.toEmail(expected);
        actual.setPartner(factory.manufacturePojo(PartnerEntity.class));
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("unifiedId")
            .isEqualTo(mapper.toEmail(actual));
    }

    @Test
    @AllureId("34061")
    void testToEmailReverse() {
        PartnerEmailEntity expected = factory.manufacturePojo(PartnerEmailEntity.class);
        expected.setPartner(factory.manufacturePojo(PartnerEntity.class));
        Email actual = mapper.toEmail(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("partner")
            .isEqualTo(mapper.toEmail(actual));
    }
}

package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactPhoneMapperTest extends BaseConfiguration {

    private static final ContactPhoneMapper mapper = Mappers.getMapper(ContactPhoneMapper.class);

    @Test
    @AllureId("34093")
    void testToContactPhone() {
        Phone expected = factory.manufacturePojo(Phone.class);
        ContactPhoneEntity actual = mapper.toPhone(expected);
        actual.setContact(factory.manufacturePojo(ContactEntity.class));
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("unifiedId")
            .isEqualTo(mapper.toPhone(actual));
    }

    @Test
    @AllureId("34093")
    void testToContactPhoneReverse() {
        ContactPhoneEntity expected = factory.manufacturePojo(ContactPhoneEntity.class);
        expected.setContact(factory.manufacturePojo(ContactEntity.class));
        Phone actual = mapper.toPhone(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("contact")
            .isEqualTo(mapper.toPhone(actual));
    }
}

package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactPhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import static org.assertj.core.api.Assertions.assertThat;

class ContactPhoneMapperTest extends BaseUnitConfiguration {

    private static final ContactPhoneMapper mapper = Mappers.getMapper(ContactPhoneMapper.class);

    @Test
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
    void testToContactPhoneReverse() {
        ContactPhoneEntity expected = factory.manufacturePojo(ContactPhoneEntity.class);
        expected.setContact(factory.manufacturePojo(ContactEntity.class));
        Phone actual = mapper.toPhone(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "contact",
                "lastModifiedDate"
            )
            .isEqualTo(mapper.toPhone(actual));
    }
}

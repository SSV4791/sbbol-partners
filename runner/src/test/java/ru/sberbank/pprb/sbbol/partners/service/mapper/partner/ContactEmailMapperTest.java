package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactEmailMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;

import static org.assertj.core.api.Assertions.assertThat;

class ContactEmailMapperTest extends BaseUnitConfiguration {

    private static final ContactEmailMapper mapper = Mappers.getMapper(ContactEmailMapper.class);

    @Test
    void testToEmail() {
        Email expected = factory.manufacturePojo(Email.class);
        ContactEmailEntity actual = mapper.toEmail(expected);
        actual.setContact(factory.manufacturePojo(ContactEntity.class));
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("unifiedId")
            .isEqualTo(mapper.toEmail(actual));
    }

    @Test
    void testToEmailReverse() {
        ContactEmailEntity expected = factory.manufacturePojo(ContactEmailEntity.class);
        expected.setContact(factory.manufacturePojo(ContactEntity.class));
        Email actual = mapper.toEmail(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "contact",
                "lastModifiedDate"
            )
            .isEqualTo(mapper.toEmail(actual));
    }
}

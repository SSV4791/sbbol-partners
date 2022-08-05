package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.EmailMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;

import static org.assertj.core.api.Assertions.assertThat;

class EmailMapperTest extends BaseUnitConfiguration {

    private static final EmailMapper mapper = Mappers.getMapper(EmailMapper.class);

    @Test
    void toEmail() {
        var expected = factory.manufacturePojo(Email.class);
        var actual = mapper.toEmail(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toEmail(actual));
    }

    @Test
    void toEmailCreate() {
        var expected = factory.manufacturePojo(EmailCreate.class);
        var actual = mapper.toEmail(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "digitalId",
                "version"
            )
            .isEqualTo(mapper.toEmail(actual));
    }
}

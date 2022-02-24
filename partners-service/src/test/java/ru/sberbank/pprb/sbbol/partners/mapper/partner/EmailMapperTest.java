package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Email;

import static org.assertj.core.api.Assertions.assertThat;

class EmailMapperTest extends BaseConfiguration {

    private static final EmailMapper mapper = Mappers.getMapper(EmailMapper.class);

    @Test
    void toEmail() {
        var expected = factory.manufacturePojo(Email.class);
        var actual = mapper.toEmail(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toEmail(actual));
    }
}

package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneMapperTest extends BaseConfiguration {

    private static final PhoneMapper mapper = Mappers.getMapper(PhoneMapper.class);

    @Test
    void toPhone() {
        var expected = factory.manufacturePojo(Phone.class);
        var actual = mapper.toPhone(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toPhone(actual));
    }
}

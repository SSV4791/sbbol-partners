package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;

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

    @Test
    void toPhoneCreate() {
        var expected = factory.manufacturePojo(PhoneCreate.class);
        var actual = mapper.toPhone(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "id",
                "digitalId",
                "version"
            )
            .isEqualTo(mapper.toPhone(actual));
    }
}

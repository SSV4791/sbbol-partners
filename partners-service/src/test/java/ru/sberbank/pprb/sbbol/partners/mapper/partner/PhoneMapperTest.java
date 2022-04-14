package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneMapperTest extends BaseUnitConfiguration {

    private static final PhoneMapper mapper = Mappers.getMapper(PhoneMapper.class);

    @Test
    @AllureId("34380")
    void toPhone() {
        var expected = factory.manufacturePojo(Phone.class);
        var actual = mapper.toPhone(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toPhone(actual));
    }

    @Test
    @AllureId("34380")
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

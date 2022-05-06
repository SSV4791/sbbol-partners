package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;

import static org.assertj.core.api.Assertions.assertThat;

class AddressMapperTest extends BaseUnitConfiguration {

    private static final AddressMapper mapper = Mappers.getMapper(AddressMapper.class);

    @Test
    @AllureId("34079")
    void testToAddress() {
        Address expected = factory.manufacturePojo(Address.class);
        AddressEntity actual = mapper.toAddress(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toAddress(actual));
    }

    @Test
    @AllureId("34079")
    void testToAddressType() {
        ru.sberbank.pprb.sbbol.partners.model.AddressType typeEnum = factory.manufacturePojo(ru.sberbank.pprb.sbbol.partners.model.AddressType.class);
        AddressType addressType = AddressMapper.toAddressType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(AddressMapper.toAddressType(addressType));
    }
}

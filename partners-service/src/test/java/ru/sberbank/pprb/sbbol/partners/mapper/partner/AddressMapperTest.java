package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Address;

import static org.assertj.core.api.Assertions.assertThat;

class AddressMapperTest extends BaseConfiguration {

    private static final AddressMapper mapper = Mappers.getMapper(AddressMapper.class);

    @Test
    void testToAddress() {
        Address expected = factory.manufacturePojo(Address.class);
        AddressEntity actual = mapper.toAddress(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toAddress(actual));
    }

    @Test
    void testToAddressType() {
        Address.TypeEnum typeEnum = factory.manufacturePojo(Address.TypeEnum.class);
        AddressType addressType = AddressMapper.toAddressType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(AddressMapper.toAddressType(addressType));
    }
}

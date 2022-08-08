package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreateFullModel;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AddressMapperTest extends BaseUnitConfiguration {

    private static final AddressMapper mapper = Mappers.getMapper(AddressMapper.class);

    @Test
    void testToAddress() {
        Address expected = factory.manufacturePojo(Address.class);
        AddressEntity actual = mapper.toAddress(expected);
        assertThat(actual)
            .isNotNull();
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(mapper.toAddress(actual));
    }

    @Test
    void testToAdressWithAdressCreateFullModel() {
        var expected = factory.manufacturePojo(AddressCreateFullModel.class);
        var digitalId = factory.manufacturePojo(String.class);
        var unifiedUuid = factory.manufacturePojo(UUID.class);
        AddressEntity actual = mapper.toAddress(expected, digitalId, unifiedUuid);
        assertThat(actual)
            .isNotNull();
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(actual);
        assertThat(digitalId)
            .isEqualTo(actual.getDigitalId());
        assertThat(unifiedUuid)
            .isEqualTo(actual.getUnifiedUuid());
    }

    @Test
    void testToAddressType() {
        ru.sberbank.pprb.sbbol.partners.model.AddressType typeEnum = factory.manufacturePojo(ru.sberbank.pprb.sbbol.partners.model.AddressType.class);
        AddressType addressType = AddressMapper.toAddressType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(AddressMapper.toAddressType(addressType));
    }
}

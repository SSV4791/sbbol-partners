package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapperImpl_;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreateFullModel;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = {
        AddressMapperImpl.class,
        AddressMapperImpl_.class
    }
)
class AddressMapperTest extends BaseUnitConfiguration {

    @Autowired
    private AddressMapper mapper;

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

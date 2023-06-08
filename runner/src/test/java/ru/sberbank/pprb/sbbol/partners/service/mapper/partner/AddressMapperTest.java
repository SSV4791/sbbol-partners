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
import ru.sberbank.pprb.sbbol.partners.model.AddressChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
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

    @Test
    void mapAddressChangeFullModelToAddress() {
        var addressChangeFullModel = factory.manufacturePojo(AddressChangeFullModel.class);
        var digitalId = factory.manufacturePojo(String.class);
        var unifiedId = factory.manufacturePojo(String.class);
        var actualAddress = mapper.toAddress(addressChangeFullModel, digitalId, unifiedId);
        var expectedAddress = new Address()
            .id(addressChangeFullModel.getId())
            .version(addressChangeFullModel.getVersion())
            .digitalId(digitalId)
            .unifiedId(unifiedId)
            .fullAddress(addressChangeFullModel.getFullAddress())
            .administrationUnit(addressChangeFullModel.getAdministrationUnit())
            .administrationUnitCode(addressChangeFullModel.getAdministrationUnitCode())
            .area(addressChangeFullModel.getArea())
            .building(addressChangeFullModel.getBuilding())
            .buildingBlock(addressChangeFullModel.getBuildingBlock())
            .city(addressChangeFullModel.getCity())
            .flat(addressChangeFullModel.getFlat())
            .location(addressChangeFullModel.getLocation())
            .region(addressChangeFullModel.getRegion())
            .regionCode(addressChangeFullModel.getRegionCode())
            .street(addressChangeFullModel.getStreet())
            .type(addressChangeFullModel.getType())
            .zipCode(addressChangeFullModel.getZipCode())
            .country(addressChangeFullModel.getCountry())
            .countryCode(addressChangeFullModel.getCountryCode())
            .countryIsoCode(addressChangeFullModel.getCountryIsoCode());
        assertThat(actualAddress)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedAddress);
    }

    @Test
    void mapAddressChangeFullModelToAddressCreate() {
        var addressChangeFullModel = factory.manufacturePojo(AddressChangeFullModel.class);
        var digitalId = factory.manufacturePojo(String.class);
        var unifiedId = factory.manufacturePojo(String.class);
        var actualAddressCreate = mapper.toAddressCreate(addressChangeFullModel, digitalId, unifiedId);
        var expectedAddressCreate = new AddressCreate()
            .digitalId(digitalId)
            .unifiedId(unifiedId)
            .fullAddress(addressChangeFullModel.getFullAddress())
            .administrationUnit(addressChangeFullModel.getAdministrationUnit())
            .administrationUnitCode(addressChangeFullModel.getAdministrationUnitCode())
            .area(addressChangeFullModel.getArea())
            .building(addressChangeFullModel.getBuilding())
            .buildingBlock(addressChangeFullModel.getBuildingBlock())
            .city(addressChangeFullModel.getCity())
            .flat(addressChangeFullModel.getFlat())
            .location(addressChangeFullModel.getLocation())
            .region(addressChangeFullModel.getRegion())
            .regionCode(addressChangeFullModel.getRegionCode())
            .street(addressChangeFullModel.getStreet())
            .type(addressChangeFullModel.getType())
            .zipCode(addressChangeFullModel.getZipCode())
            .country(addressChangeFullModel.getCountry())
            .countryCode(addressChangeFullModel.getCountryCode())
            .countryIsoCode(addressChangeFullModel.getCountryIsoCode());
        assertThat(actualAddressCreate)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedAddressCreate);
    }
}

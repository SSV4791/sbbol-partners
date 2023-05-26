package ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator;

import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreateFullModel;

import java.util.UUID;

import static java.util.Objects.isNull;

public abstract class AddressMapperDecorator implements AddressMapper {

    private static final String COUNTRY_ISO_CODE_RU = "RU";

    private static final String COUNTRY_CODE_RUS = "643";

    private static final String COUNTRY_RUS = "Россия Russian Federation";

    @Autowired
    @Qualifier("delegate")
    private AddressMapper delegate;

    @Override
    public AddressEntity toAddress(AddressCreateFullModel address, String digitalId, UUID unifiedUuid) {
        var addressEntity = delegate.toAddress(address, digitalId, unifiedUuid);
        addressEntity.setCountryCode(normalizationCountryCode(address.getCountryCode()));
        addressEntity.setCountryIsoCode(normalizationCountryIsoCode(address.getCountryIsoCode()));
        addressEntity.setCountry(normalizationCountry(address.getCountry()));
        return addressEntity;
    }

    @Override
    public AddressEntity toAddress(AddressCreate address) {
        var addressEntity = delegate.toAddress(address);
        addressEntity.setCountryCode(normalizationCountryCode(address.getCountryCode()));
        addressEntity.setCountryIsoCode(normalizationCountryIsoCode(address.getCountryIsoCode()));
        addressEntity.setCountry(normalizationCountry(address.getCountry()));
        return addressEntity;
    }

    @Override
    public AddressEntity toAddress(Address address) {
        var addressEntity = delegate.toAddress(address);
        addressEntity.setCountryCode(normalizationCountryCode(address.getCountryCode()));
        addressEntity.setCountryIsoCode(normalizationCountryIsoCode(address.getCountryIsoCode()));
        addressEntity.setCountry(normalizationCountry(address.getCountry()));
        return addressEntity;
    }

    @Override
    public void updateAddress(Address address, @MappingTarget() AddressEntity addressEntity) {
        delegate.updateAddress(address, addressEntity);
        addressEntity.setCountryCode(normalizationCountryCode(address.getCountryCode()));
        addressEntity.setCountryIsoCode(normalizationCountryIsoCode(address.getCountryIsoCode()));
        addressEntity.setCountry(normalizationCountry(address.getCountry()));
    }

    protected String normalizationCountryCode(String countryCode) {
        return isNull(countryCode) ? COUNTRY_CODE_RUS : countryCode;
    }

    protected String normalizationCountryIsoCode(String countryIsoCode) {
        return isNull(countryIsoCode) ? COUNTRY_ISO_CODE_RU : countryIsoCode;
    }

    protected String normalizationCountry(String country) {
        return isNull(country) ? COUNTRY_RUS : country;
    }
}

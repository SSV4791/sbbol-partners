package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;

import java.util.List;
import java.util.UUID;

public class AddressUpdateValidationImpl extends AbstractValidatorImpl<Address> {
    private static final String DOCUMENT_NAME = "contact_address";
    private final AddressRepository addressRepository;

    public AddressUpdateValidationImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, Address entity) {
        var foundAddress = addressRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        commonValidationUuid(errors,entity.getId());
        commonValidationUuid(errors,entity.getUnifiedId());
        commonValidationDigitalId(errors,entity.getDigitalId());
        if (entity.getVersion() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "version"));
        }
        if (!entity.getVersion().equals(foundAddress.getVersion())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundAddress.getVersion().toString(), entity.getVersion().toString()));
        }
        if (StringUtils.isNotEmpty(entity.getZipCode()) && entity.getZipCode().length() > ZIP_CODE_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "zipCode", "1", "6"));
        }
        if (StringUtils.isNotEmpty(entity.getRegion()) && entity.getRegion().length() > REGION_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "region", "1", "50"));
        }
        if (StringUtils.isNotEmpty(entity.getRegionCode()) && entity.getRegionCode().length() > REGION_CODE_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "regionCode", "1", "10"));
        }
        if (StringUtils.isNotEmpty(entity.getCity()) && entity.getCity().length() > CITY_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "city", "1", "300"));
        }
        if (StringUtils.isNotEmpty(entity.getLocation()) && entity.getLocation().length() > LOCATION_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "location", "1", "300"));
        }
        if (StringUtils.isNotEmpty(entity.getStreet()) && entity.getStreet().length() > STREET_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "street", "1", "300"));
        }
        if (StringUtils.isNotEmpty(entity.getBuilding()) && entity.getBuilding().length() > BUILDING_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "building", "1", "100"));
        }
        if (StringUtils.isNotEmpty(entity.getBuildingBlock()) && entity.getBuildingBlock().length() > BUILDING_BLOCK_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "buildingBlock", "1", "20"));
        }
        if (StringUtils.isNotEmpty(entity.getFlat()) && entity.getFlat().length() > FLAT_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "flat", "1", "20"));
        }
    }
}

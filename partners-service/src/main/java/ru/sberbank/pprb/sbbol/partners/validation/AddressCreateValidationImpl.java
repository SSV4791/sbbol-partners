package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;

import java.util.List;


public class AddressCreateValidationImpl extends AbstractValidatorImpl<AddressCreate> {

    @Override
    public void validator(List<String> errors, AddressCreate entity) {
        commonValidationUuid(errors, entity.getUnifiedId());
        commonValidationDigitalId(errors, entity.getDigitalId());
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
        if (entity.getType() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "type"));
        }
    }
}

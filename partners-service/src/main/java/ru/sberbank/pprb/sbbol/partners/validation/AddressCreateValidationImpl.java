package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import java.util.Map;

import java.util.List;


import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class AddressCreateValidationImpl extends AbstractValidatorImpl<AddressCreate> {

    @Override
    public void validator(Map<String, List<String>> errors, AddressCreate entity) {
        commonValidationUuid(errors, entity.getUnifiedId());
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (StringUtils.isNotEmpty(entity.getZipCode()) && entity.getZipCode().length() > ZIP_CODE_MAX_LENGTH_VALIDATION) {
            setError(errors, "zipCode", MessagesTranslator.toLocale(DEFAULT_LENGTH, "6"));
        }
        if (StringUtils.isNotEmpty(entity.getRegion()) && entity.getRegion().length() > REGION_MAX_LENGTH_VALIDATION) {
            setError(errors, "region", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getRegionCode()) && entity.getRegionCode().length() > REGION_CODE_MAX_LENGTH_VALIDATION) {
            setError(errors, "regionCode", MessagesTranslator.toLocale(DEFAULT_LENGTH, "10"));
        }
        if (StringUtils.isNotEmpty(entity.getCity()) && entity.getCity().length() > CITY_MAX_LENGTH_VALIDATION) {
            setError(errors, "city", MessagesTranslator.toLocale(DEFAULT_LENGTH, "300"));
        }
        if (StringUtils.isNotEmpty(entity.getLocation()) && entity.getLocation().length() > LOCATION_MAX_LENGTH_VALIDATION) {
            setError(errors, "location", MessagesTranslator.toLocale(DEFAULT_LENGTH, "300"));
        }
        if (StringUtils.isNotEmpty(entity.getStreet()) && entity.getStreet().length() > STREET_MAX_LENGTH_VALIDATION) {
            setError(errors, "street", MessagesTranslator.toLocale(DEFAULT_LENGTH, "300"));
        }
        if (StringUtils.isNotEmpty(entity.getBuilding()) && entity.getBuilding().length() > BUILDING_MAX_LENGTH_VALIDATION) {
            setError(errors, "building", MessagesTranslator.toLocale(DEFAULT_LENGTH, "100"));
        }
        if (StringUtils.isNotEmpty(entity.getBuildingBlock()) && entity.getBuildingBlock().length() > BUILDING_BLOCK_MAX_LENGTH_VALIDATION) {
            setError(errors, "buildingBlock", MessagesTranslator.toLocale(DEFAULT_LENGTH, "20"));
        }
        if (StringUtils.isNotEmpty(entity.getFlat()) && entity.getFlat().length() > FLAT_MAX_LENGTH_VALIDATION) {
            setError(errors, "flat", MessagesTranslator.toLocale(DEFAULT_LENGTH, "20"));
        }
        if (entity.getType() == null) {
            setError(errors, "type", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "Тип адреса"));
        }
    }
}

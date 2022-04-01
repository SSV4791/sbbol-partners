package ru.sberbank.pprb.sbbol.partners.mapper.config;

import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.typeManufacturers.AbstractTypeManufacturer;
import uk.co.jemos.podam.typeManufacturers.BooleanTypeManufacturerImpl;
import uk.co.jemos.podam.typeManufacturers.StringTypeManufacturerImpl;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@UnitTestLayer
public abstract class BaseConfiguration {

    protected static final PodamFactory factory = new PodamFactoryImpl();

    static {
        factory.getStrategy()
            .addOrReplaceTypeManufacturer(BigDecimal.class, new BaseConfiguration.BigDecimalManufacturerImpl())
            .addOrReplaceTypeManufacturer(String.class, new BaseConfiguration.StringManufacturerImpl())
            .addOrReplaceTypeManufacturer(Boolean.class, new BaseConfiguration.BooleanManufacturerImpl());
    }

    public static class BigDecimalManufacturerImpl extends AbstractTypeManufacturer<BigDecimal> {
        @Override
        public BigDecimal getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
            return new BigDecimal(randomNumeric(20));
        }
    }

    public static class StringManufacturerImpl extends StringTypeManufacturerImpl {
        @Override
        public String getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
            if (attributeMetadata.getAttributeName() == null) {
                return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            }
            return switch (attributeMetadata.getAttributeName()) {
                case "id", "uuid", "unifiedId", "entityId", "partnerId", "partnerAccountId", "accountId", "bankId", "documentTypeId" -> UUID.randomUUID().toString();
                case "phone" -> "007" + randomNumeric(9);
                default -> super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            };
        }
    }

    public static class BooleanManufacturerImpl extends BooleanTypeManufacturerImpl {
        @Override
        public Boolean getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
            return switch (attributeMetadata.getAttributeName()) {
                case "gku", "budget" -> false;
                default -> super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            };
        }
    }
}

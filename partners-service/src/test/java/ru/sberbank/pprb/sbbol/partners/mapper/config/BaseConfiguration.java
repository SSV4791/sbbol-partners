package ru.sberbank.pprb.sbbol.partners.mapper.config;

import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.typeManufacturers.AbstractTypeManufacturer;
import uk.co.jemos.podam.typeManufacturers.StringTypeManufacturerImpl;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public abstract class BaseConfiguration {

    protected static final PodamFactory factory = new PodamFactoryImpl();

    static {
        factory.getStrategy()
            .addOrReplaceTypeManufacturer(BigDecimal.class, new BaseConfiguration.BigDecimalManufacturerImpl())
            .addOrReplaceTypeManufacturer(String.class, new BaseConfiguration.StringManufacturerImpl());
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
            return switch (attributeMetadata.getAttributeName()) {
                case "id", "uuid", "unifiedId", "partnerId", "partnerAccountId", "accountId", "bankId" -> UUID.randomUUID().toString();
                case "phone" -> "007" + randomNumeric(9);
                default -> super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            };
        }
    }
}

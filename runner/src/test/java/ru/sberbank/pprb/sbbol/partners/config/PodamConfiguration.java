package ru.sberbank.pprb.sbbol.partners.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.typeManufacturers.AbstractTypeManufacturer;
import uk.co.jemos.podam.typeManufacturers.BooleanTypeManufacturerImpl;
import uk.co.jemos.podam.typeManufacturers.StringTypeManufacturerImpl;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@TestConfiguration
public class PodamConfiguration {

    public static final int PHONE_LENGTH = 13;

    @Bean
    PodamFactory podamFactory() {
        var factory = new PodamFactoryImpl();
        factory.getStrategy()
            .addOrReplaceTypeManufacturer(BigDecimal.class, new BigDecimalManufacturerImpl())
            .addOrReplaceTypeManufacturer(String.class, new StringManufacturerImpl())
            .addOrReplaceTypeManufacturer(Boolean.class, new BooleanManufacturerImpl());
        return factory;
    }

    private static class BigDecimalManufacturerImpl extends AbstractTypeManufacturer<BigDecimal> {
        @Override
        public BigDecimal getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
            return new BigDecimal(randomNumeric(20));
        }
    }

    private static class StringManufacturerImpl extends StringTypeManufacturerImpl {
        @Override
        public String getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
            if (attributeMetadata.getAttributeName() == null) {
                return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            }
            return switch (attributeMetadata.getAttributeName()) {
                case "id",
                    "uuid",
                    "unifiedId",
                    "entityId",
                    "partnerId",
                    "partnerAccountId",
                    "accountId",
                    "bankId",
                    "documentTypeId" -> UUID.randomUUID().toString();
                case "phone" -> randomNumeric(PHONE_LENGTH);
                default -> super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            };
        }
    }

    private static class BooleanManufacturerImpl extends BooleanTypeManufacturerImpl {
        @Override
        public Boolean getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
            return switch (attributeMetadata.getAttributeName()) {
                case "gku", "budget" -> false;
                default -> super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            };
        }
    }
}

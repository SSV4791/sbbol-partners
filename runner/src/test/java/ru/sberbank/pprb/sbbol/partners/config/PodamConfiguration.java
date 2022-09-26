package ru.sberbank.pprb.sbbol.partners.config;

import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@TestConfiguration
public class PodamConfiguration {

    public static final int PHONE_LENGTH = 13;
    private static final int[] WEIGHT_FACTOR = new int[]{7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1};
    private static final String STATIC_ACCOUNT_PART = "40702810";
    private static final String ACCOUNT_KEY = "0";

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
            String bic = getBic();
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
                case "kpp" -> "123456789";
                case "bic" -> bic;
                case "account" -> getValidAccountNumber(bic);
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
    //TODO DCBBRAIN-2724 Необходимо переписать логику под использование генерации валидного счета через podamFactory
    /**
     * Алгоритм расчета контрольного ключа:
     * Значение контрольного ключа приравнивается нулю (К = 0).
     * Рассчитываются произведения значений разрядов на соответствующие весовые коэффициенты.
     * Рассчитывается сумма значений младших разрядов полученных произведений.
     * Младший разряд вычисленной суммы умножается на 3.
     * Значение контрольного ключа (К) принимается равным младшему разряду полученного произведения.
     */
    public static String getValidAccountNumber(String bic) {

        String randomAccountPart = RandomStringUtils.randomNumeric(11);
        String accountForCalculate = STATIC_ACCOUNT_PART + ACCOUNT_KEY + randomAccountPart;
        String stringForCalculate = bic.substring(6) + accountForCalculate;
        int[] numberForCalculate = Arrays.stream(stringForCalculate.split("")).mapToInt(Integer::parseInt).toArray();
        int[] result = new int[numberForCalculate.length];
        for (int i = 0; i < WEIGHT_FACTOR.length; i++) {
            result[i] = numberForCalculate[i] * WEIGHT_FACTOR[i];
        }
        int resulSumma = 0;
        for (int i = 0; i < result.length; i++) {
            resulSumma += result[i] % 10;
        }

        return STATIC_ACCOUNT_PART + resulSumma * 3 % 10 + randomAccountPart;
    }

    public static String getBic() {
        String bic = "525411";
        var key = randomNumeric(3);
        return key + bic;
    }
}

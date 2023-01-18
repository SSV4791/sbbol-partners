package ru.sberbank.pprb.sbbol.partners.config;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.typeManufacturers.AbstractTypeManufacturer;
import uk.co.jemos.podam.typeManufacturers.BooleanTypeManufacturerImpl;
import uk.co.jemos.podam.typeManufacturers.IntTypeManufacturerImpl;
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
    private static final String STATIC_INN_PART = "7707";
    private static final int[] WEIGHT_FACTOR_FOR_LEGAL_ENTITY_INN = new int[]{2, 4, 10, 3, 5, 9, 4, 6, 8};
    private static final int[] WEIGHT_FACTOR_FOR_ELEVEN_KEY = new int[]{7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
    private static final int[] WEIGHT_FACTOR_FOR_TWELVE_KEY = new int[]{3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
    private static final String STATIC_OGRN_PART = "10077";
    private static final int NUMBER_FOR_CHECK = 9;
    private static final int DIVIDER_FOR_LEGAL_ENTITY = 11;
    private static final int DIVIDER_FOR_PHYSICAL_PERSON = 13;

    @Bean
    PodamFactory podamFactory() {
        var factory = new PodamFactoryImpl();
        factory.getStrategy()
            .addOrReplaceTypeManufacturer(BigDecimal.class, new BigDecimalManufacturerImpl())
            .addOrReplaceTypeManufacturer(String.class, new StringManufacturerImpl())
            .addOrReplaceTypeManufacturer(Boolean.class, new BooleanManufacturerImpl())
            .addOrReplaceTypeManufacturer(Integer.class, new IntManufacturerImpl());
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
                case "inn" -> getValidInnNumber(LegalForm.LEGAL_ENTITY);
                case "kpp" -> "123456789";
                case "ogrn" -> getValidOgrnNumber(LegalForm.LEGAL_ENTITY);
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

    private static class IntManufacturerImpl extends IntTypeManufacturerImpl {
        @Override
        public Integer getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
            if (attributeMetadata.getAttributeName() == null) {
                return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
            }
            return switch (attributeMetadata.getAttributeName()) {
                case "count" -> RandomUtils.nextInt(0, 200);
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
        for (int j : result) {
            resulSumma += j % 10;
        }

        return STATIC_ACCOUNT_PART + resulSumma * 3 % 10 + randomAccountPart;
    }

    /**
     * Для расчета десятого контрольного разряда в 10-ти значном ИНН
     * каждая цифра ИНН (кроме десятой) умножается на соответствующий множитель в соответствии с таблицей,
     * затем все значения суммируются, сумма берется по модулю 11 (остаток деления на 11),
     * затем полученное число берется по модулю 10 это и есть десятый разряд.
     * <p>
     * Для расчета 11-ого контрольного разряда (1-ой контрольной цифры) в 12-ти значном ИНН
     * каждая цифра ИНН (кроме 11-ой и 12-ой) умножается на соответствующий множитель в соответствии с таблицей,
     * затем все значения суммируются, сумма берется по модулю 11,
     * затем полученное число берется по модулю 10 это и есть 11-ый разряд.
     * <p>
     * Для расчета 12-ого контрольного разряда (2-ой контрольной цифры) в 12-ти значном ИНН
     * каждая цифра ИНН (кроме12-ой), 11-ая вычисляется в соотв. с пред. пунктом,
     * умножается на соответствующий множитель в соответствии с таблицей,
     * затем все значения суммируются, сумма берется по модулю 11,
     * затем полученное число берется по модулю 10 это и есть 12-ый разряд.
     */
    public static String getValidInnNumber(LegalForm legalForm) {

        if (legalForm == LegalForm.LEGAL_ENTITY) {
            String randomInnPart = RandomStringUtils.randomNumeric(5);
            String innForCalculate = STATIC_INN_PART + randomInnPart;
            int calculateValidKeyForInn = calculateValidKeyForInn(innForCalculate, WEIGHT_FACTOR_FOR_LEGAL_ENTITY_INN);

            return STATIC_INN_PART + randomInnPart + calculateValidKeyForInn;
        } else {
            String randomInnPart = RandomStringUtils.randomNumeric(6);
            String innForCalculate = STATIC_INN_PART + randomInnPart;
            int calculateElevenKeyForInn = calculateValidKeyForInn(innForCalculate, WEIGHT_FACTOR_FOR_ELEVEN_KEY);

            String innForCalculateTwelveKey = innForCalculate + calculateElevenKeyForInn;
            int calculateTwelveKeyForInn = calculateValidKeyForInn(innForCalculateTwelveKey, WEIGHT_FACTOR_FOR_TWELVE_KEY);

            return STATIC_INN_PART + randomInnPart + calculateElevenKeyForInn + calculateTwelveKeyForInn;
        }
    }

    /**
     * Алгоритм проверки ОГРН/ОГРНИП
     * Выбрать 12-значное число ОГРН (с 1-й по 12-ю цифру) / 14-значное число ОГРНИП (с 1-й по 14-ю цифру).
     * Вычислить остаток от деления выбранного числа на 11 (ОГРН) / на 13 (ОГРНИП).
     * Если остаток больше 9, то контрольная цифра = последней цифре остатка
     * Сравнить младший разряд полученного остатка от деления с 13-й цифрой ОГРН / 15-й цифрой ОГРНИП. Если они равны, то ОГРН/ОГРНИП верный.
     */
    public static String getValidOgrnNumber(LegalForm legalForm) {
        if (legalForm == LegalForm.LEGAL_ENTITY) {
            String randomOgrnPart = RandomStringUtils.randomNumeric(7);
            String ogrnForCalculate = STATIC_OGRN_PART + randomOgrnPart;
            long validKeyForOgrn = calculateValidKeyForOgrn(ogrnForCalculate, legalForm);

            return ogrnForCalculate + validKeyForOgrn;
        } else {
            String randomOgrnPart = RandomStringUtils.randomNumeric(9);
            String ogrnForCalculate = STATIC_OGRN_PART + randomOgrnPart;
            long validKeyForOgrn = calculateValidKeyForOgrn(ogrnForCalculate, legalForm);

            return ogrnForCalculate + validKeyForOgrn;
        }
    }

    private static int calculateValidKeyForInn(String innForCalculate, int[] weightFactorForCalculate) {
        int[] numberForCalculate = Arrays.stream(innForCalculate.split("")).mapToInt(Integer::parseInt).toArray();
        int[] result = new int[numberForCalculate.length];
        for (int i = 0; i < weightFactorForCalculate.length; i++) {
            result[i] = numberForCalculate[i] * weightFactorForCalculate[i];
        }
        int resulSumma = 0;
        for (int numberForCalculatingElevenKey : result) {
            resulSumma += numberForCalculatingElevenKey;
        }

        return (resulSumma % 11) % 10;
    }

    private static long calculateValidKeyForOgrn(String ogrnForCalculate, LegalForm legalForm) {
        long ogrnNumber = Long.parseLong(ogrnForCalculate);
        if (legalForm == LegalForm.LEGAL_ENTITY) {
            long key = ogrnNumber % DIVIDER_FOR_LEGAL_ENTITY;
            if (key > NUMBER_FOR_CHECK) {
                key = key % 10;
            }
            return key;
        } else {
            long key = ogrnNumber % DIVIDER_FOR_PHYSICAL_PERSON;
            if (key > NUMBER_FOR_CHECK) {
                key = key % 10;
            }
            return key;
        }
    }

    public static String getBic() {
        String bic = "525411";
        var key = randomNumeric(3);
        return key + bic;
    }

    public static String getValidOkpoNumber(LegalForm form) {
        return switch (form) {
            case LEGAL_ENTITY, PHYSICAL_PERSON -> randomNumeric(8);
            case ENTREPRENEUR -> randomNumeric(10);
        };
    }
}

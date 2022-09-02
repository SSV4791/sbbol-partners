package ru.sberbank.pprb.sbbol.partners.validation;

import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.OgrnKeyValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OgrnKeyValidator
    implements ConstraintValidator<OgrnKeyValidation, String> {

    private static final int OGRN_13_VALID_LENGTH = 13;
    private static final int OGRN_15_VALID_LENGTH = 15;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        return switch (value.length()) {
            /*
              1. Берем первые 12 чисел из 13
              2. Получить остаток от деления первых 12 чисел из 13 на делить = 11 - это и будет контрольным числом
              3. Если контрольное число/остаток больше 9, то считаем что контрольное число = последней цифре остатка (например 12 = 2)
              4. Остаток от деления сравниваем с числом 13 ОГРН, если есть равенство, то ОГРН верный
             */
            case OGRN_13_VALID_LENGTH -> checkOgrn(value, ogrnControlNumber(value.substring(0, value.length() - 1), 11));
            /*
              1. То же самое что для 13, только: Для вычисления необходимо брать первые 14 из 15 знаков и делитель будет = 13
             */
            case OGRN_15_VALID_LENGTH -> checkOgrn(value, ogrnControlNumber(value.substring(0, value.length() - 1), 13));
            default -> false;
        };
    }

    private boolean checkOgrn(String ogrn, long controlSum) {
        int controlNum = Integer.parseInt(ogrn.substring(ogrn.length() - 1));
        if (controlSum > 9) {
            return controlNum == (controlSum % 10);
        }
        return controlNum == controlSum;
    }

    private static long ogrnControlNumber(String dividend, int divider) {
        return Long.parseLong(dividend) % divider;
    }

}

package ru.sberbank.pprb.sbbol.partners.validation.common;

public final class BasePartnerValidation {

    public static final int KPP_VALID_LENGTH = 9;
    private static final int INN_5_VALID_LENGTH = 5;
    private static final int INN_10_VALID_LENGTH = 10;
    private static final int INN_12_VALID_LENGTH = 12;
    private static final int OGRN_13_VALID_LENGTH = 13;
    private static final int OGRN_15_VALID_LENGTH = 15;
    private static final int[] INN_12_SYMBOL_CONTROL_KEY_ONE = {7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
    private static final int[] INN_12_SYMBOL_CONTROL_KEY_TWO = {3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
    private static final int[] INN_10_SYMBOL_CONTROL_KEY = {2, 4, 10, 3, 5, 9, 4, 6, 8};

    private BasePartnerValidation() {
        throw new AssertionError();
    }

    public static boolean checkInn(String inn) {
        return switch (inn.length()) {
            /*
              1. Вычисляется контрольная сумма со следующими весовыми коэффициентами: (2,4,10,3,5,9,4,6,8) на 9 первых чисел ИНН
              2. Вычисляется контрольное число как остаток от деления контрольной суммы на 11
              3. Если контрольное число больше 9, то контрольное число вычисляется как остаток от деления контрольного числа на 10
              4. Контрольное число проверяется с десятым знаком ИНН. В случае их равенства ИНН считается правильным.
             */
            case INN_10_VALID_LENGTH -> checkInn(inn, INN_10_SYMBOL_CONTROL_KEY);
            /*
              1. Вычисляется контрольная сумма по 11-ти знакам со следующими весовыми коэффициентами: (7,2,4,10,3,5,9,4,6,8) на 10 первых чисел ИНН
              2. Вычисляется контрольное число(1) как остаток от деления контрольной суммы на 11
              3. Если контрольное число(1) больше 9, то контрольное число(1) вычисляется как остаток от деления контрольного числа(1) на 10
              4. Вычисляется контрольная сумма по 12-ти знакам со следующими весовыми коэффициентами: (3,7,2,4,10,3,5,9,4,6,8) на 11 первых чисел ИНН
              5. Вычисляется контрольное число(2) как остаток от деления контрольной суммы на 11
              6. Если контрольное число(2) больше 9, то контрольное число(2) вычисляется как остаток от деления контрольного числа(2) на 10
              7. Контрольное число(1) проверяется с одиннадцатым знаком ИНН и контрольное число(2) проверяется с двенадцатым знаком ИНН.
             */
            case INN_12_VALID_LENGTH -> checkInn(inn, INN_12_SYMBOL_CONTROL_KEY_ONE) && checkInn(inn, INN_12_SYMBOL_CONTROL_KEY_TWO);
            // Валидация для КИО
            case INN_5_VALID_LENGTH -> true;
            default -> false;
        };
    }

    public static boolean checkInn(String inn, int[] control) {
        int checkSum = 0;
        for (int i = 0; i < control.length; i++) {
            var checkSymbol = inn.substring(i, i + 1);
            checkSum += Integer.parseInt(checkSymbol) * control[i];
        }
            /**
             * В связи с тем что checkSum % 11 = может быть от 1 до 10 и при безусловном делении на 10 будет по остатку соблюдены все условия;
             */
        return checkSum % 11 % 10 == Integer.parseInt(inn.substring(control.length, control.length + 1));
    }

    public static boolean checkOgrn(String ogrn) {
        return switch (ogrn.length()) {
            /*
              1. Берем первые 12 чисел из 13
              2. Получить остаток от деления первых 12 чисел из 13 на делить = 11 - это и будет контрольным числом
              3. Если контрольное число/остаток больше 9, то считаем что контрольное число = последней цифре остатка (например 12 = 2)
              4. Остаток от деления сравниваем с числом 13 ОГРН, если есть равенство, то ОГРН верный
             */
            case OGRN_13_VALID_LENGTH -> checkOgrn(ogrn, ogrnControlNumber(ogrn.substring(0, ogrn.length() - 1), 11));
            /*
              1. То же самое что для 13, только: Для вычисления необходимо брать первые 14 из 15 знаков и делитель будет = 13
             */
            case OGRN_15_VALID_LENGTH -> checkOgrn(ogrn, ogrnControlNumber(ogrn.substring(0, ogrn.length() - 1), 13));
            default -> false;
        };
    }

    public static boolean checkOgrn(String ogrn, long controlSum) {
        int controlNum = Integer.parseInt(ogrn.substring(ogrn.length() - 1));
        if (controlSum > 9) {
            return controlNum == (controlSum % 10);
        }
        return controlNum == controlSum;
    }

    public static long ogrnControlNumber(String dividend, int divider) {
        return Long.parseLong(dividend) % divider;
    }
}

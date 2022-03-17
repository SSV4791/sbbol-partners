package ru.sberbank.pprb.sbbol.partners.validation.common;

import org.apache.commons.lang3.StringUtils;

public final class BasePartnerAccountValidation {

    private static final int[] CONTROL_KEY_ACCOUNT = new int[]{7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1};

    public static final int CONTROL_KEY_BIC = 3;
    public static final int ACCOUNT_VALID_LENGTH = 20;
    public static final int BIC_VALID_LENGTH = 9;

    private BasePartnerAccountValidation() {
        throw new AssertionError();
    }

    /**
     * Реализация валидации счета
     * Счёт 40602810800000000025, БИК 049805746.
     * Расчёт контрольной цифры.
     * Добавляем 4-6 символы БИК и 0: 08040602810800000000025.
     * Контрольная сумма: 7*7+4*1+6*3+4*7+0*1+6*3+0*7+2*1+8*3+1*7+0*1+8*3+0*7+0*1+0*3+0*7+0*1+0*3+0*7+0*1+0*3+2*7+5*1
     * = 49+4+18+28+0+18+0+2+24+7+0+24+0+0+0+0+0+0+0+0+0+14+5 = 193.
     * Контрольное число: 193 mod 10 = 3.
     * Итог: контрольное число отлично от нуля, следовательно счет указан неверно - отсутствует в данном банке (БИК).
     */
    public static boolean bankAccountValid(String account, String bic) {
        if (bic.length() != BIC_VALID_LENGTH) {
            return true;
        }
        return accountValid(account, "0" + bic.substring(4, 6));
    }

    /**
     * Реализация валидации счета
     * Счёт 40602810800000000025, БИК 049805746.
     * Расчёт контрольной цифры.
     * Добавляем 3 последних символа БИК: 74640602810800000000025.
     * Контрольная сумма: 7*7+4*1+6*3+4*7+0*1+6*3+0*7+2*1+8*3+1*7+0*1+8*3+0*7+0*1+0*3+0*7+0*1+0*3+0*7+0*1+0*3+2*7+5*1
     * = 49+4+18+28+0+18+0+2+24+7+0+24+0+0+0+0+0+0+0+0+0+14+5 = 193.
     * Контрольное число: 193 mod 10 = 3.
     * Итог: контрольное число отлично от нуля, следовательно счет указан неверно - отсутствует в данном банке (БИК).
     */
    public static boolean userAccountValid(String account, String bic) {
        if (bic.length() != BIC_VALID_LENGTH) {
            return true;
        }
        return accountValid(account, StringUtils.right(bic, CONTROL_KEY_BIC));
    }

    /**
     * Проверка валидности счета
     *
     * @param account счёт
     * @param keyBic  ключ БИКа
     * @return Результат проверки
     */
    public static boolean accountValid(String account, String keyBic) {
        if (account.length() != ACCOUNT_VALID_LENGTH) {
            return false;
        }
        var checkAccount = keyBic + account;
        int controlSum = 0;
        for (int i = 0; i < checkAccount.length(); i++) {
            var checkSymbol = checkAccount.substring(i, i + 1);
            controlSum += (Integer.parseInt(checkSymbol) * CONTROL_KEY_ACCOUNT[i]);
        }
        return controlSum % 10 == 0;
    }
}

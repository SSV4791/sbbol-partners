package ru.sberbank.pprb.sbbol.partners.validation.common;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public final class BasePartnerAccountValidation {

    private static final int[] CONTROL_KEY_ACCOUNT = new int[]{7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1};

    public static final int CONTROL_KEY_BIC = 3;
    public static final int ACCOUNT_VALID_LENGTH = 20;
    public static final int BIC_VALID_LENGTH = 9;
    public static final Pattern BUDGET_ACCOUNT = Pattern.compile("^0[0-9]{4}643[0-9]{12}$");
    public static final Pattern CORR_ACCOUNT_EKS = Pattern.compile("^40102[0-9]{15}$");
    public static final Pattern RKC_BIC = Pattern.compile("^[0-9]{6}00[0-9]$");

    private BasePartnerAccountValidation() {
        throw new AssertionError();
    }

    /**
     * Реализация валидации счета
     * Счет 40602810800000000025, БИК 049805746.
     * Расчет контрольной цифры.
     * Добавляем 4-6 символы БИК и 0: 08040602810800000000025.
     * Контрольная сумма: 7*7+4*1+6*3+4*7+0*1+6*3+0*7+2*1+8*3+1*7+0*1+8*3+0*7+0*1+0*3+0*7+0*1+0*3+0*7+0*1+0*3+2*7+5*1
     * = 49+4+18+28+0+18+0+2+24+7+0+24+0+0+0+0+0+0+0+0+0+14+5 = 193.
     * Контрольное число: 193 mod 10 = 3.
     * Итог: контрольное число отлично от нуля, следовательно счет указан неверно - отсутствует в данном банке (БИК).
     */
    public static boolean validateBankAccount(String account, String bic) {
        if (bic.length() != BIC_VALID_LENGTH) {
            return false;
        }
        var matchCorrAccEks = CORR_ACCOUNT_EKS.matcher(account);
        if (matchCorrAccEks.matches()) {
            return true;
        }
        return validateAccount(account, "0" + bic.substring(4, 6));
    }

    /**
     * Реализация валидации счета
     * Счет 40602810800000000025, БИК 049805746.
     * Расчет контрольной цифры.
     * Добавляем 3 последних символа БИК: 74640602810800000000025.
     * Контрольная сумма: 7*7+4*1+6*3+4*7+0*1+6*3+0*7+2*1+8*3+1*7+0*1+8*3+0*7+0*1+0*3+0*7+0*1+0*3+0*7+0*1+0*3+2*7+5*1
     * = 49+4+18+28+0+18+0+2+24+7+0+24+0+0+0+0+0+0+0+0+0+14+5 = 193.
     * Контрольное число: 193 mod 10 = 3.
     * Итог: контрольное число отлично от нуля, следовательно счет указан неверно - отсутствует в данном банке (БИК).
     */
    public static boolean validateUserAccount(String account, String bic) {
        if (bic.length() != BIC_VALID_LENGTH) {
            return false;
        }
        return validateAccount(account, StringUtils.right(bic, CONTROL_KEY_BIC));
    }

    /**
     * Проверка валидности счета
     *
     * @param account счет
     * @param keyBic  ключ БИКа
     * @return Результат проверки
     */
    public static boolean validateAccount(String account, String keyBic) {
        if (account.length() != ACCOUNT_VALID_LENGTH) {
            return false;
        }
        var matchAcc = BUDGET_ACCOUNT.matcher(account);
        if (matchAcc.matches()) {
            return true;
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

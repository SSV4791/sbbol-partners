package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;

import java.util.ArrayList;

public class PartnerAccountValidator implements Validator<Account> {

    private static final int[] CONTROL_KEY_ACCOUNT = new int[]{7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1};

    public static final int CONTROL_KEY_BIC = 3;
    public static final int ACCOUNT_VALID_LENGTH = 20;
    public static final int BIC_VALID_LENGTH = 9;

    @Override
    public ArrayList<String> validation(Account entity) {
        var errors = new ArrayList<String>();
        if (entity.getPartnerId() == null) {
            errors.add("Ошибка заполнения partnerId, поле обязательно для заполнения");
        }
        if (entity.getDigitalId() == null) {
            errors.add("Ошибка заполнения digitalId, поле обязательно для заполнения");
        }
        if (entity.getAccount() == null) {
            errors.add("Ошибка заполнения поля account, поле обязательно к заполнению");
        }
        if (entity.getAccount().length() != ACCOUNT_VALID_LENGTH) {
            errors.add("Ошибка заполнения поля account, поле меньше/больше 20 символов");
        }
        if (CollectionUtils.isEmpty(entity.getBanks())) {
            errors.add("Ошибка заполнения поля bank, поле обязательно к заполнению");
        } else {
            checkBank(entity, errors);
        }
        return errors;
    }

    private void checkBank(Account entity, ArrayList<String> errors) {
        for (Bank bank : entity.getBanks()) {
            if (bank.getBic() == null) {
                errors.add("Ошибка заполнения поля bank.bic, поле обязательно к заполнению");
            }
            if (bank.getBic().length() != BIC_VALID_LENGTH) {
                errors.add("Ошибка заполнения поля bic, поле меньше/больше 9 символов");
            }
            if (bank.getName() == null) {
                errors.add("Ошибка заполнения поля bank.name, поле обязательно к заполнению");
            }
            if (CollectionUtils.isEmpty(bank.getBankAccounts())) {
                errors.add("Ошибка заполнения поля bank.bankAccounts, поле обязательно к заполнению");
            } else {
                checkBankAccount(bank, errors);
            }
            if (!userAccountValid(entity.getAccount(), bank.getBic())) {
                errors.add("Ошибка заполнения поля account, " + entity.getAccount() + " не проходит проверку контрольного числа");
            }
        }
    }

    private void checkBankAccount(Bank bank, ArrayList<String> errors) {
        for (BankAccount bankAccount : bank.getBankAccounts()) {
            if (bankAccount.getAccount() == null) {
                errors.add("Ошибка заполнения поля bank.bankAccount.account, поле обязательно к заполнению");
            }
            if (!bankAccountValid(bankAccount.getAccount(), bank.getBic())) {
                errors.add("Ошибка заполнения поля account, " + bankAccount.getAccount() + " не проходит проверку контрольного числа");
            }
        }
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
    private static boolean bankAccountValid(String account, String bic) {
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
    private static boolean userAccountValid(String account, String bic) {
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
    private static boolean accountValid(String account, String keyBic) {
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

package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.bankAccountValid;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.userAccountValid;

public class PartnerAccountValidator implements Validator<AccountChange> {

    public static final String DEFAULT_FIELD_IS_NULL = "default.field.is_null";

    @Override
    public void validation(List<String> errors, AccountChange entity) {
        if (entity.getId() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "id"));
        }
        if (entity.getPartnerId() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "partnerId"));
        }
        if (entity.getDigitalId() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "digitalId"));
        }
        if (entity.getAccount() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "account"));
        }
        if (entity.getAccount().length() != BasePartnerAccountValidation.ACCOUNT_VALID_LENGTH) {
            errors.add(MessagesTranslator.toLocale("account.account.length"));
        }
        if (entity.getBank() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "bank"));
        } else {
            checkBank(entity, errors);
        }
    }

    private void checkBank(AccountChange entity, List<String> errors) {
        var bank = entity.getBank();
        if (bank.getBic() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "bank.bic"));
        }
        if (bank.getBic().length() != BasePartnerAccountValidation.BIC_VALID_LENGTH) {
            errors.add("account.bic.length");
        }
        if (bank.getName() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "bank.name"));
        }
        if (bank.getBankAccount() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "bank.bankAccounts"));
        } else {
            checkBankAccount(bank, errors);
        }
        if (!userAccountValid(entity.getAccount(), bank.getBic())) {
            errors.add(MessagesTranslator.toLocale("account.account.control_number", entity.getAccount()));
        }
    }

    private void checkBankAccount(Bank bank, List<String> errors) {
        var bankAccount = bank.getBankAccount();
        if (bankAccount.getAccount() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "bank.bankAccount.account"));
        }
        if (!bankAccountValid(bankAccount.getAccount(), bank.getBic())) {
            errors.add(MessagesTranslator.toLocale("account.bank_account.control_number", bankAccount.getAccount()));
        }
    }
}

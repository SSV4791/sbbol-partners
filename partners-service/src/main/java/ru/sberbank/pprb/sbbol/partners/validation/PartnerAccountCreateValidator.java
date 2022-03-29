
package ru.sberbank.pprb.sbbol.partners.validation;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.bankAccountValid;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.userAccountValid;

public class PartnerAccountCreateValidator implements Validator<AccountCreate> {

    public static final String DEFAULT_FIELD_IS_NULL = "default.field.is_null";

    @Override
    public void validation(List<String> errors, AccountCreate entity) {
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
        if (CollectionUtils.isEmpty(entity.getBanks())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "bank"));
        } else {
            checkBank(entity, errors);
        }
    }

    private void checkBank(AccountCreate entity, List<String> errors) {
        for (BankCreate bank : entity.getBanks()) {
            if (bank.getBic() == null) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "bank.bic"));
            }
            if (bank.getBic().length() != BasePartnerAccountValidation.BIC_VALID_LENGTH) {
                errors.add("account.bic.length");
            }
            if (bank.getName() == null) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "bank.name"));
            }
            if (CollectionUtils.isEmpty(bank.getBankAccounts())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "bank.bankAccounts"));
            } else {
                checkBankAccount(bank, errors);
            }
            if (!userAccountValid(entity.getAccount(), bank.getBic())) {
                errors.add(MessagesTranslator.toLocale("account.account.control_number", entity.getAccount()));
            }
        }
    }

    private void checkBankAccount(BankCreate bank, List<String> errors) {
        for (BankAccountCreate bankAccount : bank.getBankAccounts()) {
            if (bankAccount.getAccount() == null) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_FIELD_IS_NULL, "bank.bankAccount.account"));
            }
            if (!bankAccountValid(bankAccount.getAccount(), bank.getBic())) {
                errors.add(MessagesTranslator.toLocale("account.bank_account.control_number", bankAccount.getAccount()));
            }
        }
    }

}
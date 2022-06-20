
package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.List;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.ACCOUNT_VALID_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.BIC_VALID_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.RKC_BIC;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.validateBankAccount;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.validateUserAccount;

public class AccountCreateValidatorImpl extends AbstractValidatorImpl<AccountCreate> {

    private static final String DOCUMENT_NAME = "partner";
    private static final String DEFAULT_MESSAGE_ACCOUNT_LENGTH = "account.account.length";
    private static final String DEFAULT_MESSAGE_BIC_LENGTH = "account.bic.length";
    private static final String DEFAULT_MESSAGE_BANK_ACCOUNT_CONTROL_NUMBER = "account.bank_account.control_number";
    private static final String DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER = "account.account.control_number";
    private final PartnerRepository partnerRepository;

    public AccountCreateValidatorImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, AccountCreate entity) {
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getPartnerId()));
        if (foundPartner.isEmpty()) {
            throw new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getPartnerId()));
        }
        commonValidationUuid(errors, entity.getPartnerId());
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (StringUtils.isNotEmpty(entity.getComment()) && entity.getComment().length() > COMMENT_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "comment", "1", "50"));
        }
        if (StringUtils.isNotEmpty(entity.getAccount()) && entity.getAccount().length() != ACCOUNT_VALID_LENGTH) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_LENGTH));
        }
        if (entity.getBank() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "bank"));
        } else {
            checkBank(entity, errors);
        }
    }

    private void checkBank(AccountCreate entity, List<String> errors) {
        var bank = entity.getBank();
        if (bank.getBic() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "bank.bic"));
        }
        if (bank.getBic().length() != BIC_VALID_LENGTH) {
            errors.add(DEFAULT_MESSAGE_BIC_LENGTH);
        }
        if (StringUtils.isNotEmpty(bank.getName()) && bank.getName().length() > BANK_NAME_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "bank.name", "1", "160"));
        }
        if (bank.getName() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "bank.name"));
        }
        var matchBic = RKC_BIC.matcher(bank.getBic());
        if (matchBic.matches()) {
            if (StringUtils.isNotEmpty(entity.getAccount()) && !validateBankAccount(entity.getAccount(), bank.getBic())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER, entity.getAccount()));
            }
        } else if (StringUtils.isNotEmpty(entity.getAccount()) && !validateUserAccount(entity.getAccount(), bank.getBic())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER, entity.getAccount()));
        }
        if (bank.getBankAccount() != null) {
            checkBankAccount(bank, errors);
        }
    }

    private void checkBankAccount(BankCreate bank, List<String> errors) {
        var bankAccount = bank.getBankAccount();
        if (StringUtils.isEmpty(bankAccount.getBankAccount())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "bank.bankAccount.account"));
        }
        if (StringUtils.isNotEmpty(bankAccount.getBankAccount()) && bankAccount.getBankAccount().length() != ACCOUNT_VALID_LENGTH) {
            errors.add(DEFAULT_MESSAGE_ACCOUNT_LENGTH);
        }
        if (StringUtils.isNotEmpty(bankAccount.getBankAccount()) && !validateBankAccount(bankAccount.getBankAccount(), bank.getBic())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_BANK_ACCOUNT_CONTROL_NUMBER, bankAccount.getBankAccount()));
        }
    }
}

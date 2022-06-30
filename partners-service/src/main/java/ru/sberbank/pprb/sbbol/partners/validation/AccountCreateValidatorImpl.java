
package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.util.ObjectUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.ACCOUNT_VALID_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.BIC_VALID_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.RKC_BIC;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.validateBankAccount;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.validateUserAccount;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class AccountCreateValidatorImpl extends AbstractValidatorImpl<AccountCreate> {

    private static final String DOCUMENT_NAME = "partner";
    private static final String DEFAULT_MESSAGE_ACCOUNT_LENGTH = "account.account.length";
    private static final String DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER = "account.account.control_number";
    private static final String DEFAULT_MESSAGE_BANK_ACCOUNT_CONTROL_NUMBER = "account.account.bank_account.control_number";
    private static final String DEFAULT_MESSAGE_ACCOUNT_IS_NULL = "account.account.fields.is_null";
    private static final String DEFAULT_MESSAGE_BIC_LENGTH = "account.account.bank.bic_length";
    private final PartnerRepository partnerRepository;

    public AccountCreateValidatorImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(Map<String, List<String>> errors, AccountCreate entity) {
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getPartnerId()));
        if (isEmpty(foundPartner)) {
            throw new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getPartnerId()));
        }
        commonValidationUuid(errors, entity.getPartnerId());
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (StringUtils.isNotEmpty(entity.getComment()) && entity.getComment().length() > COMMENT_MAX_LENGTH_VALIDATION) {
            setError(errors, "account_comment", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getAccount()) && entity.getAccount().length() != ACCOUNT_VALID_LENGTH) {
            setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_LENGTH));
        }
        if (entity.getBank() == null) {
            setError(errors, "bank", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "реквизиты банка"));
        } else {
            checkBank(entity, errors);
        }
    }

    private void checkBank(AccountCreate entity, Map<String, List<String>> errors) {
        var bank = entity.getBank();
        if (bank.getBic() == null) {
            setError(errors, "bic", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "реквизиты банка"));
        }
        if (bank.getBic().length() != BIC_VALID_LENGTH) {
            setError(errors, "bic", DEFAULT_MESSAGE_BIC_LENGTH);
        }
        if (StringUtils.isNotEmpty(bank.getName()) && bank.getName().length() > BANK_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "bankName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "160"));
        }
        if (bank.getName() == null) {
            setError(errors, "bankName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "название банка"));
        }
        var matchBic = RKC_BIC.matcher(bank.getBic());
        if (matchBic.matches()) {
            if (StringUtils.isNotEmpty(entity.getAccount()) && !validateBankAccount(entity.getAccount(), bank.getBic())) {
                setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER));
            }
        } else if (StringUtils.isNotEmpty(entity.getAccount()) && !validateUserAccount(entity.getAccount(), bank.getBic())) {
            setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER));
        }
        if (bank.getBankAccount() != null) {
            checkBankAccount(bank, errors);
        }
    }

    private void checkBankAccount(BankCreate bank, Map<String, List<String>> errors) {
        var bankAccount = bank.getBankAccount();
        if (StringUtils.isEmpty(bankAccount.getBankAccount())) {
            setError(errors, "bankAccount", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "корреспондентский счёт"));
        }
        if (StringUtils.isNotEmpty(bankAccount.getBankAccount()) && bankAccount.getBankAccount().length() != ACCOUNT_VALID_LENGTH) {
            setError(errors, "bankAccount", DEFAULT_MESSAGE_ACCOUNT_LENGTH);
        }
        if (StringUtils.isNotEmpty(bankAccount.getBankAccount()) && !validateBankAccount(bankAccount.getBankAccount(), bank.getBic())) {
            setError(errors, "bankAccount", MessagesTranslator.toLocale(DEFAULT_MESSAGE_BANK_ACCOUNT_CONTROL_NUMBER));
        }
    }
}

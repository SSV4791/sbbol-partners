package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.ACCOUNT_VALID_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.BIC_VALID_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.RKC_BIC;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.validateBankAccount;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.validateUserAccount;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class AccountUpdateValidatorImpl extends AbstractValidatorImpl<AccountChange> {

    private static final String DOCUMENT_NAME = "account";
    private static final String DEFAULT_MESSAGE_ACCOUNT_LENGTH = "account.account.length";
    private static final String DEFAULT_MESSAGE_BIC_LENGTH = "account.account.bank.bic_length";
    private static final String DEFAULT_MESSAGE_BANK_ACCOUNT_CONTROL_NUMBER = "account.account.bank_account.control_number";
    private static final String DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER = "account.account.control_number";
    private static final String DEFAULT_MESSAGE_ACCOUNT_IS_NULL = "account.account.fields.is_null";
    private static final String DEFAULT_MESSAGE_ACCOUNT_SIGN_IS_TRUE = "account.account.sign.is_true";
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountUpdateValidatorImpl(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(Map<String, List<String>> errors, AccountChange entity) {
        validateUpdateAccount(entity, errors);
        commonValidationUuid(errors, entity.getPartnerId(), entity.getId());
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (StringUtils.isNotEmpty(entity.getComment()) && entity.getComment().length() > COMMENT_MAX_LENGTH_VALIDATION) {
            setError(errors, "account_comment", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getAccount()) && entity.getAccount().length() != ACCOUNT_VALID_LENGTH) {
            setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_LENGTH));
        }
        if (entity.getVersion() == null) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "version"));
        }
        if (entity.getBank() != null) {
            checkBank(entity, errors);
        }
    }

    private void validateUpdateAccount(AccountChange entity, Map<String, List<String>> errors) {
        var foundAccount = accountRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        if (!foundAccount.getPartnerUuid().toString().equals(entity.getPartnerId())) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getPartnerId()));
        }
        if (!entity.getVersion().equals(foundAccount.getVersion())) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundAccount.getVersion().toString(), entity.getVersion().toString()));
        }
        if (AccountStateType.SIGNED == foundAccount.getState()) {
            setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_SIGN_IS_TRUE, entity.getAccount()));
        }
        accountMapper.updateAccount(entity, foundAccount);
        if (foundAccount.getBank() == null || foundAccount.getBank().getBic() == null || foundAccount.getBank().getBankAccount() == null || foundAccount.getBank().getBankAccount().getAccount() == null) {
            setError(errors, "bic", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "реквизиты банка"));
        }
        var matchBic = RKC_BIC.matcher(foundAccount.getBank().getBic());
        if (matchBic.matches()) {
            if (StringUtils.isNotEmpty(foundAccount.getAccount()) && !validateBankAccount(foundAccount.getAccount(), foundAccount.getBank().getBic())) {
                setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER));
            }
        } else if (StringUtils.isNotEmpty(foundAccount.getAccount()) && !validateUserAccount(foundAccount.getAccount(), foundAccount.getBank().getBic())) {
            setError(errors, "account", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER));
        }
        if (!BasePartnerAccountValidation.validateBankAccount(foundAccount.getBank().getBankAccount().getAccount(), foundAccount.getBank().getBic())) {
            setError(errors, "bankAccount", MessagesTranslator.toLocale(DEFAULT_MESSAGE_BANK_ACCOUNT_CONTROL_NUMBER));
        }
    }

    private void checkBank(AccountChange entity, Map<String, List<String>> errors) {
        var bank = entity.getBank();
        if (bank.getBic() != null && bank.getBic().equals(EMPTY)) {
            setError(errors, "bic", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "реквизиты банка"));
        }
        if (StringUtils.isNotEmpty(bank.getBic()) && bank.getBic().length() != BIC_VALID_LENGTH) {
            setError(errors, "bic", MessagesTranslator.toLocale(DEFAULT_MESSAGE_BIC_LENGTH));
        }
        if (StringUtils.isNotEmpty(bank.getName()) && bank.getName().length() > BANK_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "bankName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "160"));
        }
        if (StringUtils.isNotEmpty(bank.getBic()) && StringUtils.isEmpty(bank.getName())) {
            setError(errors, "bic", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "название банка"));
        }
        if (bank.getBankAccount() != null) {
            checkBankAccount(bank, errors);
        }
    }

    private void checkBankAccount(Bank bank, Map<String, List<String>> errors) {
        var bankAccount = bank.getBankAccount();
        if (bankAccount.getBankAccount() != null && bankAccount.getBankAccount().equals(EMPTY)) {
            setError(errors, "bankAccount", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_IS_NULL, "корреспондентский счёт"));
        }
        if (StringUtils.isNotEmpty(bankAccount.getBankAccount()) && bankAccount.getBankAccount().length() != ACCOUNT_VALID_LENGTH) {
            setError(errors, "bankAccount", MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_LENGTH));
        }
    }
}

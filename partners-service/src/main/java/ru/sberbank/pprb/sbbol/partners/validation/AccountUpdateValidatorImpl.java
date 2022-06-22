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
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.ACCOUNT_VALID_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.BIC_VALID_LENGTH;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.RKC_BIC;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.validateBankAccount;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerAccountValidation.validateUserAccount;

public class AccountUpdateValidatorImpl extends AbstractValidatorImpl<AccountChange> {

    private static final String DOCUMENT_NAME = "account";
    private static final String DEFAULT_MESSAGE_ACCOUNT_LENGTH = "account.account.length";
    private static final String DEFAULT_MESSAGE_BIC_LENGTH = "account.bic.length";
    private static final String DEFAULT_MESSAGE_BANK_ACCOUNT_CONTROL_NUMBER = "account.bank_account.control_number";
    private static final String DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER = "account.account.control_number";
    private static final String DEFAULT_MESSAGE_ACCOUNT_SIGN_IS_TRUE = "account.account.sign.is_true";
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountUpdateValidatorImpl(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, AccountChange entity) {
        validateUpdateAccount(entity, errors);
        commonValidationUuid(errors, entity.getPartnerId(), entity.getId());
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (StringUtils.isNotEmpty(entity.getComment()) && entity.getComment().length() > COMMENT_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "comment", "1", "50"));
        }
        if (StringUtils.isNotEmpty(entity.getAccount()) && entity.getAccount().length() != ACCOUNT_VALID_LENGTH) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_LENGTH));
        }
        if (entity.getVersion() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "version"));
        }
        if (entity.getBank() != null) {
            checkBank(entity, errors);
        }
    }

    private void validateUpdateAccount(AccountChange entity, List<String> errors) {
        var foundAccount = accountRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        if (!foundAccount.getPartnerUuid().toString().equals(entity.getPartnerId())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getPartnerId()));
        }
        if (!entity.getVersion().equals(foundAccount.getVersion())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundAccount.getVersion().toString(), entity.getVersion().toString()));
        }
        if (AccountStateType.SIGNED == foundAccount.getState()) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_SIGN_IS_TRUE, entity.getAccount()));
        }
        accountMapper.updateAccount(entity, foundAccount);
        if (foundAccount.getBank() == null || foundAccount.getBank().getBic() == null || foundAccount.getBank().getBankAccount() == null || foundAccount.getBank().getBankAccount().getAccount() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "bic/bankAccount", DOCUMENT_NAME));
        }
        var matchBic = RKC_BIC.matcher(foundAccount.getBank().getBic());
        if (matchBic.matches()) {
            if (StringUtils.isNotEmpty(foundAccount.getAccount()) && !validateBankAccount(foundAccount.getAccount(), foundAccount.getBank().getBic())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER, foundAccount.getAccount()));
            }
        } else if (StringUtils.isNotEmpty(foundAccount.getAccount()) && !validateUserAccount(foundAccount.getAccount(), foundAccount.getBank().getBic())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_ACCOUNT_CONTROL_NUMBER, foundAccount.getAccount()));
        }
        if (!BasePartnerAccountValidation.validateBankAccount(foundAccount.getBank().getBankAccount().getAccount(), foundAccount.getBank().getBic())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_BANK_ACCOUNT_CONTROL_NUMBER, foundAccount.getBank().getBankAccount().getAccount()));
        }
    }

    private void checkBank(AccountChange entity, List<String> errors) {
        var bank = entity.getBank();
        if (bank.getBic() != null && bank.getBic().equals(EMPTY)) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "bank.bic"));
        }
        if (StringUtils.isNotEmpty(bank.getBic()) && bank.getBic().length() != BIC_VALID_LENGTH) {
            errors.add(DEFAULT_MESSAGE_BIC_LENGTH);
        }
        if (StringUtils.isNotEmpty(bank.getName()) && bank.getName().length() > BANK_NAME_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "bank.name", "1", "160"));
        }
        if (StringUtils.isNotEmpty(bank.getBic()) && StringUtils.isEmpty(bank.getName())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "bank.name"));
        }
        if (bank.getBankAccount() != null) {
            checkBankAccount(bank, errors);
        }
    }

    private void checkBankAccount(Bank bank, List<String> errors) {
        var bankAccount = bank.getBankAccount();
        if (bankAccount.getBankAccount() != null && bankAccount.getBankAccount().equals(EMPTY)) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "bankAccount.account"));
        }
        if (bankAccount.getBankAccount() != null && bankAccount.getBankAccount().length() != ACCOUNT_VALID_LENGTH) {
            errors.add(DEFAULT_MESSAGE_ACCOUNT_LENGTH);
        }
    }
}

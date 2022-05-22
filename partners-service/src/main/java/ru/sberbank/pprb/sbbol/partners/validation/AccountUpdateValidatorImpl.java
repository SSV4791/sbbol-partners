package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
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
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PartnerRepository partnerRepository;

    public AccountUpdateValidatorImpl(AccountRepository accountRepository, AccountMapper accountMapper, PartnerRepository partnerRepository) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, AccountChange entity) {
        commonValidationUuid(entity.getPartnerId(), entity.getId());
        commonValidationDigitalId(entity.getDigitalId());
        validateUpdateAccount(entity, errors);
        if (StringUtils.isNotEmpty(entity.getComment()) && entity.getComment().length() > COMMENT_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale("default.fields.length", "comment", "1", "50"));
        }
        if (StringUtils.isNotEmpty(entity.getAccount()) && entity.getAccount().length() != ACCOUNT_VALID_LENGTH) {
            errors.add(MessagesTranslator.toLocale("account.account.length"));
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
            .orElseThrow(() -> new MissingValueException("Не найден объект " + DOCUMENT_NAME + " " + entity.getDigitalId() + " " + entity.getId()));
        if (!entity.getVersion().equals(foundAccount.getVersion())) {
            throw new OptimisticLockingFailureException("Версия записи в базе данных " + foundAccount.getVersion() +
                " не равна версии записи в запросе version=" + entity.getVersion());
        }
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getPartnerId()));
        if (foundPartner.isEmpty()) {
            throw new MissingValueException("Не найден объект partner " + entity.getDigitalId() + " " + entity.getPartnerId());
        }
        if (AccountStateType.SIGNED == foundAccount.getState()) {
            errors.add(MessagesTranslator.toLocale("Ошибка обновления счёта клиента " + entity.getAccount() + " id " + entity.getId() + " нельзя обновлять подписанные счёта"));
        }
        accountMapper.updateAccount(entity, foundAccount);
        if (foundAccount.getBank() == null || foundAccount.getBank().getBic() == null || foundAccount.getBank().getBankAccount() == null || foundAccount.getBank().getBankAccount().getAccount() == null) {
            errors.add(MessagesTranslator.toLocale("Нельзя обновить сущность account: " + foundAccount.getAccount() + " заполните bank.bic и bank.bankAccount"));
        }
        var matchBic = RKC_BIC.matcher(foundAccount.getBank().getBic());
        if (matchBic.matches()) {
            if (StringUtils.isNotEmpty(foundAccount.getAccount()) && !validateBankAccount(foundAccount.getAccount(), foundAccount.getBank().getBic())) {
                errors.add(MessagesTranslator.toLocale("account не проходит проверку контрольного числа: " + foundAccount.getAccount() + " + bic: " + foundAccount.getBank().getBic()));
            }
        } else if (StringUtils.isNotEmpty(foundAccount.getAccount()) && !validateUserAccount(foundAccount.getAccount(), foundAccount.getBank().getBic())) {
            errors.add(MessagesTranslator.toLocale("account не проходит проверку контрольного числа: " + foundAccount.getAccount() + " + bic: " + foundAccount.getBank().getBic()));
        }
        if (!BasePartnerAccountValidation.validateBankAccount(foundAccount.getBank().getBankAccount().getAccount(), foundAccount.getBank().getBic())) {
            errors.add(MessagesTranslator.toLocale("bankAccount не проходит проверку контрольного числа: " + foundAccount.getBank().getBankAccount().getAccount() + " + bic: " + foundAccount.getBank().getBic()));
        }
    }

    private void checkBank(AccountChange entity, List<String> errors) {
        var bank = entity.getBank();
        if (bank.getBic() != null && bank.getBic().equals(EMPTY)) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "bank.bic"));
        }
        if (StringUtils.isNotEmpty(bank.getBic()) && bank.getBic().length() != BIC_VALID_LENGTH) {
            errors.add("account.bic.length");
        }
        if (StringUtils.isNotEmpty(bank.getName()) && bank.getName().length() > BANK_NAME_MAX_LENGTH_VALIDATION){
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
            errors.add("account.account.length");
        }
    }
}

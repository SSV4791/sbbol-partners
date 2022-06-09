package ru.sberbank.pprb.sbbol.partners.validation;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;

import java.util.List;
import java.util.UUID;

public class AccountChangePriorityValidationImpl extends AbstractValidatorImpl<AccountPriority> {
    private final AccountRepository accountRepository;

    public AccountChangePriorityValidationImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, AccountPriority entity) {
        var digitalId = entity.getDigitalId();
        var accountId = entity.getId();
        if (entity.getPriorityAccount()) {
            var findAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(accountId))
                .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, digitalId, entity.getId())));
            var foundPriorityAccounts = accountRepository
                .findByDigitalIdAndPartnerUuidAndPriorityAccountIsTrue(digitalId, findAccount.getPartnerUuid());
            if (!CollectionUtils.isEmpty(foundPriorityAccounts)) {
                errors.add(MessagesTranslator.toLocale("У пользователя digitalId: " + digitalId + "партнера " + findAccount.getPartnerUuid() + " уже есть приоритетные счета"));
            }
        }
        commonValidationUuid(errors, entity.getId());
        commonValidationDigitalId(errors, entity.getDigitalId());
    }
}

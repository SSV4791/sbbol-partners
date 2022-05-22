package ru.sberbank.pprb.sbbol.partners.validation;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;

import java.util.List;
import java.util.UUID;

public class AccountChangePriorityValidationImpl extends AbstractValidatorImpl<AccountPriority>{
    private final AccountRepository accountRepository;

    public AccountChangePriorityValidationImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, AccountPriority entity) {
        commonValidationUuid(entity.getId());
        commonValidationDigitalId(entity.getDigitalId());
        var digitalId = entity.getDigitalId();
        if (entity.getPriorityAccount()) {
            var foundPriorityAccounts = accountRepository.findByDigitalIdAndPriorityAccountIsTrue(digitalId);
            if (!CollectionUtils.isEmpty(foundPriorityAccounts)) {
                errors.add(MessagesTranslator.toLocale("У пользователя digitalId: " + digitalId + "Уже есть приоритетные счета"));
            }
            var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(entity.getId()));
            if (foundAccount.isEmpty()) {
                throw new MissingValueException("Не найден объект account " + digitalId + " " + entity.getId());
            }
        }
    }
}

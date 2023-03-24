package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.audit.Audit;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.AccountAlreadySignedException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryDeleteException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.EntrySaveException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudServiceManager;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.SIGN_ACCOUNTS_CREATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.SIGN_ACCOUNTS_DELETE;

@Loggable
public class AccountSignServiceImpl implements AccountSignService {

    private static final String DOCUMENT_NAME = "account";

    private final AccountRepository accountRepository;
    private final AccountSignRepository accountSignRepository;
    private final FraudServiceManager fraudServiceManager;
    private final AccountSingMapper accountSingMapper;
    private final ReplicationService replicationService;

    public AccountSignServiceImpl(
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        FraudServiceManager fraudServiceManager,
        AccountSingMapper accountSingMapper,
        ReplicationService replicationService
    ) {
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
        this.fraudServiceManager = fraudServiceManager;
        this.accountSingMapper = accountSingMapper;
        this.replicationService = replicationService;
    }

    @Override
    @Transactional
    @Audit(eventType = SIGN_ACCOUNTS_CREATE)
    public AccountsSignInfoResponse createAccountsSign(AccountsSignInfo accountsSign, FraudMetaData fraudMetaData) {
        var response = new AccountsSignInfoResponse();
        var digitalId = accountsSign.getDigitalId();
        response.setDigitalId(digitalId);
        for (var accountSign : accountsSign.getAccountsSignDetail()) {
            var account = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(accountSign.getAccountId()))
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountSign.getAccountId()));
            if (account.getState() == AccountStateType.SIGNED) {
                throw new AccountAlreadySignedException(account.getAccount());
            }
            if (!Objects.equals(account.getVersion(), accountSign.getAccountVersion())) {
                throw new OptimisticLockException(account.getVersion(), accountSign.getAccountVersion());
            }
            var sign = accountSingMapper.toSing(accountSign, account.getPartnerUuid(), digitalId);
            fraudServiceManager
                .getService(FraudEventType.SIGN_ACCOUNT)
                .sendEvent(fraudMetaData, account);
            try {
                var savedSign = accountSignRepository.save(sign);
                replicationService.saveSign(digitalId, accountsSign.getDigitalUserId(), savedSign.getAccountUuid());
                response.addAccountsSignDetailItem(accountSingMapper.toSignAccount(savedSign));
                account.setState(AccountStateType.SIGNED);
                accountRepository.save(account);
            } catch (RuntimeException e) {
                throw new EntrySaveException(DOCUMENT_NAME, e);
            }
        }
        return response;
    }

    @Override
    @Transactional
    @Audit(eventType = SIGN_ACCOUNTS_DELETE)
    public void deleteAccountsSign(String digitalId, List<String> accountIds) {
        for (String accountId : accountIds) {
            var accountUuid = accountSingMapper.mapUuid(accountId);
            var sign = accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, accountUuid);
            // Сделанно в рамках поддержания миграции чтоб не создавать пустушку для мигрированных подписанных счетов
            if (sign.isPresent()) {
                SignEntity signEntity = sign.get();
                try {
                    accountSignRepository.delete(signEntity);
                } catch (RuntimeException e) {
                    throw new EntryDeleteException(DOCUMENT_NAME, signEntity.getEntityUuid(), e);
                }
            }
            var account = accountRepository.getByDigitalIdAndUuid(digitalId, accountUuid)
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountUuid));
            try {
                account.setState(AccountStateType.NOT_SIGNED);
                accountRepository.save(account);
                replicationService.deleteSign(digitalId, accountUuid);
            } catch (RuntimeException e) {
                throw new EntrySaveException(DOCUMENT_NAME, e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountSignInfo getAccountSign(String digitalId, String accountId) {
        var uuid = UUID.fromString(accountId);
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, uuid);
        if (foundAccount.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, uuid);
        }
        var sign = accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, uuid)
            .orElseThrow(() -> new EntryNotFoundException("sign", digitalId, uuid));
        return accountSingMapper.toSignAccount(sign, digitalId);
    }
}

package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.audit.Audit;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.AccountAlreadySignedException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryDeleteException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.EntrySaveException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfoRequisites;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfoRequisitesResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.SIGN_ACCOUNTS_CREATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.SIGN_ACCOUNTS_DELETE;

@Loggable
public class AccountSignServiceImpl implements AccountSignService {

    private static final String DOCUMENT_NAME = "account";

    private final AccountRepository accountRepository;
    private final AccountSignRepository accountSignRepository;
    private final AccountSingMapper accountSingMapper;
    private final ReplicationService replicationService;

    public AccountSignServiceImpl(
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        AccountSingMapper accountSingMapper,
        ReplicationService replicationService
    ) {
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
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
            var account = accountRepository.getByDigitalIdAndUuid(digitalId, accountSign.getAccountId())
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountSign.getAccountId()));
            if (account.getState() == AccountStateType.SIGNED) {
                throw new AccountAlreadySignedException(account.getAccount());
            }
            if (!Objects.equals(account.getVersion(), accountSign.getAccountVersion())) {
                throw new OptimisticLockException(account.getVersion(), accountSign.getAccountVersion());
            }
            var sign = accountSingMapper.toSing(accountSign, account.getPartnerUuid(), digitalId);
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
    public void deleteAccountsSign(String digitalId, List<UUID> accountIds) {
        for (var accountId : accountIds) {
            var sign = accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, accountId);
            // Сделанно в рамках поддержания миграции чтоб не создавать пустушку для мигрированных подписанных счетов
            if (sign.isPresent()) {
                SignEntity signEntity = sign.get();
                try {
                    accountSignRepository.delete(signEntity);
                } catch (RuntimeException e) {
                    throw new EntryDeleteException(DOCUMENT_NAME, signEntity.getEntityUuid(), e);
                }
            }
            var account = accountRepository.getByDigitalIdAndUuid(digitalId, accountId)
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountId));
            try {
                account.setState(AccountStateType.NOT_SIGNED);
                accountRepository.save(account);
                replicationService.deleteSign(digitalId, accountId);
            } catch (RuntimeException e) {
                throw new EntrySaveException(DOCUMENT_NAME, e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountSignInfo getAccountSign(String digitalId, UUID accountId) {
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountId);
        if (foundAccount.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountId);
        }
        var sign = accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, accountId)
            .orElseThrow(() -> new EntryNotFoundException("sign", digitalId, accountId));
        return accountSingMapper.toSignAccount(sign, digitalId);
    }

    @Override
    public AccountSignInfoRequisitesResponse getSignInfoByRequisites(AccountSignInfoRequisites accountSignInfoRequisites) {
        List<AccountEntity> foundAccounts = accountRepository.findByRequisites(accountSignInfoRequisites);
        return accountSingMapper.toAccountSignRequisitesResponse(getCorrectAccount(foundAccounts, accountSignInfoRequisites));
    }

    private AccountEntity getCorrectAccount(List<AccountEntity> accounts, AccountSignInfoRequisites accountSignInfoRequisites) {
        if (CollectionUtils.isEmpty(accounts)) {
            throw new EntryNotFoundException(DOCUMENT_NAME, accountSignInfoRequisites.getDigitalId());
        }
        List<AccountEntity> filteredAccounts = accounts.stream()
            .filter(acc -> Objects.equals(accountSignInfoRequisites.getAccount(), acc.getAccount()))
            .filter(acc -> Objects.equals(accountSignInfoRequisites.getBic(), acc.getBank().getBic()))
            .sorted((acc1, acc2) -> {
                if (AccountStateType.SIGNED.equals(acc1.getState())) {
                    return -1;
                } else if (AccountStateType.SIGNED.equals(acc2.getState())) {
                    return 1;
                } else {
                    return 0;
                }
            })
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filteredAccounts)) {
            throw new EntryNotFoundException(DOCUMENT_NAME, accountSignInfoRequisites.getDigitalId());
        }
        return filteredAccounts.get(0);
    }
}

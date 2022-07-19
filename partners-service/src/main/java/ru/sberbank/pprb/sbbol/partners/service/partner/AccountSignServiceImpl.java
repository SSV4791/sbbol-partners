package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.audit.model.Event;
import ru.sberbank.pprb.sbbol.partners.audit.model.EventType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.EntrySaveException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.List;
import java.util.UUID;

@Loggable
public class AccountSignServiceImpl implements AccountSignService {

    private static final String DOCUMENT_NAME = "account";

    private final AccountRepository accountRepository;
    private final AccountSignRepository accountSignRepository;
    private final ReplicationService replicationService;
    private final AuditAdapter auditAdapter;
    private final AccountSingMapper accountSingMapper;
    private final AccountMapper accountMapper;

    public AccountSignServiceImpl(
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        ReplicationService replicationService,
        AuditAdapter auditAdapter,
        AccountMapper accountMapper,
        AccountSingMapper accountSingMapper
    ) {
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
        this.replicationService = replicationService;
        this.auditAdapter = auditAdapter;
        this.accountMapper = accountMapper;
        this.accountSingMapper = accountSingMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountsSignResponse getAccountsSign(AccountsSignFilter filter) {
        var foundSignedAccounts = accountRepository.findByFilter(filter);
        var accountsSignResponse = new AccountsSignResponse();
        for (AccountEntity account : foundSignedAccounts) {
            accountsSignResponse.addAccountsSignItem(accountSingMapper.toSignAccount(account));
        }
        var pagination = filter.getPagination();
        accountsSignResponse.setPagination(
            new Pagination()
                .offset(pagination.getOffset())
                .count(pagination.getCount())
        );
        var size = foundSignedAccounts.size();
        if (pagination.getCount() < size) {
            accountsSignResponse.getPagination().hasNextPage(Boolean.TRUE);
            accountsSignResponse.getAccountsSign().remove(size - 1);
        }
        return accountsSignResponse;
    }

    @Override
    @Transactional
    public AccountsSignInfoResponse createAccountsSign(AccountsSignInfo accountsSign) {
        var response = new AccountsSignInfoResponse();
        response.setDigitalId(accountsSign.getDigitalId());
        for (var accountSign : accountsSign.getAccountsSignDetail()) {
            var account = accountRepository.getByDigitalIdAndUuid(accountsSign.getDigitalId(), UUID.fromString(accountSign.getAccountId()))
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, accountsSign.getDigitalId(), accountSign.getAccountId()));
            var sign = accountSingMapper.toSing(accountSign, account.getPartnerUuid());
            try {
                var savedSign = accountSignRepository.save(sign);
                replicationService.saveSign(accountsSign.getDigitalId(), sign);
                auditAdapter.send(new Event()
                    .eventType(EventType.SIGN_ACCOUNT_CREATE_SUCCESS)
                    .eventParams(accountSingMapper.toEventParams(savedSign))
                );
                response.addAccountsSignDetailItem(accountSingMapper.toSignAccount(savedSign));
            } catch (RuntimeException e) {
                auditAdapter.send(new Event()
                    .eventType(EventType.SIGN_ACCOUNT_CREATE_ERROR)
                    .eventParams(accountSingMapper.toEventParams(sign))
                );
                throw new EntrySaveException(DOCUMENT_NAME, e);
            }
            try {
                account.setState(AccountStateType.SIGNED);
                var saveAccount = accountRepository.save(account);
                auditAdapter.send(new Event()
                    .eventType(EventType.ACCOUNT_UPDATE_SUCCESS)
                    .eventParams(accountMapper.toEventParams(saveAccount))
                );
            } catch (RuntimeException e) {
                auditAdapter.send(new Event()
                    .eventType(EventType.ACCOUNT_UPDATE_ERROR)
                    .eventParams(accountMapper.toEventParams(account))
                );
                throw new EntrySaveException(DOCUMENT_NAME, e);
            }
        }
        return response;
    }

    @Override
    @Transactional
    public void deleteAccountsSign(String digitalId, List<String> accountIds) {
        for (String accountId : accountIds) {
            var sign = accountSignRepository.getByAccountUuid(UUID.fromString(accountId))
                .orElseThrow(() -> new EntryNotFoundException("sign", digitalId, accountId));
            try {
                accountSignRepository.delete(sign);
                replicationService.deleteSign(digitalId, accountId);
                auditAdapter.send(new Event()
                    .eventType(EventType.SIGN_ACCOUNT_CREATE_SUCCESS)
                    .eventParams(accountSingMapper.toEventParams(sign))
                );
            } catch (RuntimeException e) {
                auditAdapter.send(new Event()
                    .eventType(EventType.SIGN_ACCOUNT_DELETE_ERROR)
                    .eventParams(accountSingMapper.toEventParams(sign))
                );
                throw new EntrySaveException(DOCUMENT_NAME, e);
            }
            var account = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(accountId))
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountId));
            try {
                account.setState(AccountStateType.NOT_SIGNED);
                var saveAccount = accountRepository.save(account);
                auditAdapter.send(new Event()
                    .eventType(EventType.ACCOUNT_UPDATE_SUCCESS)
                    .eventParams(accountMapper.toEventParams(saveAccount))
                );
            } catch (RuntimeException e) {
                auditAdapter.send(new Event()
                    .eventType(EventType.ACCOUNT_UPDATE_ERROR)
                    .eventParams(accountMapper.toEventParams(account))
                );
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
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountId);
        }
        var sign = accountSignRepository.getByAccountUuid(uuid)
            .orElseThrow(() -> new EntryNotFoundException("sign", digitalId, accountId));
        return accountSingMapper.toSignAccount(sign, digitalId);
    }
}

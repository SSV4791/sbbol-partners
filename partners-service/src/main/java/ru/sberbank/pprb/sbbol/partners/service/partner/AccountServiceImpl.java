package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.audit.model.Event;
import ru.sberbank.pprb.sbbol.partners.audit.model.EventType;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.EntrySaveException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.UUID;

@Loggable
public class AccountServiceImpl implements AccountService {

    public static final String DOCUMENT_NAME = "account";

    private final AccountRepository accountRepository;
    private final ReplicationService replicationService;
    private final BudgetMaskService budgetMaskService;
    private final AuditAdapter auditAdapter;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(
        AccountRepository accountRepository,
        ReplicationService replicationService,
        BudgetMaskService budgetMaskService,
        AuditAdapter auditAdapter,
        AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.replicationService = replicationService;
        this.budgetMaskService = budgetMaskService;
        this.auditAdapter = auditAdapter;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(String digitalId, String id) {
        var account = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        var response = accountMapper.toAccount(account, budgetMaskService);
        return new AccountResponse().account(response);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountsResponse getAccounts(AccountsFilter accountsFilter) {
        var accountsResponse = new AccountsResponse();
        var response = accountRepository.findByFilter(accountsFilter);
        for (var entity : response) {
            var item = accountMapper.toAccount(entity, budgetMaskService);
            accountsResponse.addAccountsItem(item);
        }
        var pagination = accountsFilter.getPagination();
        accountsResponse.setPagination(
            new Pagination()
                .offset(pagination.getOffset())
                .count(pagination.getCount())
        );
        var size = response.size();
        if (pagination.getCount() < size) {
            accountsResponse.getPagination().hasNextPage(Boolean.TRUE);
            accountsResponse.getAccounts().remove(size - 1);
        }
        return accountsResponse;
    }

    @Override
    @Transactional
    public AccountResponse saveAccount(AccountCreate account) {
        var requestAccount = accountMapper.toAccount(account);
        try {
            var savedAccount = accountRepository.save(requestAccount);
            auditAdapter.send(new Event()
                .eventType(EventType.ACCOUNT_CREATE_SUCCESS)
                .eventParams(accountMapper.toEventParams(savedAccount))
            );
            var response = accountMapper.toAccount(savedAccount, budgetMaskService);
            replicationService.saveCounterparty(response);
            return new AccountResponse().account(response);
        } catch (RuntimeException e) {
            auditAdapter.send(new Event()
                .eventType(EventType.ACCOUNT_CREATE_ERROR)
                .eventParams(accountMapper.toEventParams(requestAccount))
            );
            throw new EntrySaveException(DOCUMENT_NAME, e);
        }
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(AccountChange account) {
        var foundAccount = accountRepository.getByDigitalIdAndUuid(account.getDigitalId(), UUID.fromString(account.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, account.getDigitalId(), account.getId()));
        accountMapper.updateAccount(account, foundAccount);
        try {
            var savedAccount = accountRepository.save(foundAccount);
            auditAdapter.send(new Event()
                .eventType(EventType.ACCOUNT_UPDATE_SUCCESS)
                .eventParams(accountMapper.toEventParams(foundAccount))
            );
            var response = accountMapper.toAccount(savedAccount, budgetMaskService);
            replicationService.saveCounterparty(response);
            return new AccountResponse().account(response);
        } catch (RuntimeException e) {
            auditAdapter.send(new Event()
                .eventType(EventType.ACCOUNT_UPDATE_ERROR)
                .eventParams(accountMapper.toEventParams(foundAccount))
            );
            throw new EntrySaveException(DOCUMENT_NAME, e);
        }
    }

    @Override
    @Transactional
    public void deleteAccount(String digitalId, String id) {
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        try {
            accountRepository.delete(foundAccount);
            auditAdapter.send(new Event()
                .eventType(EventType.ACCOUNT_DELETE_SUCCESS)
                .eventParams(accountMapper.toEventParams(foundAccount))
            );
            replicationService.deleteCounterparty(foundAccount);
        } catch (RuntimeException e) {
            auditAdapter.send(new Event()
                .eventType(EventType.ACCOUNT_DELETE_ERROR)
                .eventParams(accountMapper.toEventParams(foundAccount))
            );
            throw new EntrySaveException(DOCUMENT_NAME, e);
        }
    }

    @Override
    @Transactional
    public AccountResponse changePriority(AccountPriority accountPriority) {
        var digitalId = accountPriority.getDigitalId();
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(accountPriority.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountPriority.getId()));
        foundAccount.setPriorityAccount(accountPriority.getPriorityAccount());
        var savedAccount = accountRepository.save(foundAccount);
        var account = accountMapper.toAccount(savedAccount, budgetMaskService);
        return new AccountResponse().account(account);
    }
}

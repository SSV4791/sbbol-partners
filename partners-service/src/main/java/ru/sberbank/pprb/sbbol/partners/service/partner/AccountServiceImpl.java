package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.audit.model.Event;
import ru.sberbank.pprb.sbbol.partners.audit.model.EventType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.AccountAlreadySignedException;
import ru.sberbank.pprb.sbbol.partners.exception.AccountPriorityOneMoreException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.EntrySaveException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Loggable
public class AccountServiceImpl implements AccountService {

    public static final String DOCUMENT_NAME = "account";

    private final AccountRepository accountRepository;
    private final AccountSignRepository accountSignRepository;
    private final ReplicationService replicationService;
    private final BudgetMaskService budgetMaskService;
    private final AuditAdapter auditAdapter;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        ReplicationService replicationService,
        BudgetMaskService budgetMaskService,
        AuditAdapter auditAdapter,
        AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
        this.replicationService = replicationService;
        this.budgetMaskService = budgetMaskService;
        this.auditAdapter = auditAdapter;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccount(String digitalId, String id) {
        var account = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        return accountMapper.toAccount(account, budgetMaskService);
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
    public Account saveAccount(AccountCreate account) {
        var accountEntity = accountMapper.toAccount(account);
        try {
            var savedAccount = accountRepository.save(accountEntity);
            auditAdapter.send(new Event()
                .eventType(EventType.ACCOUNT_CREATE_SUCCESS)
                .eventParams(accountMapper.toEventParams(savedAccount))
            );
            var response = accountMapper.toAccount(savedAccount, budgetMaskService);
            replicationService.saveCounterparty(response);
            return response;
        } catch (DataIntegrityViolationException e) {
            throw e;
        } catch (RuntimeException e) {
            auditAdapter.send(new Event()
                .eventType(EventType.ACCOUNT_CREATE_ERROR)
                .eventParams(accountMapper.toEventParams(accountEntity))
            );
            throw new EntrySaveException(DOCUMENT_NAME, e);
        }
    }

    @Override
    @Transactional
    public Account updateAccount(AccountChange account) {
        var foundAccount = accountRepository.getByDigitalIdAndUuid(account.getDigitalId(), UUID.fromString(account.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, account.getDigitalId(), account.getId()));
        if (!Objects.equals(account.getVersion(), foundAccount.getVersion())) {
            throw new OptimisticLockException(foundAccount.getVersion(), account.getVersion());
        }
        accountMapper.updateAccount(account, foundAccount);
        if (AccountStateType.SIGNED == foundAccount.getState()) {
            throw new AccountAlreadySignedException(account.getAccount());
        }
        try {
            var savedAccount = accountRepository.save(foundAccount);
            auditAdapter.send(new Event()
                .eventType(EventType.ACCOUNT_UPDATE_SUCCESS)
                .eventParams(accountMapper.toEventParams(foundAccount))
            );
            var response = accountMapper.toAccount(savedAccount, budgetMaskService);
            response.setVersion(response.getVersion() + 1);
            replicationService.saveCounterparty(response);
            return response;
        } catch (DataIntegrityViolationException e) {
            throw e;
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
    public void deleteAccounts(String digitalId, List<String> ids) {
        for (String id : ids) {
            var uuid = accountMapper.mapUuid(id);
            var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, uuid)
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, uuid));
            try {
                accountRepository.delete(foundAccount);
                auditAdapter.send(new Event()
                    .eventType(EventType.ACCOUNT_DELETE_SUCCESS)
                    .eventParams(accountMapper.toEventParams(foundAccount))
                );
                var accountSignEntity =
                    accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, foundAccount.getUuid());
                accountSignEntity.ifPresent(accountSignRepository::delete);
                replicationService.deleteCounterparty(foundAccount);
            } catch (RuntimeException e) {
                auditAdapter.send(new Event()
                    .eventType(EventType.ACCOUNT_DELETE_ERROR)
                    .eventParams(accountMapper.toEventParams(foundAccount))
                );
                throw new EntrySaveException(DOCUMENT_NAME, e);
            }
        }
    }

    @Override
    @Transactional
    public Account changePriority(AccountPriority accountPriority) {
        var digitalId = accountPriority.getDigitalId();
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(accountPriority.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountPriority.getId()));
        var foundPriorityAccounts = accountRepository
            .findByDigitalIdAndPartnerUuidAndPriorityAccountIsTrue(digitalId, foundAccount.getPartnerUuid());
        if (!CollectionUtils.isEmpty(foundPriorityAccounts)) {
            throw new AccountPriorityOneMoreException(foundAccount.getDigitalId(), foundAccount.getUuid());
        }
        foundAccount.setPriorityAccount(accountPriority.getPriorityAccount());
        var savedAccount = accountRepository.save(foundAccount);
        return accountMapper.toAccount(savedAccount, budgetMaskService);
    }
}

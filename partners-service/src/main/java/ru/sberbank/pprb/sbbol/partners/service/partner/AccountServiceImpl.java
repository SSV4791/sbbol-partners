package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.audit.Audit;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.exception.AccountAlreadySignedException;
import ru.sberbank.pprb.sbbol.partners.exception.AccountPriorityOneMoreException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.EntrySaveException;
import ru.sberbank.pprb.sbbol.partners.exception.MultipleEntryFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountAndPartnerRequest;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.ACCOUNTS_DELETE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.ACCOUNT_CREATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.ACCOUNT_UPDATE;

@Loggable
public class AccountServiceImpl implements AccountService {

    public static final String DOCUMENT_NAME = "account";

    private final AccountRepository accountRepository;
    private final PartnerRepository partnerRepository;
    private final AccountSignRepository accountSignRepository;
    private final BudgetMaskService budgetMaskService;
    private final AccountMapper accountMapper;
    private final ReplicationService replicationService;

    public AccountServiceImpl(
        AccountRepository accountRepository,
        PartnerRepository partnerRepository,
        AccountSignRepository accountSignRepository,
        BudgetMaskService budgetMaskService,
        AccountMapper accountMapper,
        ReplicationService replicationService
    ) {
        this.accountRepository = accountRepository;
        this.partnerRepository = partnerRepository;
        this.accountSignRepository = accountSignRepository;
        this.budgetMaskService = budgetMaskService;
        this.accountMapper = accountMapper;
        this.replicationService = replicationService;
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
    @Audit(eventType = ACCOUNT_CREATE)
    public Account saveAccount(AccountCreate account) {
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(account.getDigitalId(), UUID.fromString(account.getPartnerId()));
        if (foundPartner.isEmpty()) {
            throw new EntryNotFoundException("partner", account.getDigitalId(), account.getPartnerId());
        }
        var accountEntity = accountMapper.toAccount(account);
        try {
            var savedAccount = accountRepository.save(accountEntity);
            var response = accountMapper.toAccount(savedAccount, budgetMaskService);
            replicationService.createCounterparty(response);
            return response;
        } catch (DataIntegrityViolationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new EntrySaveException(DOCUMENT_NAME, e);
        }
    }

    @Override
    @Transactional
    @Audit(eventType = ACCOUNT_UPDATE)
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
            var response = accountMapper.toAccount(savedAccount, budgetMaskService);
            response.setVersion(response.getVersion() + 1);
            replicationService.updateCounterparty(response);
            return response;
        } catch (DataIntegrityViolationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new EntrySaveException(DOCUMENT_NAME, e);
        }
    }

    @Override
    @Transactional
    @Audit(eventType = ACCOUNTS_DELETE)
    public void deleteAccounts(String digitalId, List<String> ids) {
        for (String id : ids) {
            var uuid = accountMapper.mapUuid(id);
            var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, uuid)
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, uuid));
            try {
                accountRepository.delete(foundAccount);
                var accountSignEntity =
                    accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, foundAccount.getUuid());
                accountSignEntity.ifPresent(accountSignRepository::delete);
                replicationService.deleteCounterparty(digitalId, uuid.toString());
            } catch (RuntimeException e) {
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

    @Override
    @Transactional(readOnly = true)
    public List<AccountWithPartnerResponse> getAtRequisites(AccountAndPartnerRequest request) {
        List<AccountEntity> accounts = accountRepository.findByRequest(request);
        if (CollectionUtils.isEmpty(accounts)) {
            var search =
                accountMapper.prepareSearchString(request.getInn(), request.getKpp(), request.getName());
            PartnerEntity partner = partnerRepository.findByDigitalIdAndSearchContainsAndType(request.getDigitalId(), search, PartnerType.PARTNER);
            if (Objects.isNull(partner)) {
                throw new EntryNotFoundException(DOCUMENT_NAME, request.getDigitalId());
            }
            return accountMapper.toAccountsWithPartner(partner);
        }
        if (accounts.size() == 1) {
            AccountEntity account = accounts.get(0);
            if (account.getAccount() != null || account.getPartner().getInn() != null) {
                return accountMapper.toAccountsWithPartner(accounts);
            }
        }
        List<AccountEntity> accountsWithKppField = accounts;
        if (Objects.nonNull(request.getKpp()) && !Objects.equals(request.getKpp(), "0")) {
            accountsWithKppField = accounts.stream()
                .filter(value -> Objects.equals(value.getPartner().getKpp(), request.getKpp()))
                .collect(Collectors.toList());
            if (accountsWithKppField.size() == 1) {
                return accountMapper.toAccountsWithPartner(accountsWithKppField);
            }
        }

        List<AccountEntity> accountsWithPartnerNameField = accountsWithKppField.stream()
            .filter(value -> {
                PartnerEntity partner = value.getPartner();
                String fio =
                    accountMapper.prepareSearchString(partner.getSecondName(), partner.getFirstName(), partner.getMiddleName());
                return Objects.equals(partner.getOrgName(), request.getName()) ||
                    Objects.equals(fio, request.getName());
            })
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(accountsWithPartnerNameField)) {
            throw new EntryNotFoundException(DOCUMENT_NAME, request.getDigitalId());
        }
        return accountMapper.toAccountsWithPartner(accountsWithPartnerNameField);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountWithPartnerResponse getAtAllRequisites(AccountAndPartnerRequest request) {
        List<AccountEntity> accounts = accountRepository.findByAllRequestAttributes(request);
        if (CollectionUtils.isEmpty(accounts)) {
            throw new EntryNotFoundException(DOCUMENT_NAME, request.getDigitalId());
        }
        if (accounts.size() > 1) {
            throw new MultipleEntryFoundException(DOCUMENT_NAME, request.getDigitalId());
        }
        return accountMapper.toAccountWithPartner(accounts.get(0));
    }
}

package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.audit.Audit;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.AccountAlreadySignedException;
import ru.sberbank.pprb.sbbol.partners.exception.AccountPriorityOneMoreException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.MultipleEntryFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountAndPartnerRequest;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfoRequisites;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.SignType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.ACCOUNTS_DELETE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.ACCOUNT_CREATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.ACCOUNT_UPDATE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.prepareSearchString;

@Loggable
public class AccountServiceImpl implements AccountService {

    public static final String DOCUMENT_NAME = "account";

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountSignService accountSignService;
    private final PartnerService partnerService;
    private final ReplicationService replicationService;
    private final IdsHistoryService idsHistoryService;

    public AccountServiceImpl(
        AccountRepository accountRepository,
        AccountMapper accountMapper,
        AccountSignService accountSignService,
        PartnerService partnerService,
        ReplicationService replicationService,
        IdsHistoryService idsHistoryService
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.accountSignService = accountSignService;
        this.partnerService = partnerService;
        this.replicationService = replicationService;
        this.idsHistoryService = idsHistoryService;
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccount(String digitalId, UUID id) {
        var account = accountRepository.getByDigitalIdAndUuid(digitalId, id)
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        return accountMapper.toAccount(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAccountsByPartnerId(String digitalId, UUID partnerId) {
        return accountRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerId).stream()
            .map(accountMapper::toAccount)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountsResponse getAccounts(AccountsFilter accountsFilter) {
        var accountsResponse = new AccountsResponse();
        var response = accountRepository.findByFilter(accountsFilter);
        for (var entity : response) {
            var item = accountMapper.toAccount(entity);
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
        var digitalId = account.getDigitalId();
        partnerService.existsPartner(digitalId, account.getPartnerId());
        var accountEntity = accountMapper.toAccount(account);
        var savedAccount = accountRepository.save(accountEntity);
        var history = idsHistoryService.saveAccountIdLink(account.getExternalId(), savedAccount.getUuid(), digitalId);
        var response = accountMapper.toAccount(savedAccount, history.getExternalId());
        replicationService.createCounterparty(response);
        return response;
    }

    @Override
    @Transactional
    public List<Account> saveAccounts(Set<AccountCreateFullModel> accounts, String digitalId, UUID partnerUuid) {
        if (CollectionUtils.isEmpty(accounts)) {
            return Collections.emptyList();
        }
        var response = new ArrayList<Account>(accounts.size());
        for (var account : accounts) {
            var saveAccount = accountMapper.toAccount(account, digitalId, partnerUuid);
            var savedAccount = accountRepository.save(saveAccount);
            idsHistoryService.saveAccountIdLink(account.getExternalId(), savedAccount.getUuid(), digitalId);
            response.add(accountMapper.toAccount(savedAccount));
        }
        return response;
    }

    @Override
    @Transactional
    @Audit(eventType = ACCOUNT_UPDATE)
    public Account updateAccount(AccountChange account) {
        var foundAccount = findAccountEntity(account.getDigitalId(), account.getId(), account.getVersion());
        accountMapper.updateAccount(account, foundAccount);
        return saveAccount(foundAccount);
    }

    @Override
    @Transactional
    @Audit(eventType = ACCOUNT_UPDATE)
    public Account patchAccount(AccountChange account) {
        var foundAccount = findAccountEntity(account.getDigitalId(), account.getId(), account.getVersion());
        accountMapper.patchAccount(account, foundAccount);
        return saveAccount(foundAccount);
    }

    @Override
    @Transactional
    @Audit(eventType = ACCOUNTS_DELETE)
    public void deleteAccounts(String digitalId, List<UUID> ids) {
        for (var accountId : ids) {
            var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountId)
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountId));
            accountSignService.deleteAccountsSign(digitalId, List.of(foundAccount.getUuid()));
            accountRepository.delete(foundAccount);
            replicationService.deleteCounterparty(digitalId, foundAccount.getUuid().toString());
        }
    }

    @Override
    @Transactional
    public Account changePriority(AccountPriority accountPriority) {
        var digitalId = accountPriority.getDigitalId();
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountPriority.getId())
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountPriority.getId()));
        var foundPriorityAccounts = accountRepository
            .findByDigitalIdAndPartnerUuidAndPriorityAccountIsTrue(digitalId, foundAccount.getPartnerUuid());
        if (!CollectionUtils.isEmpty(foundPriorityAccounts)) {
            throw new AccountPriorityOneMoreException(foundAccount.getDigitalId(), foundAccount.getUuid());
        }
        foundAccount.setPriorityAccount(accountPriority.getPriorityAccount());
        var savedAccount = accountRepository.save(foundAccount);
        return accountMapper.toAccount(savedAccount);
    }

    @Override
    @Transactional
    public Account changeSignType(String digitalId, UUID accountId, SignType signType) {
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountId)
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountId));
        foundAccount.setState(accountMapper.toAccountStateType(signType));
        var savedAccount = accountRepository.save(foundAccount);
        return accountMapper.toAccount(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountWithPartnerResponse> getAtRequisites(AccountAndPartnerRequest request) {
        List<AccountEntity> accounts = accountRepository.findByRequestAttributes(request);
        if (CollectionUtils.isEmpty(accounts)) {
            var foundPartner = partnerService.findPartner(request.getDigitalId(), request.getName(), request.getInn(), request.getKpp());
            return accountMapper.toAccountsWithPartner(foundPartner);
        }
        accounts = findAccountByPartnerName(request, accounts);
        if (accounts.size() == 1) {
            AccountEntity account = accounts.get(0);
            if (account.getAccount() != null || account.getPartner().getInn() != null) {
                return accountMapper.toAccountsWithPartner(accounts);
            }
        }
        List<AccountEntity> accountsWithKppField = new ArrayList<>(accounts.size());
        if (nonNull(request.getKpp()) && !Objects.equals(request.getKpp(), "0")) {
            accountsWithKppField = accounts.stream()
                .filter(value -> Objects.equals(value.getPartner().getKpp(), request.getKpp()))
                .toList();
            if (accountsWithKppField.size() == 1) {
                return accountMapper.toAccountsWithPartner(accountsWithKppField);
            }
        }
        List<AccountEntity> accountsWithPartnerNameField = findAccountByPartnerName(request, accountsWithKppField);
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
            var foundPartner = partnerService.findPartner(request.getDigitalId(), request.getName(), request.getInn(), request.getKpp());
            return accountMapper.toAccountWithPartner(foundPartner);
        }
        List<AccountEntity> foundAccounts = findAccountByPartnerName(request, accounts);
        if (foundAccounts.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, request.getDigitalId());
        } else if (foundAccounts.size() > 1) {
            throw new MultipleEntryFoundException(DOCUMENT_NAME, request.getDigitalId());
        }
        return accountMapper.toAccountWithPartner(foundAccounts.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getByRequisites(AccountSignInfoRequisites requisites) {
        return accountRepository.findByRequisites(requisites).stream()
            .map(accountMapper::toAccount)
            .toList();
    }

    @NotNull
    private List<AccountEntity> findAccountByPartnerName(AccountAndPartnerRequest request, List<AccountEntity> accounts) {
        return accounts.stream()
            .filter(value -> {
                PartnerEntity partner = value.getPartner();
                String fio =
                    prepareSearchString(partner.getSecondName(), partner.getFirstName(), partner.getMiddleName());
                var nameLowerCase = request.getName() == null ? request.getName() : request.getName().toLowerCase(Locale.getDefault());
                var orgNameLowerCase = partner.getOrgName() == null ? partner.getOrgName() : partner.getOrgName().toLowerCase(Locale.getDefault());
                var fioLowerCase = fio.toLowerCase(Locale.getDefault());
                return Objects.equals(orgNameLowerCase, nameLowerCase)
                    || Objects.equals(fioLowerCase, nameLowerCase);
            })
            .toList();
    }

    @Override
    @Transactional
    public void saveOrPatchAccounts(String digitalId, UUID partnerId, Set<AccountChangeFullModel> accounts) {
        Optional.ofNullable(accounts)
            .ifPresent(addressList ->
                addressList.forEach(accountChangeFullModel -> saveOrPatchAccount(digitalId, partnerId, accountChangeFullModel)));
    }

    @Override
    @Transactional
    public void saveOrPatchAccount(String digitalId, UUID partnerId, AccountChangeFullModel accountChangeFullModel) {
        if (Objects.nonNull(accountChangeFullModel.getId())) {
            var account = accountMapper.toAccount(accountChangeFullModel, digitalId, partnerId);
            patchAccount(account);
        } else {
            var accountCreate = accountMapper.toAccountCreate(accountChangeFullModel, digitalId, partnerId);
            saveAccount(accountCreate);
        }
    }

    private AccountEntity findAccountEntity(String digitalId, UUID accountId, Long version) {
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountId)
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountId));
        if (!Objects.equals(version, foundAccount.getVersion())) {
            throw new OptimisticLockException(foundAccount.getVersion(), version);
        }
        return foundAccount;
    }

    private Account saveAccount(AccountEntity accountEntity) {
        if (AccountStateType.SIGNED == accountEntity.getState()) {
            throw new AccountAlreadySignedException(accountEntity.getAccount());
        }
        var savedAccount = accountRepository.save(accountEntity);
        var response = accountMapper.toAccount(savedAccount);
        response.setVersion(response.getVersion() + 1);
        replicationService.updateCounterparty(response);
        return response;
    }
}

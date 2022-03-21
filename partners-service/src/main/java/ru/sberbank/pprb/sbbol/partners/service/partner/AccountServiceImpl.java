package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ReplicationHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.exception.BadRequestException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.ModelValidationException;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.Counterparty;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyFilter;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyView;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.ListResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ReplicationHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationHistoryService;
import ru.sberbank.pprb.sbbol.partners.service.utils.PartnerUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Logged(printRequestResponse = true)
public class AccountServiceImpl implements AccountService {

    public static final String DOCUMENT_NAME = "account";

    private final PartnerRepository partnerRepository;
    private final AccountRepository accountRepository;
    private final ReplicationHistoryRepository replicationHistoryRepository;
    private final ReplicationHistoryService replicationHistoryService;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final BudgetMaskService budgetMaskService;
    private final PartnerUtils partnerUtils;
    private final AccountMapper accountMapper;
    private final CounterpartyMapper counterpartyMapper;

    public AccountServiceImpl(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        ReplicationHistoryRepository replicationHistoryRepository,
        ReplicationHistoryService replicationHistoryService,
        LegacySbbolAdapter legacySbbolAdapter,
        BudgetMaskService budgetMaskService,
        PartnerUtils partnerUtils,
        AccountMapper accountMapper,
        CounterpartyMapper counterpartyMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.replicationHistoryRepository = replicationHistoryRepository;
        this.replicationHistoryService = replicationHistoryService;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.budgetMaskService = budgetMaskService;
        this.partnerUtils = partnerUtils;
        this.accountMapper = accountMapper;
        this.counterpartyMapper = counterpartyMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(String digitalId, String id) {
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            var account = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
            if (account == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            var response = accountMapper.toAccount(account, budgetMaskService);
            return new AccountResponse().account(response);
        } else {
            List<ReplicationHistoryEntity> replicationHistoryEntityList = replicationHistoryRepository.findByAccountUuid(UUID.fromString(id));
            if (CollectionUtils.isEmpty(replicationHistoryEntityList)) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            List<ReplicationHistoryEntity> replicationHistoryEntityListWithSbbolGuids = replicationHistoryEntityList.stream()
                .filter(r -> r.getSbbolGuid() != null)
                .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(replicationHistoryEntityListWithSbbolGuids) || replicationHistoryEntityListWithSbbolGuids.size() != 1) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            Counterparty counterparty = legacySbbolAdapter.getByPprbGuid(digitalId, replicationHistoryEntityListWithSbbolGuids.get(0).getSbbolGuid());
            Account account = counterpartyMapper.toAccount(counterparty, digitalId, replicationHistoryEntityListWithSbbolGuids.get(0).getAccountUuid(), budgetMaskService);
            return new AccountResponse().account(account);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountsResponse getAccounts(AccountsFilter accountsFilter) {
        var accountsResponse = new AccountsResponse();
        if (legacySbbolAdapter.checkMigration(accountsFilter.getDigitalId())) {
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
        } else {
            List<Account> counterpartyAccounts;
            Integer offset;
            if (accountsFilter.getPagination() != null) {
                CounterpartyFilter counterpartyFilter = counterpartyMapper.toCounterpartyFilter(accountsFilter);
                ListResponse<CounterpartyView> viewResponse = legacySbbolAdapter.viewRequest(accountsFilter.getDigitalId(), counterpartyFilter);
                counterpartyAccounts = counterpartyMapper.toAccounts(viewResponse.getItems(), accountsFilter.getDigitalId(), budgetMaskService);
                offset = accountsFilter.getPagination().getOffset();
            } else {
                List<CounterpartyView> viewResponse = legacySbbolAdapter.list(accountsFilter.getDigitalId());
                counterpartyAccounts = counterpartyMapper.toAccounts(viewResponse, accountsFilter.getDigitalId(), budgetMaskService);
                offset = 0;
            }
            List<String> partnerIds = accountsFilter.getPartnerIds();
            if (!CollectionUtils.isEmpty(counterpartyAccounts) && !CollectionUtils.isEmpty(partnerIds)) {
                counterpartyAccounts = counterpartyAccounts.stream().filter(a -> partnerIds.contains(a.getPartnerId())).collect(Collectors.toList());
            }
            accountsResponse.setPagination(
                new Pagination()
                    .offset(offset)
                    .count(counterpartyAccounts.size())
            );
            accountsResponse.setAccounts(counterpartyAccounts);
        }
        return accountsResponse;
    }

    @Override
    @Transactional
    public AccountResponse saveAccount(AccountCreate account) {
        if (legacySbbolAdapter.checkMigration(account.getDigitalId())) {
            var partner = partnerRepository.getByDigitalIdAndUuid(account.getDigitalId(), UUID.fromString(account.getPartnerId()));
            if (partner == null) {
                throw new EntryNotFoundException("partner", account.getDigitalId(), account.getPartnerId());
            }
            var requestAccount = accountMapper.toAccount(account);
            var savedAccount = accountRepository.save(requestAccount);
            replicationHistoryService.saveCounterparty(partner, account, savedAccount);
            var response = accountMapper.toAccount(savedAccount, budgetMaskService);
            return new AccountResponse().account(response);
        } else {
            if (CollectionUtils.isEmpty(account.getBanks())) {
                throw new ModelValidationException(
                    Collections.singletonList("Сохранение контрагента в СББОЛ не возможно, поле банк обязательно для заполнения")
                );
            }
            Counterparty sbbolCounterparty = legacySbbolAdapter.getByPprbGuid(account.getDigitalId(), account.getPartnerId());
            if (sbbolCounterparty == null) {
                throw new EntryNotFoundException("sbbol-counterparty", account.getDigitalId(), account.getPartnerId());
            }
            sbbolCounterparty.setPprbGuid(null);
            Counterparty sbbolUpdatedCounterparty = partnerUtils.createOrUpdateCounterparty(sbbolCounterparty, account);
            UUID accountUuid = replicationHistoryService.saveAccount(account, sbbolUpdatedCounterparty);
            var response = counterpartyMapper.toAccount(sbbolUpdatedCounterparty, account.getDigitalId(), accountUuid, budgetMaskService);
            return new AccountResponse().account(response);
        }
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(AccountChange account) {
        if (legacySbbolAdapter.checkMigration(account.getDigitalId())) {
            var saveAccount = partnerUtils.updatePartnerAccount(account);
            var mappedAccount = accountMapper.toAccount(saveAccount, budgetMaskService);
            replicationHistoryService.updateCounterparty(account);
            return new AccountResponse().account(mappedAccount);
        } else {
            Counterparty sbbolCounterparty = legacySbbolAdapter.getByPprbGuid(account.getDigitalId(), account.getPartnerId());
            Counterparty sbbolUpdatedCounterparty = partnerUtils.createOrUpdateCounterparty(sbbolCounterparty, account);
            UUID accountUuid = replicationHistoryService.updateAccount(account, sbbolUpdatedCounterparty);
            return new AccountResponse()
                .account(counterpartyMapper.toAccount(sbbolUpdatedCounterparty, account.getDigitalId(), accountUuid, budgetMaskService));
        }
    }

    @Override
    @Transactional
    public void deleteAccount(String digitalId, String id) {
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
            if (foundAccount == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            accountRepository.delete(foundAccount);
            partnerUtils.deleteCounterpartyAndReplicationHistory(digitalId, id, false);
        } else {
            partnerUtils.deleteCounterpartyAndReplicationHistory(digitalId, id, true);
            replicationHistoryService.deleteAccount(digitalId, id);
        }
    }

    @Override
    @Transactional
    public AccountResponse changePriority(AccountPriority accountPriority) {
        var digitalId = accountPriority.getDigitalId();
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(accountPriority.getId()));
        if (foundAccount == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountPriority.getId());
        }
        if (accountPriority.getPriorityAccount()) {
            var foundAccounts = accountRepository.findByDigitalIdAndPriorityAccountIsTrue(digitalId);
            if (!CollectionUtils.isEmpty(foundAccounts)) {
                throw new BadRequestException("У пользователя digitalId: " + digitalId + "Уже есть приоритетные счета");
            }
        }
        foundAccount.setPriorityAccount(accountPriority.getPriorityAccount());
        var savedAccount = accountRepository.save(foundAccount);
        var account = accountMapper.toAccount(savedAccount, budgetMaskService);
        return new AccountResponse().account(account);
    }
}

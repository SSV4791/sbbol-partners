package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.BadRequestException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.exception.SignAccountException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.Collections;
import java.util.UUID;

@Logged(printRequestResponse = true)
public class AccountServiceImpl implements AccountService {

    public static final String DOCUMENT_NAME = "account";

    private final PartnerRepository partnerRepository;
    private final AccountRepository accountRepository;
    private final ReplicationService replicationService;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final BudgetMaskService budgetMaskService;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        ReplicationService replicationService,
        LegacySbbolAdapter legacySbbolAdapter,
        BudgetMaskService budgetMaskService,
        AccountMapper accountMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.replicationService = replicationService;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.budgetMaskService = budgetMaskService;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(String digitalId, String id) {
        if (legacySbbolAdapter.checkNotMigration(digitalId)) {
            throw new PartnerMigrationException();
        }
        var account = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        var response = accountMapper.toAccount(account, budgetMaskService);
        return new AccountResponse().account(response);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountsResponse getAccounts(AccountsFilter accountsFilter) {
        if (legacySbbolAdapter.checkNotMigration(accountsFilter.getDigitalId())) {
            throw new PartnerMigrationException();
        }
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
        if (legacySbbolAdapter.checkNotMigration(account.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(account.getDigitalId(), UUID.fromString(account.getPartnerId()));
        if (foundPartner.isEmpty()) {
            throw new EntryNotFoundException("partner", account.getDigitalId(), account.getPartnerId());
        }
        var requestAccount = accountMapper.toAccount(account);
        var savedAccount = accountRepository.save(requestAccount);
        var response = accountMapper.toAccount(savedAccount, budgetMaskService);
        replicationService.saveCounterparty(response);
        return new AccountResponse().account(response);
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(AccountChange account) {
        if (legacySbbolAdapter.checkNotMigration(account.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var foundAccount = accountRepository.getByDigitalIdAndUuid(account.getDigitalId(), UUID.fromString(account.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, account.getDigitalId(), account.getId()));
        if (account.getVersion() <= foundAccount.getVersion()) {
            throw new OptimisticLockingFailureException("Версия документа в базе данных " + foundAccount.getVersion() +
                " больше или равна версии документа в запросе version=" + account.getVersion());
        }
        if (AccountStateType.SIGNED.equals(foundAccount.getState())) {
            throw new SignAccountException(Collections.singletonList("Ошибка обновления счёта клиента, нельзя обновлять подписанные счёта"));
        }
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(account.getDigitalId(), UUID.fromString(account.getPartnerId()));
        if (foundPartner.isEmpty()) {
            throw new EntryNotFoundException("partner", account.getDigitalId(), account.getPartnerId());
        }
        accountMapper.updateAccount(account, foundAccount);
        var savedAccount = accountRepository.save(foundAccount);
        var response = accountMapper.toAccount(savedAccount, budgetMaskService);
        replicationService.saveCounterparty(response);
        return new AccountResponse().account(response);
    }

    @Override
    @Transactional
    public void deleteAccount(String digitalId, String id) {
        if (legacySbbolAdapter.checkNotMigration(digitalId)) {
            throw new PartnerMigrationException();
        }
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        accountRepository.delete(foundAccount);
        replicationService.deleteCounterparty(foundAccount);
    }

    @Override
    @Transactional
    public AccountResponse changePriority(AccountPriority accountPriority) {
        var digitalId = accountPriority.getDigitalId();
        if (accountPriority.getPriorityAccount()) {
            var foundAccounts = accountRepository.findByDigitalIdAndPriorityAccountIsTrue(digitalId);
            if (!CollectionUtils.isEmpty(foundAccounts)) {
                throw new BadRequestException("У пользователя digitalId: " + digitalId + "Уже есть приоритетные счета");
            }
        }
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(accountPriority.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountPriority.getId()));
        foundAccount.setPriorityAccount(accountPriority.getPriorityAccount());
        var savedAccount = accountRepository.save(foundAccount);
        var account = accountMapper.toAccount(savedAccount, budgetMaskService);
        return new AccountResponse().account(account);
    }
}

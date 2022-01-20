package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class AccountServiceImpl implements AccountService {

    public static final String DOCUMENT_NAME = "account";

    private final PartnerRepository partnerRepository;
    private final AccountRepository accountRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        LegacySbbolAdapter legacySbbolAdapter,
        AccountMapper accountMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(String digitalId, String id) {
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            var account = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
            if (account == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            var response = accountMapper.toAccount(account);
            return new AccountResponse().account(response);
        } else {
            //TODO DCBBRAIN-1642 реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountsResponse getAccounts(AccountsFilter accountsFilter) {
        if (legacySbbolAdapter.checkMigration(accountsFilter.getDigitalId())) {
            var response = accountRepository.findByFilter(accountsFilter);
            var accountsResponse = new AccountsResponse();
            for (var entity : response) {
                accountsResponse.addAccountsItem(accountMapper.toAccount(entity));
            }
            accountsResponse.setPagination(
                new Pagination()
                    .offset(accountsFilter.getPagination().getOffset())
                    .count(accountsFilter.getPagination().getCount())
            );
            return accountsResponse;
        } else {
            //TODO DCBBRAIN-1642 реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public AccountResponse saveAccount(Account account) {
        if (legacySbbolAdapter.checkMigration(account.getDigitalId())) {
            var partner = partnerRepository.getByDigitalIdAndUuid(account.getDigitalId(), UUID.fromString(account.getPartnerId()));
            if (partner == null) {
                throw new EntryNotFoundException("partner", account.getDigitalId(), account.getPartnerId());
            }
            var requestAccount = accountMapper.toAccount(account);
            var savedAccount = accountRepository.save(requestAccount);
            var response = accountMapper.toAccount(savedAccount);
            return new AccountResponse().account(response);
        } else {
            //TODO DCBBRAIN-1642 реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(Account account) {
        if (legacySbbolAdapter.checkMigration(account.getDigitalId())) {
            var foundAccount = accountRepository.getByDigitalIdAndUuid(account.getDigitalId(), UUID.fromString(account.getId()));
            if (foundAccount == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, account.getDigitalId(), account.getId());
            }
            accountMapper.updateAccount(account, foundAccount);
            var saveAccount = accountRepository.save(foundAccount);
            var mappedAccount = accountMapper.toAccount(saveAccount);
            return new AccountResponse().account(mappedAccount);
        } else {
            //TODO DCBBRAIN-1642 реализация работы с legacy
        }
        return null;
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
        } else {
            //TODO DCBBRAIN-1642 реализация работы с legacy
        }
    }
}

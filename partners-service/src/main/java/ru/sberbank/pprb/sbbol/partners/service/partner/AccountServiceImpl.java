package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

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
            var account = accountRepository.getByDigitalIdAndId(digitalId, UUID.fromString(id));
            if (account == null) {
                throw new EntryNotFoundException("account", digitalId, id);
            }
            var response = accountMapper.toAccount(account);
            return new AccountResponse().account(response);
        } else {
            //TODO реализация работы с legacy
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
            //TODO реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public AccountResponse saveAccount(Account account) {
        if (legacySbbolAdapter.checkMigration(account.getDigitalId())) {
            var partner = partnerRepository.getByDigitalIdAndId(account.getDigitalId(), UUID.fromString(account.getPartnerUuid()));
            if (partner == null) {
                throw new EntryNotFoundException("partner", account.getDigitalId(), account.getPartnerUuid());
            }
            var requestAccount = accountMapper.toAccount(account);
            if (!CollectionUtils.isEmpty(requestAccount.getBanks())) {
                for (var bank : requestAccount.getBanks()) {
                    bank.setAccount(requestAccount);
                    for (var bankAccount : bank.getBankAccounts()) {
                        bankAccount.setBank(bank);
                    }
                }
            }
            var saveAccount = accountRepository.save(requestAccount);
            var response = accountMapper.toAccount(saveAccount);
            return new AccountResponse().account(response);
        } else {
            //TODO реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(Account account) {
        if (legacySbbolAdapter.checkMigration(account.getDigitalId())) {
            var searchAccount = accountRepository.getByDigitalIdAndId(account.getDigitalId(), UUID.fromString(account.getUuid()));
            if (searchAccount == null) {
                throw new EntryNotFoundException("account", account.getDigitalId(), account.getUuid());
            }
            accountMapper.updateAccount(account, searchAccount);
            var saveAccount = accountRepository.save(searchAccount);
            var response = accountMapper.toAccount(saveAccount);
            return new AccountResponse().account(response);
        } else {
            //TODO реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public Error deleteAccount(String digitalId, String id) {
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            var searchAccount = accountRepository.getByDigitalIdAndId(digitalId, UUID.fromString(id));
            if (searchAccount == null) {
                throw new EntryNotFoundException("account", digitalId, id);
            }
            accountRepository.delete(searchAccount);
        } else {
            //TODO реализация работы с legacy
        }
        return new Error();
    }
}

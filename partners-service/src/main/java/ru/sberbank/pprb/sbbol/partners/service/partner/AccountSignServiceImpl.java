package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;

import java.util.Collections;
import java.util.UUID;

public class AccountSignServiceImpl implements AccountSignService {

    private static final String DOCUMENT_NAME = "account";

    private final AccountRepository accountRepository;
    private final AccountSignRepository accountSignRepository;
    private final AccountSingMapper accountSingMapper;
    private final LegacySbbolAdapter legacySbbolAdapter;

    public AccountSignServiceImpl(
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        AccountSingMapper accountSingMapper,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
        this.accountSingMapper = accountSingMapper;
        this.legacySbbolAdapter = legacySbbolAdapter;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountsSignResponse getAccountsSign(AccountsSignFilter filter) {
        if (legacySbbolAdapter.checkNotMigration(filter.getDigitalId())) {
            throw new PartnerMigrationException();
        }
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
        if (legacySbbolAdapter.checkNotMigration(accountsSign.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var response = new AccountsSignInfoResponse();
        response.setDigitalId(accountsSign.getDigitalId());
        for (var accountSign : accountsSign.getAccountsSignDetail()) {
            var account = accountRepository.getByDigitalIdAndUuid(accountsSign.getDigitalId(), UUID.fromString(accountSign.getAccountId()))
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, accountsSign.getDigitalId(), accountSign.getAccountId()));
            if (AccountStateType.SIGNED.equals(account.getState())) {
                response.addErrorsItem(
                    new Error()
                        .code("PPRB:PARTNER:SIGN_ACCOUNT_EXCEPTION")
                        .text(Collections.singletonList("Account " + account.getUuid() + " уже имеет статус " + account.getState()))
                );
                continue;
            }
            var sign = accountSingMapper.toSing(accountSign, account.getPartnerUuid());
            var savedSign = accountSignRepository.save(sign);
            account.setState(AccountStateType.SIGNED);
            accountRepository.save(account);
            response.addAccountsSignDetailItem(accountSingMapper.toSignAccount(savedSign));
        }
        return response;
    }

    @Override
    @Transactional
    public void deleteAccountSign(String digitalId, String accountId) {
        if (legacySbbolAdapter.checkNotMigration(digitalId)) {
            throw new PartnerMigrationException();
        }
        var uuid = UUID.fromString(accountId);
        var account = accountRepository.getByDigitalIdAndUuid(digitalId, uuid);
        if (account.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountId);
        }
        var sign = accountSignRepository.getByAccountUuid(uuid)
            .orElseThrow(() -> new EntryNotFoundException("sign", digitalId, accountId));
        accountSignRepository.delete(sign);
        account.get().setState(AccountStateType.NOT_SIGNED);
        accountRepository.save(account.get());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountSignInfo getAccountSign(String digitalId, String accountId) {
        if (legacySbbolAdapter.checkNotMigration(digitalId)) {
            throw new PartnerMigrationException();
        }
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

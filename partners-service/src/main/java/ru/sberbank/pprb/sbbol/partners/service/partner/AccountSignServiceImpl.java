package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
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
    private final CounterpartyMapper counterpartyMapper;
    private final LegacySbbolAdapter legacySbbolAdapter;

    public AccountSignServiceImpl(
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        AccountSingMapper accountSingMapper,
        CounterpartyMapper counterpartyMapper,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
        this.accountSingMapper = accountSingMapper;
        this.counterpartyMapper = counterpartyMapper;
        this.legacySbbolAdapter = legacySbbolAdapter;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountsSignResponse getAccountsSign(AccountsSignFilter filter) {
        var foundSignedAccounts = accountRepository.findByFilter(filter);
        var accountsSignResponse = new AccountsSignResponse();
        for (AccountEntity account : foundSignedAccounts) {
            accountsSignResponse.addAccountsSignItem(accountSingMapper.toSignAccount(account));
        }
        accountsSignResponse.setPagination(
            new Pagination()
                .offset(filter.getPagination().getOffset())
                .count(filter.getPagination().getCount())
        );
        return accountsSignResponse;
    }

    @Override
    @Transactional
    public AccountsSignInfoResponse createAccountsSign(AccountsSignInfo accountsSign) {
        var response = new AccountsSignInfoResponse();
        if (legacySbbolAdapter.checkMigration(accountsSign.getDigitalId())) {
            response.setDigitalId(accountsSign.getDigitalId());
            for (var accountSign : accountsSign.getAccountsSignDetail()) {
                var account = accountRepository.getByDigitalIdAndUuid(accountsSign.getDigitalId(), UUID.fromString(accountSign.getAccountId()));
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
        } else {
            for (AccountSignDetail accountSign : accountsSign.getAccountsSignDetail()) {
                var counterpartySignData = counterpartyMapper.toCounterpartySignedData(accountSign);
                legacySbbolAdapter.saveSign(accountsSign.getDigitalId(), counterpartySignData);
                var account = accountRepository.getByDigitalIdAndUuid(accountsSign.getDigitalId(), UUID.fromString(accountSign.getAccountId()));
                var sign = accountSingMapper.toSing(accountSign, account.getPartnerUuid());
                var savedSign = accountSignRepository.save(sign);
                response.addAccountsSignDetailItem(accountSingMapper.toSignAccount(savedSign));
            }
        }
        return response;
    }

    @Override
    @Transactional
    public void deleteAccountSign(String digitalId, String accountId) {
        var uuid = UUID.fromString(accountId);
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            var account = accountRepository.getByDigitalIdAndUuid(digitalId, uuid);
            if (account == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountId);
            }
            var sign = accountSignRepository.getByAccountUuid(uuid);
            if (sign == null) {
                throw new EntryNotFoundException("sign", digitalId, accountId);
            }
            accountSignRepository.delete(sign);
            account.setState(AccountStateType.NOT_SIGNED);
            accountRepository.save(account);
        } else {
            legacySbbolAdapter.removeSign(digitalId, accountId);
            var sign = accountSignRepository.getByAccountUuid(uuid);
            if (sign == null) {
                throw new EntryNotFoundException("sign", digitalId, accountId);
            }
            accountSignRepository.delete(sign);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountSignInfo getAccountSign(String digitalId, String accountId) {
        var uuid = UUID.fromString(accountId);
        var account = accountRepository.getByDigitalIdAndUuid(digitalId, uuid);
        if (account == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, accountId);
        }
        var sign = accountSignRepository.getByAccountUuid(uuid);
        if (sign == null) {
            throw new EntryNotFoundException("sign", digitalId, accountId);
        }
        return accountSingMapper.toSignAccount(sign, digitalId);
    }
}

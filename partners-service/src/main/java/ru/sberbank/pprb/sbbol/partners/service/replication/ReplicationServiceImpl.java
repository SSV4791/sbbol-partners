package ru.sberbank.pprb.sbbol.partners.service.replication;

import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Loggable
public class ReplicationServiceImpl implements ReplicationService {

    private final PartnerRepository partnerRepository;
    private final AccountRepository accountRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final AccountMapper accountMapper;
    private final AccountSingMapper accountSingMapper;
    private final CounterpartyMapper counterpartyMapper;

    public ReplicationServiceImpl(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        LegacySbbolAdapter legacySbbolAdapter,
        AccountMapper accountMapper,
        AccountSingMapper accountSingMapper,
        CounterpartyMapper counterpartyMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.accountMapper = accountMapper;
        this.accountSingMapper = accountSingMapper;
        this.counterpartyMapper = counterpartyMapper;
    }

    @Override
    public void saveCounterparty(List<Account> accounts) {
        for (Account account : accounts) {
            saveCounterparty(account);
        }
    }

    @Override
    public void saveCounterparty(Account account) {
        var partnerUuid = UUID.fromString(account.getPartnerId());
        var accountUuid = UUID.fromString(account.getId());
        var digitalId = account.getDigitalId();
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerUuid)
            .orElseThrow();
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, accountUuid)
            .orElseThrow();
        var foundCounterparty = legacySbbolAdapter.getByPprbGuid(digitalId, account.getId());
        if (foundCounterparty == null) {
            Counterparty counterparty = counterpartyMapper.toCounterparty(foundPartner, foundAccount);
            legacySbbolAdapter.create(digitalId, counterparty);
        } else {
            counterpartyMapper.toCounterparty(foundPartner, foundAccount, foundCounterparty);
            legacySbbolAdapter.update(digitalId, foundCounterparty);
        }
    }

    @Override
    public void deleteCounterparties(List<AccountEntity> accounts) {
        var accountList = accountMapper.toAccounts(accounts);
        for (Account account : accountList) {
            var counterparty = legacySbbolAdapter.getByPprbGuid(account.getDigitalId(), account.getId());
            if (counterparty != null) {
                legacySbbolAdapter.delete(account.getDigitalId(), account.getId());
            }
        }
    }

    @Override
    public void deleteCounterparty(AccountEntity account) {
        deleteCounterparties(Collections.singletonList(account));
    }

    @Override
    public void saveSign(String digitalId, SignEntity sign) {
        var accountId = sign.getAccountUuid().toString();
        var counterparty = legacySbbolAdapter.getByPprbGuid(digitalId, accountId);
        if (counterparty != null) {
            var counterpartySignData = accountSingMapper.toCounterpartySignData(sign);
            legacySbbolAdapter.saveSign(digitalId, counterpartySignData);
        }
    }

    @Override
    public void deleteSign(String digitalId, String counterpartyId) {
        var counterparty = legacySbbolAdapter.getByPprbGuid(digitalId, counterpartyId);
        if (counterparty != null) {
            legacySbbolAdapter.removeSign(digitalId, counterpartyId);
        }
    }
}

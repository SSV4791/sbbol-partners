package ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator;

import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class AccountMapperDecorator implements AccountMapper {

    @Autowired
    @Qualifier("delegate")
    private AccountMapper delegate;

    @Autowired
    private BankMapper bankMapper;

    @Autowired
    private BudgetMaskService budgetMaskService;

    @Autowired
    private IdsHistoryService idsHistoryService;

    @Override
    public Account toAccount(AccountEntity account) {
        var accountResponse = delegate.toAccount(account);
        setBudgetMarker(accountResponse);
        fillExternalIds(account, accountResponse);
        return accountResponse;
    }

    @Override
    public Account toAccount(AccountEntity account, UUID externalId) {
        var accountResponse = delegate.toAccount(account);
        accountResponse.setExternalIds(Set.of(externalId));
        return accountResponse;
    }

    private void fillExternalIds(AccountEntity account, Account accountResponse) {
        var idsHistory = idsHistoryService.getAccountByPprbUuid(account.getDigitalId(), account.getUuid());
        accountResponse.externalIds(Set.copyOf(idsHistory));
    }

    @Override
    public AccountWithPartnerResponse toAccountWithPartner(AccountEntity account) {
        var accountWithPartnerResponse = delegate.toAccountWithPartner(account);
        setBudgetMarker(accountWithPartnerResponse.getAccount());
        return accountWithPartnerResponse;
    }

    @Override
    public List<AccountWithPartnerResponse> toAccountsWithPartner(List<AccountEntity> accounts) {
        var accountWithPartnerResponseList = delegate.toAccountsWithPartner(accounts);
        accountWithPartnerResponseList.forEach(accountWithPartnerResponse ->
            setBudgetMarker(accountWithPartnerResponse.getAccount()));
        return accountWithPartnerResponseList;
    }

    @Override
    public AccountChange toAccount(AccountChangeFullModel accountChangeFullModel, String digitalId, UUID partnerId) {
        var account = delegate.toAccount(accountChangeFullModel, digitalId, partnerId);
        Optional.ofNullable(account.getBank())
            .ifPresent(bank -> bank.setAccountId(account.getId()));
        return account;
    }

    @Override
    public void updateAccount(AccountChange account, @MappingTarget AccountEntity accountEntity) {
        delegate.updateAccount(account, accountEntity);
        if (account.getBank() != null) {
            if (accountEntity.getBank() == null) {
                accountEntity.setBank(new BankEntity());
            }
            bankMapper.updateBank(account.getBank(), accountEntity.getBank());
        }
        delegate.mapBidirectional(accountEntity);
    }

    @Override
    public void patchAccount(AccountChange account, AccountEntity accountEntity) {
        delegate.patchAccount(account, accountEntity);
        if (account.getBank() != null) {
            if (accountEntity.getBank() == null) {
                accountEntity.setBank(new BankEntity());
            }
            bankMapper.patchBank(account.getBank(), accountEntity.getBank());
        }
        delegate.mapBidirectional(accountEntity);
    }

    private boolean isBudget(Account account) {
        if (account != null) {
            var bank = account.getBank();
            if (bank != null) {
                var bankAccount = Objects.nonNull(bank.getBankAccount()) ? bank.getBankAccount().getBankAccount() : null;
                return budgetMaskService.isBudget(account.getAccount(), bank.getBic(), bankAccount);
            }
        }
        return false;
    }

    private void setBudgetMarker(Account account) {
        if (account != null) {
            account.setBudget(isBudget(account));
        }
    }
}

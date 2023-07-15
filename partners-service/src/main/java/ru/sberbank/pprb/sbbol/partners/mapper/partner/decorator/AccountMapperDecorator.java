package ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator;

import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;
import ru.sberbank.pprb.sbbol.partners.storage.GkuInnCacheableStorage;

import java.util.List;
import java.util.Optional;

public abstract class AccountMapperDecorator implements AccountMapper {

    @Autowired
    @Qualifier("delegate")
    private AccountMapper delegate;

    @Autowired
    private GkuInnCacheableStorage gkuInnCacheableStorage;

    @Autowired
    private BankMapper bankMapper;

    @Autowired
    private BudgetMaskService budgetMaskService;

    @Override
    public AccountWithPartnerResponse toAccountWithPartner(AccountEntity accountDto) {
        var accountWithPartnerResponse = delegate.toAccountWithPartner(accountDto);
        accountWithPartnerResponse.setGku(isGkuInn(accountWithPartnerResponse.getInn()));
        setBudgetMarker(accountWithPartnerResponse.getAccount());
        return accountWithPartnerResponse;
    }

    public AccountWithPartnerResponse toAccountWithPartner(PartnerEntity partner) {
        var accountWithPartnerResponse = delegate.toAccountWithPartner(partner);
        accountWithPartnerResponse.setGku(isGkuInn(accountWithPartnerResponse.getInn()));
        return accountWithPartnerResponse;
    }

    @Override
    public List<AccountWithPartnerResponse> toAccountsWithPartner(PartnerEntity partner) {
        var accountWithPartnerResponseList = delegate.toAccountsWithPartner(partner);
        accountWithPartnerResponseList.forEach(accountWithPartnerResponse ->
            accountWithPartnerResponse.setGku(isGkuInn(accountWithPartnerResponse.getInn())));
        return accountWithPartnerResponseList;
    }

    @Override
    public List<AccountWithPartnerResponse> toAccountsWithPartner(List<AccountEntity> accounts) {
        var accountWithPartnerResponseList = delegate.toAccountsWithPartner(accounts);
        accountWithPartnerResponseList.forEach(accountWithPartnerResponse -> {
            accountWithPartnerResponse.setGku(isGkuInn(accountWithPartnerResponse.getInn()));
            setBudgetMarker(accountWithPartnerResponse.getAccount());
        });
        return accountWithPartnerResponseList;
    }

    @Override
    public AccountChange toAccount(AccountChangeFullModel accountChangeFullModel, String digitalId, String partnerId) {
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

    private boolean isGkuInn(String inn) {
        return gkuInnCacheableStorage.isGkuInn(inn);
    }

    private boolean isBudget(Account account) {
        if (account !=  null) {
            var bank = account.getBank();
            if (bank != null) {
                var bankAccount = bank.getBankAccount();
                if (bankAccount != null) {
                    return budgetMaskService.isBudget(account.getAccount(), bank.getBic(), bankAccount.getBankAccount());
                }
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

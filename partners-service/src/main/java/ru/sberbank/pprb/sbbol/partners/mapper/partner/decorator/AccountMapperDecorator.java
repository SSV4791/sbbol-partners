package ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator;

import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.sberbank.pprb.sbbol.partners.entity.partner.enums.ParentType.ACCOUNT;

public abstract class AccountMapperDecorator implements AccountMapper {

    @Autowired
    @Qualifier("delegate")
    private AccountMapper delegate;

    @Autowired
    private BankMapper bankMapper;

    @Autowired
    private BudgetMaskService budgetMaskService;

    @Override
    public Account toAccount(AccountEntity account) {
        Account accountResponse = delegate.toAccount(account);
        setBudgetMarker(accountResponse);
        fillExternalIds(account, accountResponse);
        return accountResponse;
    }

    private void fillExternalIds(AccountEntity account, Account accountResponse) {
        for (IdsHistoryEntity idLink : account.getIdLinks()) {
            accountResponse.addExternalIdsItem(idLink.getExternalId());
        }
    }

    @Override
    public List<AccountEntity> toAccounts(Set<AccountCreateFullModel> accounts, String digitalId, UUID partnerUuid) {
        if (CollectionUtils.isEmpty(accounts)) {
            return Collections.emptyList();
        }
        return accounts.stream()
            .map(value -> toAccount(value, digitalId, partnerUuid))
            .collect(Collectors.toList());
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

    public AccountEntity toAccount(AccountCreateFullModel account, String digitalId, UUID partnerUuid) {
        var accountEntity = delegate.toAccount(account, digitalId, partnerUuid);
        createIdLink(accountEntity, account.getExternalId());
        return accountEntity;
    }

    public AccountEntity toAccount(AccountCreate account) {
        var accountEntity = delegate.toAccount(account);
        createIdLink(accountEntity, account.getExternalId());
        return accountEntity;
    }

    private void createIdLink(AccountEntity account, UUID externalId) {
        UUID accountUuid = Objects.isNull(externalId) ? account.getUuid() : externalId;
        var idHistoryEntity = new IdsHistoryEntity();
        idHistoryEntity.setExternalId(accountUuid);
        idHistoryEntity.setDigitalId(account.getDigitalId());
        idHistoryEntity.setParentType(ACCOUNT);
        idHistoryEntity.setAccount(account);
        account.setIdLinks(List.of(idHistoryEntity));
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

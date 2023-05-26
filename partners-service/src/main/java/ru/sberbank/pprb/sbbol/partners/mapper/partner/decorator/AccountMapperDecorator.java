package ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator;

import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

public abstract class AccountMapperDecorator implements AccountMapper {

    private static final String CURRENCY_ISO_CODE_RUR = "RUB";

    private static final String CURRENCY_CODE_RUR = "643";

    @Autowired
    @Qualifier("delegate")
    private AccountMapper delegate;

    @Autowired
    private BankMapper bankMapper;

    @Override
    public AccountEntity toAccount(AccountCreateFullModel account, String digitalId, UUID partnerUuid) {
        var accountEntity = delegate.toAccount(account, digitalId, partnerUuid);
        accountEntity.setCurrencyIsoCode(normalizationCurrencyIsoCode(account.getCurrencyIsoCode()));
        accountEntity.setCurrencyCode(normalizationCurrencyCode(account.getCurrencyCode()));
        delegate.mapBidirectional(accountEntity);
        return accountEntity;
    }

    @Override
    public AccountEntity toAccount(AccountCreate account) {
        var accountEntity = delegate.toAccount(account);
        accountEntity.setCurrencyIsoCode(normalizationCurrencyIsoCode(account.getCurrencyIsoCode()));
        accountEntity.setCurrencyCode(normalizationCurrencyCode(account.getCurrencyCode()));
        delegate.mapBidirectional(accountEntity);
        return accountEntity;
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
        accountEntity.setCurrencyIsoCode(normalizationCurrencyIsoCode(account.getCurrencyIsoCode()));
        accountEntity.setCurrencyCode(normalizationCurrencyCode(account.getCurrencyCode()));
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

    protected String normalizationCurrencyIsoCode(String currencyIsoCode) {
        return isNull(currencyIsoCode) ? CURRENCY_ISO_CODE_RUR : currencyIsoCode;
    }

    protected String normalizationCurrencyCode(String currencyCode) {
        return isNull(currencyCode) ? CURRENCY_CODE_RUR : currencyCode;
    }
}

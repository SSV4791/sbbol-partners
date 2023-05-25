package ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator;

import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;

import java.util.UUID;

import static java.util.Objects.isNull;

public abstract class AccountMapperDecorator implements AccountMapper {

    private static final String CURRENCY_ISO_CODE_RUR = "RUB";

    private static final String CURRENCY_CODE_RUR = "643";

    @Autowired
    @Qualifier("delegate")
    private AccountMapper delegate;

    @Override
    public AccountEntity toAccount(AccountCreateFullModel account, String digitalId, UUID partnerUuid) {
        var accountEntity = delegate.toAccount(account, digitalId, partnerUuid);
        accountEntity.setCurrencyIsoCode(normalizationCurrencyIsoCode(account.getCurrencyIsoCode()));
        accountEntity.setCurrencyCode(normalizationCurrencyCode(account.getCurrencyCode()));
        return accountEntity;
    }

    @Override
    public AccountEntity toAccount(AccountCreate account) {
        var accountEntity = delegate.toAccount(account);
        accountEntity.setCurrencyIsoCode(normalizationCurrencyIsoCode(account.getCurrencyIsoCode()));
        accountEntity.setCurrencyCode(normalizationCurrencyCode(account.getCurrencyCode()));
        return accountEntity;
    }

    @Override
    public void updateAccount(AccountChange account, @MappingTarget AccountEntity accountEntity) {
        delegate.updateAccount(account, accountEntity);
        accountEntity.setCurrencyIsoCode(normalizationCurrencyIsoCode(account.getCurrencyIsoCode()));
        accountEntity.setCurrencyCode(normalizationCurrencyCode(account.getCurrencyCode()));
    }

    protected String normalizationCurrencyIsoCode(String currencyIsoCode) {
        return isNull(currencyIsoCode) ? CURRENCY_ISO_CODE_RUR : currencyIsoCode;
    }

    protected String normalizationCurrencyCode(String currencyCode) {
        return isNull(currencyCode) ? CURRENCY_CODE_RUR : currencyCode;
    }
}

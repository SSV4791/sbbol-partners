package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.DecoratedWith;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator.AccountMapperDecorator;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnerInfo;
import ru.sberbank.pprb.sbbol.partners.model.SignType;

import java.util.List;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.saveSearchString;

@Loggable
@Mapper(uses = {
    BankMapper.class,
    BaseMapper.class,
    GkuMapper.class,
},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
@DecoratedWith(AccountMapperDecorator.class)
public interface AccountMapper {

    @InheritConfiguration
    List<Account> toAccounts(List<AccountEntity> accounts);

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "partnerId", source = "partnerUuid")
    @Mapping(target = "changeDate", source = "lastModifiedDate")
    @Mapping(target = "externalIds", ignore = true)
    @Mapping(target = "budget", ignore = true)
    Account toAccount(AccountEntity account);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Account toAccount(AccountEntity account, UUID externalId);

    AccountChange toAccount(AccountChangeFullModel account, String digitalId, UUID partnerId);

    AccountCreate toAccountCreate(AccountChangeFullModel account, String digitalId, UUID partnerId);

    @Mapping(target = "partnerUuid", source = "partnerUuid")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "account", source = "account.account")
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "state", constant = "NOT_SIGNED")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "partnerType", constant = "PARTNER")
    AccountEntity toAccount(AccountCreateFullModel account, String digitalId, UUID partnerUuid);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", source = "partnerId")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "state", constant = "NOT_SIGNED")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "partnerType", constant = "PARTNER")
    AccountEntity toAccount(AccountCreate account);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", source = "partnerId")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "bank", ignore = true)
    void updateAccount(AccountChange account, @MappingTarget AccountEntity accountEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", source = "partnerId")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "bank", ignore = true)
    void patchAccount(AccountChange account, @MappingTarget AccountEntity accountEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget AccountEntity account) {
        var partnerUUID = account.getPartnerUuid();
        var accountNumber = account.getAccount();
        String bic = null;
        String corAccount = null;
        var bank = account.getBank();
        if (bank != null) {
            bank.setAccount(account);
            bic = bank.getBic();
            var bankAccount = bank.getBankAccount();
            if (bankAccount != null) {
                corAccount = bankAccount.getAccount();
            }
        }
        var search = prepareSearchField(partnerUUID, accountNumber, bic, corAccount);
        account.setSearch(search);
    }

    List<AccountWithPartnerResponse> toAccountsWithPartner(List<AccountEntity> accounts);

    @Mapping(target = "account", source = "accountEntity")
    @Mapping(target = "id", source = "partner.uuid")
    @Mapping(target = "digitalId", source = "partner.digitalId")
    @Mapping(target = "legalForm", source = "partner.legalType")
    @Mapping(target = "orgName", source = "partner.orgName")
    @Mapping(target = "firstName", source = "partner.firstName")
    @Mapping(target = "secondName", source = "partner.secondName")
    @Mapping(target = "middleName", source = "partner.middleName")
    @Mapping(target = "inn", source = "partner.inn")
    @Mapping(target = "kpp", source = "partner.kpp")
    @Mapping(target = "comment", source = "partner.comment")
    @Mapping(target = "version", source = "partner.version")
    @Mapping(target = "gku", source = "partner.gkuInnEntity", qualifiedByName = "isGku")
    AccountWithPartnerResponse toAccountWithPartner(AccountEntity accountEntity);

    default List<AccountWithPartnerResponse> toAccountsWithPartner(PartnerInfo partner) {
        return List.of(toAccountWithPartner(partner));
    }

    @Mapping(target = "account", ignore = true)
    AccountWithPartnerResponse toAccountWithPartner(PartnerInfo partner);

    default String prepareSearchField(
        UUID partnerUUID,
        String account,
        String bic,
        String corAccount
    ) {
        return saveSearchString(partnerUUID.toString(), account, bic, corAccount);
    }

    default AccountStateType toAccountStateType(SignType signType) {
        return switch (signType) {
            case NOT_SIGNED -> AccountStateType.NOT_SIGNED;
            case SIGNED -> AccountStateType.SIGNED;
        };
    }
}

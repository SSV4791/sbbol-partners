package ru.sberbank.pprb.sbbol.partners.mapper.counterparty;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.counterparties.model.CounterpartySearchRequest;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.Counterparty;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyCheckRequisites;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyFilter;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyView;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CounterpartyMapper extends BaseMapper {

    CounterpartyCheckRequisites toCounterpartyCheckRequisites(CounterpartySearchRequest request);

    @Mapping(target = "operationGuid", ignore = true)
    @Mapping(target = "bankCity", ignore = true)
    @Mapping(target = "settlementType", ignore = true)
    @Mapping(target = "counterpartyPhone", ignore = true)
    @Mapping(target = "counterpartyEmail", ignore = true)
    @Mapping(target = "pprbGuid", source = "sbbolGuid")
    @Mapping(target = "name", source = "partner.orgName")
    @Mapping(target = "taxNumber", source = "partner.inn")
    @Mapping(target = "kpp", source = "partner.kpp")
    @Mapping(target = "description", source = "partner.comment")
    @Mapping(target = "account", source = "account.account")
    @Mapping(target = "signed", source = "account.state", qualifiedByName = "toSigned")
    @Mapping(target = "bankName", source = "bank.name")
    @Mapping(target = "bankBic", source = "bank.bic")
    @Mapping(target = "corrAccount", source = "bankAccount.account")
    Counterparty toCounterparty(PartnerEntity partner, AccountEntity account, BankEntity bank, BankAccountEntity bankAccount, String sbbolGuid);

    @Mapping(target = "operationGuid", ignore = true)
    @Mapping(target = "bankCity", ignore = true)
    @Mapping(target = "settlementType", ignore = true)
    @Mapping(target = "counterpartyPhone", ignore = true)
    @Mapping(target = "counterpartyEmail", ignore = true)
    @Mapping(target = "pprbGuid", ignore = true)
    @Mapping(target = "name", source = "partner.orgName")
    @Mapping(target = "taxNumber", source = "partner.inn")
    @Mapping(target = "kpp", source = "partner.kpp")
    @Mapping(target = "description", source = "partner.comment")
    @Mapping(target = "account", source = "account.account")
    @Mapping(target = "signed", source = "account.state", qualifiedByName = "toSigned")
    @Mapping(target = "bankName", source = "account.banks", qualifiedByName = "toBankName")
    @Mapping(target = "bankBic", source = "account.banks", qualifiedByName = "toBankBic")
    @Mapping(target = "corrAccount", source = "account.banks", qualifiedByName = "toCorrAccount")
    Counterparty toCounterparty(PartnerEntity partner, Account account);

    @Mapping(target = "operationGuid", ignore = true)
    @Mapping(target = "bankCity", ignore = true)
    @Mapping(target = "settlementType", ignore = true)
    @Mapping(target = "counterpartyPhone", ignore = true)
    @Mapping(target = "counterpartyEmail", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "signed", ignore = true)
    @Mapping(target = "bankName", ignore = true)
    @Mapping(target = "bankBic", ignore = true)
    @Mapping(target = "corrAccount", ignore = true)
    @Mapping(target = "pprbGuid", ignore = true)
    @Mapping(target = "name", source = "orgName")
    @Mapping(target = "taxNumber", source = "inn")
    @Mapping(target = "kpp", source = "kpp")
    @Mapping(target = "description", source = "comment")
    void updateCounterparty(@MappingTarget() Counterparty counterparty, Partner partner);

    @Mapping(target = "operationGuid", ignore = true)
    @Mapping(target = "bankCity", ignore = true)
    @Mapping(target = "settlementType", ignore = true)
    @Mapping(target = "counterpartyPhone", ignore = true)
    @Mapping(target = "counterpartyEmail", ignore = true)
    @Mapping(target = "pprbGuid", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "taxNumber", ignore = true)
    @Mapping(target = "kpp", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "account", source = "account.account")
    @Mapping(target = "bankName", source = "bank.name")
    @Mapping(target = "bankBic", source = "bank.bic")
    @Mapping(target = "corrAccount", source = "bankAccount.account")
    @Mapping(target = "signed", source = "account.state", qualifiedByName = "toSigned")
    void updateCounterparty(@MappingTarget() Counterparty counterparty, Account account, Bank bank, BankAccount bankAccount);

    @Mapping(target = "citizenship", ignore = true)
    @Mapping(target = "emails", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "legalForm", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "ogrn", ignore = true)
    @Mapping(target = "okpo", ignore = true)
    @Mapping(target = "phones", ignore = true)
    @Mapping(target = "secondName", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "gku", ignore = true)
    @Mapping(target = "budget", ignore = true)
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "id", source = "counterparty.pprbGuid")
    @Mapping(target = "orgName", source = "counterparty.name")
    @Mapping(target = "inn", source = "counterparty.taxNumber")
    @Mapping(target = "kpp", source = "counterparty.kpp")
    @Mapping(target = "comment", source = "counterparty.description")
    Partner toPartner(Counterparty counterparty, String digitalId);

    @Mapping(target = "housingPaymentReceiever", ignore = true)
    @Mapping(target = "searchPattern", ignore = true)
    @Mapping(target = "signed", ignore = true)
    @Mapping(target = "orderBy", ignore = true)
    CounterpartyFilter toCounterpartyFilter(PartnersFilter filter);

    @Mapping(target = "housingPaymentReceiever", ignore = true)
    @Mapping(target = "searchPattern", ignore = true)
    @Mapping(target = "signed", ignore = true)
    @Mapping(target = "orderBy", ignore = true)
    CounterpartyFilter toCounterpartyFilter(AccountsFilter filter);

    default List<Partner> toPartners(List<CounterpartyView> counterparties, String digitalId) {
        return counterparties.stream().map(c -> toPartner(c, digitalId)).collect(Collectors.toList());
    }

    default List<Account> toAccounts(List<CounterpartyView> counterparties, String digitalId, @Context BudgetMaskService budgetMaskService) {
        return counterparties.stream().map(c -> toAccount(c, digitalId, null, budgetMaskService)).collect(Collectors.toList());
    }

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "budget", ignore = true)
    @Mapping(target = "id", expression = "java(accountUuid != null ? accountUuid.toString() : null)")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "partnerId", source = "counterparty.pprbGuid")
    @Mapping(target = "account", source = "counterparty.account")
    @Mapping(target = "state", source = "counterparty.signed", qualifiedByName = "toState")
    @Mapping(target = "banks", source = "counterparty", qualifiedByName = "toBanks")
    Account toAccount(CounterpartyView counterparty, String digitalId, UUID accountUuid, @Context BudgetMaskService budgetMaskService);

    @Mapping(target = "citizenship", ignore = true)
    @Mapping(target = "emails", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "legalForm", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "ogrn", ignore = true)
    @Mapping(target = "okpo", ignore = true)
    @Mapping(target = "phones", ignore = true)
    @Mapping(target = "secondName", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "gku", ignore = true)
    @Mapping(target = "budget", ignore = true)
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "id", source = "counterparty.pprbGuid")
    @Mapping(target = "orgName", source = "counterparty.name")
    @Mapping(target = "inn", source = "counterparty.taxNumber")
    @Mapping(target = "kpp", source = "counterparty.kpp")
    @Mapping(target = "comment", source = "counterparty.description")
    Partner toPartner(CounterpartyView counterparty, String digitalId);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "budget", ignore = true)
    @Mapping(target = "id", expression = "java(accountUuid != null ? accountUuid.toString() : null)")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "partnerId", source = "counterparty.pprbGuid")
    @Mapping(target = "account", source = "counterparty.account")
    @Mapping(target = "state", source = "counterparty.signed", qualifiedByName = "toState")
    @Mapping(target = "banks", source = "counterparty", qualifiedByName = "toBanks")
    Account toAccount(Counterparty counterparty, String digitalId, UUID accountUuid, @Context BudgetMaskService budgetMaskService);

    @Named("toState")
    static Account.StateEnum toState(Boolean signed) {
        if (signed == null) {
            return Account.StateEnum.NOT_SIGNED;
        }
        return signed ? Account.StateEnum.SIGNED : Account.StateEnum.NOT_SIGNED;
    }

    @Named("toBanks")
    static List<Bank> toBanks(Counterparty counterparty) {
        Bank bank = new Bank();
        bank.setBic(counterparty.getBankBic());
        bank.setName(counterparty.getBankName());
        if (counterparty.getCorrAccount() != null) {
            bank.setBankAccounts(Collections.singletonList(new BankAccount().account(counterparty.getCorrAccount())));
        }
        return Collections.singletonList(bank);
    }

    @Named("toBanks")
    static List<Bank> toBanks(CounterpartyView counterparty) {
        Bank bank = new Bank();
        bank.setBic(counterparty.getBankBic());
        bank.setName(counterparty.getBankName());
        if (counterparty.getBankAccount() != null) {
            bank.setBankAccounts(Collections.singletonList(new BankAccount().account(counterparty.getBankAccount())));
        }
        return Collections.singletonList(bank);
    }

    @Named("toSigned")
    static boolean toSigned(AccountStateType type) {
        if (type == null) {
            return false;
        }
        return AccountStateType.SIGNED.equals(type);
    }

    @Named("toSigned")
    static boolean toSigned(Account.StateEnum type) {
        if (type == null) {
            return false;
        }
        return Account.StateEnum.SIGNED.equals(type);
    }

    @Named("toBankName")
    static String toBankName(List<Bank> banks) {
        if (CollectionUtils.isEmpty(banks) || banks.size() != 1) {
            return null;
        }
        return banks.get(0).getName();
    }

    @Named("toBankBic")
    static String toBankBic(List<Bank> banks) {
        if (CollectionUtils.isEmpty(banks) || banks.size() != 1) {
            return null;
        }
        return banks.get(0).getBic();
    }

    @Named("toCorrAccount")
    static String toCorrAccount(List<Bank> banks) {
        if (CollectionUtils.isEmpty(banks) || banks.size() != 1) {
            return null;
        }
        var bank = banks.get(0);
        if (CollectionUtils.isEmpty(bank.getBankAccounts()) || bank.getBankAccounts().size() != 1) {
            return null;
        }
        return bank.getBankAccounts().get(0).getAccount();
    }

    @AfterMapping
    default void mapBudgetMask(@MappingTarget Account account, @Context BudgetMaskService budgetMaskService) {
        for (Bank bank : account.getBanks()) {
            for (BankAccount bankAccount : bank.getBankAccounts()) {
                account.setBudget(budgetMaskService.isBudget(account.getAccount(), bank.getBic(), bankAccount.getAccount()));
            }
        }
    }

    @Mapping(target = "pprbGuid", expression = "java(mapUuid(sign.getAccountId()))")
    @Mapping(target = "signProfileId", source = "signProfileId")
    @Mapping(target = "signDate", source = "dateTimeOfSign", qualifiedByName = "toDate")
    @Mapping(target = "base64sign", source = "sign")
    @Mapping(target = "digest", source = "digest")
    @Mapping(target = "dcsId", source = "externalDataFileId")
    CounterpartySignData toCounterpartySignedData(AccountSignDetail sign);

    @Named("toDate")
    static Date toDate(OffsetDateTime date) {
        return new Date(date.toString());
    }
}

package ru.sberbank.pprb.sbbol.partners.mapper.counterparty;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.counterparties.model.CounterpartySearchRequest;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyCheckRequisites;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;

import java.util.StringJoiner;

@Loggable
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
    @Mapping(target = "pprbGuid", expression = "java(account.getUuid().toString())")
    @Mapping(target = "name", source = "partner", qualifiedByName = "toName")
    @Mapping(target = "taxNumber", source = "partner.inn")
    @Mapping(target = "kpp", source = "partner.kpp")
    @Mapping(target = "description", source = "partner.comment")
    @Mapping(target = "account", source = "account.account")
    @Mapping(target = "signed", source = "account.state", qualifiedByName = "toSign")
    @Mapping(target = "bankName", source = "account.bank", qualifiedByName = "toBankName")
    @Mapping(target = "bankBic", source = "account.bank", qualifiedByName = "toBankBic")
    @Mapping(target = "corrAccount", source = "account.bank", qualifiedByName = "toCorrAccount")
    Counterparty toCounterparty(PartnerEntity partner, AccountEntity account);

    @Mapping(target = "operationGuid", ignore = true)
    @Mapping(target = "bankCity", ignore = true)
    @Mapping(target = "settlementType", ignore = true)
    @Mapping(target = "counterpartyPhone", ignore = true)
    @Mapping(target = "counterpartyEmail", ignore = true)
    @Mapping(target = "pprbGuid", expression = "java(account.getUuid().toString())")
    @Mapping(target = "name", source = "partner", qualifiedByName = "toName")
    @Mapping(target = "taxNumber", source = "partner.inn")
    @Mapping(target = "kpp", source = "partner.kpp")
    @Mapping(target = "description", source = "partner.comment")
    @Mapping(target = "account", source = "account.account")
    @Mapping(target = "signed", source = "account.state", qualifiedByName = "toSign")
    @Mapping(target = "bankName", source = "account.bank", qualifiedByName = "toBankName")
    @Mapping(target = "bankBic", source = "account.bank", qualifiedByName = "toBankBic")
    @Mapping(target = "corrAccount", source = "account.bank", qualifiedByName = "toCorrAccount")
    void toCounterparty(PartnerEntity partner, AccountEntity account, @MappingTarget Counterparty counterparty);

    @Named("toName")
    static String toName(PartnerEntity partner) {
        if (LegalType.PHYSICAL_PERSON == partner.getLegalType()) {
            StringJoiner fioJoiner = new StringJoiner(" ");
            if (partner.getFirstName() != null) {
                fioJoiner.add(partner.getFirstName());
            }
            if (partner.getSecondName() != null) {
                fioJoiner.add(partner.getSecondName());
            }
            if (partner.getMiddleName() != null) {
                fioJoiner.add(partner.getMiddleName());
            }
            return fioJoiner.toString();
        }
        return partner.getOrgName();
    }


    @Named("toBankName")
    static String toBankNameCreate(BankEntity bank) {
        if (bank == null) {
            return null;
        }
        return bank.getName();
    }

    @Named("toBankBic")
    static String toBankBicCreate(BankEntity bank) {
        if (bank == null) {
            return null;
        }
        return bank.getBic();
    }

    @Named("toCorrAccount")
    static String toCorrAccountCreate(BankEntity bank) {
        if (bank == null) {
            return null;
        }
        if (bank.getBankAccount() == null) {
            return null;
        }
        return bank.getBankAccount().getAccount();
    }

    @Named("toSign")
    static Boolean toSign(AccountStateType sign) {
        return sign == AccountStateType.SIGNED;
    }
}

package ru.sberbank.pprb.sbbol.partners.mapper.counterparty;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CounterpartyMapper extends BaseMapper {

    @Mapping(target = "taxNumber", source = "taxNumber", qualifiedByName = "toTaxNumber")
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

    @Named("toName")
    default String toName(PartnerEntity partner) {
        if (LegalType.PHYSICAL_PERSON == partner.getLegalType()) {
            return prepareSearchString(
                partner.getSecondName(),
                partner.getFirstName(),
                partner.getMiddleName()
            );
        }
        return partner.getOrgName();
    }

    @Named("toTaxNumber")
    default String toTaxNumber(String taxNumber) {
        if (StringUtils.isEmpty(taxNumber)) {
            return null;
        }
        return taxNumber;
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

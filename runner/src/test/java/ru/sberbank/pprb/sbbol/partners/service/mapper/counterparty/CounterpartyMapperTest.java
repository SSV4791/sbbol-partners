package ru.sberbank.pprb.sbbol.partners.service.mapper.counterparty;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.counterparties.model.CounterpartySearchRequest;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyCheckRequisites;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;

import static org.assertj.core.api.Assertions.assertThat;

class CounterpartyMapperTest extends BaseUnitConfiguration {

    private static final CounterpartyMapper mapper = Mappers.getMapper(CounterpartyMapper.class);

    @Test
    void toCounterpartyCheckRequisites() {
        var searchRequest = factory.manufacturePojo(CounterpartySearchRequest.class);
        CounterpartyCheckRequisites response = mapper.toCounterpartyCheckRequisites(searchRequest);
        assertThat(searchRequest.getAccountNumber()).isEqualTo(response.getAccountNumber());
        assertThat(searchRequest.getBankAccount()).isEqualTo(response.getBankAccount());
        assertThat(searchRequest.getBankBic()).isEqualTo(response.getBankBic());
        assertThat(searchRequest.getDigitalId()).isEqualTo(response.getDigitalId());
        assertThat(searchRequest.getKpp()).isEqualTo(response.getKpp());
        assertThat(searchRequest.getName()).isEqualTo(response.getName());
        assertThat(searchRequest.getTaxNumber()).isEqualTo(response.getTaxNumber());
    }

    @Test
    void toCounterpartyCheckRequisites_thenTaxNumberIsEmpty() {
        var searchRequest = factory.manufacturePojo(CounterpartySearchRequest.class);
        searchRequest.setTaxNumber("");
        CounterpartyCheckRequisites response = mapper.toCounterpartyCheckRequisites(searchRequest);
        assertThat(searchRequest.getAccountNumber()).isEqualTo(response.getAccountNumber());
        assertThat(searchRequest.getBankAccount()).isEqualTo(response.getBankAccount());
        assertThat(searchRequest.getBankBic()).isEqualTo(response.getBankBic());
        assertThat(searchRequest.getDigitalId()).isEqualTo(response.getDigitalId());
        assertThat(searchRequest.getKpp()).isEqualTo(response.getKpp());
        assertThat(searchRequest.getName()).isEqualTo(response.getName());
        assertThat(response.getTaxNumber()).isNull();
    }

    @Test
    void toCounterpartyTest() {
        var partner = factory.manufacturePojo(PartnerEntity.class);
        var account = factory.manufacturePojo(AccountEntity.class);
        var bank = factory.manufacturePojo(BankEntity.class);
        var bankAccount = factory.manufacturePojo(BankAccountEntity.class);
        bank.setBankAccount(bankAccount);
        account.setBank(bank);
        Counterparty response = mapper.toCounterparty(partner, account);
        if (LegalType.PHYSICAL_PERSON.equals(partner.getLegalType())) {
            assertThat(response.getName())
                .isEqualTo(partner.getSecondName() + " " + partner.getFirstName() + " " + partner.getMiddleName());
        } else {
            assertThat(response.getName()).isEqualTo(partner.getOrgName());
        }
        assertThat(response.getTaxNumber()).isEqualTo(partner.getInn());
        assertThat(response.getKpp()).isEqualTo(partner.getKpp());
        assertThat(response.getDescription()).isEqualTo(partner.getComment());
        assertThat(response.getAccount()).isEqualTo(account.getAccount());
        assertThat(response.getBankName()).isEqualTo(bank.getName());
        assertThat(response.getBankBic()).isEqualTo(bank.getBic());
        assertThat(response.getCorrAccount()).isEqualTo(bankAccount.getAccount());
        assertThat(response.getPprbGuid()).isEqualTo(account.getUuid().toString());
        assertThat(response.getOperationGuid()).isNull();
        assertThat(response.getBankCity()).isNull();
        assertThat(response.getSettlementType()).isNull();
        assertThat(response.getCounterpartyPhone()).isNull();
        assertThat(response.getCounterpartyEmail()).isNull();
    }
}

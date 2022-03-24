package ru.sberbank.pprb.sbbol.partners.mapper.counterparty;

import io.qameta.allure.AllureId;
import org.apache.commons.lang.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;
import ru.sberbank.pprb.sbbol.counterparties.model.CounterpartySearchRequest;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.Counterparty;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyCheckRequisites;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyFilter;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyView;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@UnitTestLayer
class CounterpartyMapperTest {

    private static final CounterpartyMapper mapper = Mappers.getMapper(CounterpartyMapper.class);
    private static final PodamFactory factory = new PodamFactoryImpl();

    @Test
    @AllureId("34054")
    void toCounterpartyCheckRequisites() {
        var searchRequest = factory.manufacturePojo(CounterpartySearchRequest.class);
        CounterpartyCheckRequisites response = mapper.toCounterpartyCheckRequisites(searchRequest);
        assertEquals(searchRequest.getAccountNumber(), response.getAccountNumber());
        assertEquals(searchRequest.getBankAccount(), response.getBankAccount());
        assertEquals(searchRequest.getBankBic(), response.getBankBic());
        assertEquals(searchRequest.getDigitalId(), response.getDigitalId());
        assertEquals(searchRequest.getKpp(), response.getKpp());
        assertEquals(searchRequest.getName(), response.getName());
        assertEquals(searchRequest.getTaxNumber(), response.getTaxNumber());
    }

    @Test
    @AllureId("34045")
    void toCounterpartyTest() {
        var partner = factory.manufacturePojo(PartnerEntity.class);
        var account = factory.manufacturePojo(AccountCreate.class);
        var bank = factory.manufacturePojo(BankCreate.class);
        var bankAccount = factory.manufacturePojo(BankAccountCreate.class);
        bank.setBankAccounts(Collections.singletonList(bankAccount));
        account.setBanks(Collections.singletonList(bank));
        Counterparty response = mapper.toCounterparty(partner, account);
        assertEquals(response.getName(), partner.getOrgName());
        assertEquals(response.getTaxNumber(), partner.getInn());
        assertEquals(response.getKpp(), partner.getKpp());
        assertEquals(response.getDescription(), partner.getComment());
        assertEquals(response.getAccount(), account.getAccount());
        assertEquals(response.getBankName(), bank.getName());
        assertEquals(response.getBankBic(), bank.getBic());
        assertEquals(response.getCorrAccount(), bankAccount.getAccount());
        assertNull(response.getPprbGuid());
        assertNull(response.getOperationGuid());
        assertNull(response.getBankCity());
        assertNull(response.getSettlementType());
        assertNull(response.getCounterpartyPhone());
        assertNull(response.getCounterpartyEmail());
    }

    @Test
    @AllureId("34045")
    void toCounterpartyTest1() {
        var partner = factory.manufacturePojo(PartnerEntity.class);
        var account = factory.manufacturePojo(AccountEntity.class);
        var bank = factory.manufacturePojo(BankEntity.class);
        var bankAccount = factory.manufacturePojo(BankAccountEntity.class);
        var sbbolGuid = UUID.randomUUID().toString();
        Counterparty response = mapper.toCounterparty(partner, account, bank, bankAccount, sbbolGuid);
        assertEquals(response.getPprbGuid(), sbbolGuid);
        assertEquals(response.getName(), partner.getOrgName());
        assertEquals(response.getTaxNumber(), partner.getInn());
        assertEquals(response.getKpp(), partner.getKpp());
        assertEquals(response.getDescription(), partner.getComment());
        assertEquals(response.getAccount(), account.getAccount());
        assertEquals(response.getSigned(), AccountStateType.SIGNED.equals(account.getState()));
        assertEquals(response.getBankName(), bank.getName());
        assertEquals(response.getBankBic(), bank.getBic());
        assertEquals(response.getCorrAccount(), bankAccount.getAccount());
        assertNull(response.getOperationGuid());
        assertNull(response.getBankCity());
        assertNull(response.getSettlementType());
        assertNull(response.getCounterpartyPhone());
        assertNull(response.getCounterpartyEmail());
    }

    @Test
    @AllureId("34068")
    void updateCounterpartyTest() {
        var counterparty = factory.manufacturePojo(Counterparty.class);
        var updatedCounterparty = (Counterparty) SerializationUtils.clone(counterparty);
        var partner = factory.manufacturePojo(Partner.class);
        mapper.updateCounterparty(updatedCounterparty, partner);
        assertEquals(updatedCounterparty.getPprbGuid(), counterparty.getPprbGuid());
        assertEquals(updatedCounterparty.getName(), partner.getOrgName());
        assertEquals(updatedCounterparty.getTaxNumber(), partner.getInn());
        assertEquals(updatedCounterparty.getKpp(), partner.getKpp());
        assertEquals(updatedCounterparty.getDescription(), partner.getComment());
        assertEquals(updatedCounterparty.getAccount(), counterparty.getAccount());
        assertEquals(updatedCounterparty.getSigned(), counterparty.getSigned());
        assertEquals(updatedCounterparty.getBankName(), counterparty.getBankName());
        assertEquals(updatedCounterparty.getBankBic(), counterparty.getBankBic());
        assertEquals(updatedCounterparty.getCorrAccount(), counterparty.getCorrAccount());
        assertEquals(updatedCounterparty.getOperationGuid(), counterparty.getOperationGuid());
        assertEquals(updatedCounterparty.getBankCity(), counterparty.getBankCity());
        assertEquals(updatedCounterparty.getSettlementType(), counterparty.getSettlementType());
        assertEquals(updatedCounterparty.getCounterpartyPhone(), counterparty.getCounterpartyPhone());
        assertEquals(updatedCounterparty.getCounterpartyEmail(), counterparty.getCounterpartyEmail());
    }

    @Test
    @AllureId("34099")
    void updateCounterpartyTest1() {
        var counterparty = factory.manufacturePojo(Counterparty.class);
        var updatedCounterparty = (Counterparty) SerializationUtils.clone(counterparty);
        var account = factory.manufacturePojo(AccountChange.class);
        var bank = factory.manufacturePojo(Bank.class);
        var bankAccount = factory.manufacturePojo(BankAccount.class);
        mapper.updateCounterparty(updatedCounterparty, account, bank, bankAccount);
        assertEquals(updatedCounterparty.getPprbGuid(), counterparty.getPprbGuid());
        assertEquals(updatedCounterparty.getName(), counterparty.getName());
        assertEquals(updatedCounterparty.getTaxNumber(), counterparty.getTaxNumber());
        assertEquals(updatedCounterparty.getKpp(), counterparty.getKpp());
        assertEquals(updatedCounterparty.getDescription(), counterparty.getDescription());
        assertEquals(updatedCounterparty.getAccount(), account.getAccount());
        assertEquals(updatedCounterparty.getSigned(), AccountChange.StateEnum.SIGNED.equals(account.getState()));
        assertEquals(updatedCounterparty.getBankName(), bank.getName());
        assertEquals(updatedCounterparty.getBankBic(), bank.getBic());
        assertEquals(updatedCounterparty.getCorrAccount(), bankAccount.getAccount());
        assertEquals(updatedCounterparty.getOperationGuid(), counterparty.getOperationGuid());
        assertEquals(updatedCounterparty.getBankCity(), counterparty.getBankCity());
        assertEquals(updatedCounterparty.getSettlementType(), counterparty.getSettlementType());
        assertEquals(updatedCounterparty.getCounterpartyPhone(), counterparty.getCounterpartyPhone());
        assertEquals(updatedCounterparty.getCounterpartyEmail(), counterparty.getCounterpartyEmail());
    }

    @Test
    @AllureId("34095")
    void toPartnerTest() {
        var counterparty = factory.manufacturePojo(Counterparty.class);
        String digitalId = randomAlphabetic(10);
        Partner partner = mapper.toPartner(counterparty, digitalId);
        assertNull(partner.getCitizenship());
        assertNull(partner.getEmails());
        assertNull(partner.getFirstName());
        assertNull(partner.getLegalForm());
        assertNull(partner.getMiddleName());
        assertNull(partner.getOgrn());
        assertNull(partner.getOkpo());
        assertNull(partner.getPhones());
        assertNull(partner.getSecondName());
        assertNull(partner.getVersion());
        assertEquals(partner.getDigitalId(), digitalId);
        assertEquals(partner.getId(), counterparty.getPprbGuid());
        assertEquals(partner.getOrgName(), counterparty.getName());
        assertEquals(partner.getInn(), counterparty.getTaxNumber());
        assertEquals(partner.getKpp(), counterparty.getKpp());
        assertEquals(partner.getComment(), counterparty.getDescription());
    }

    @Test
    @AllureId("34066")
    void toCounterpartyFilterTest() {
        var partnersFilter = factory.manufacturePojo(PartnersFilter.class);
        CounterpartyFilter filter = mapper.toCounterpartyFilter(partnersFilter);
        assertNull(filter.getHousingPaymentReceiever());
        assertNull(filter.getSearchPattern());
        assertNull(filter.getSigned());
        assertNull(filter.getOrderBy());
        assertNotNull(filter.getPagination());
        assertEquals(filter.getPagination().getOffset(), partnersFilter.getPagination().getOffset());
        assertEquals(filter.getPagination().getCount(), partnersFilter.getPagination().getCount());
        assertEquals(filter.getPagination().getHasNextPage(), partnersFilter.getPagination().getHasNextPage());
    }

    @Test
    @AllureId("34073")
    void toCounterpartyFilterTest1() {
        var accountsFilter = factory.manufacturePojo(AccountsFilter.class);
        CounterpartyFilter filter = mapper.toCounterpartyFilter(accountsFilter);
        assertNull(filter.getHousingPaymentReceiever());
        assertNull(filter.getSearchPattern());
        assertNull(filter.getSigned());
        assertNull(filter.getOrderBy());
        assertNotNull(filter.getPagination());
        assertEquals(filter.getPagination().getOffset(), accountsFilter.getPagination().getOffset());
        assertEquals(filter.getPagination().getCount(), accountsFilter.getPagination().getCount());
        assertEquals(filter.getPagination().getHasNextPage(), accountsFilter.getPagination().getHasNextPage());
    }

    @Test
    @AllureId("34057")
    void toPartnersTest() {
        List<CounterpartyView> counterparties = Collections.singletonList(factory.manufacturePojo(CounterpartyView.class));
        String digitalId = randomAlphabetic(10);
        List<Partner> partners = mapper.toPartners(counterparties, digitalId);
        assertNotNull(partners);
        assertEquals(partners.size(), counterparties.size());
        var partner = partners.get(0);
        var counterparty = counterparties.get(0);
        checkPartner(partner, counterparty, digitalId);
    }

    @Test
    @AllureId("34089")
    void toPartnerTest1() {
        CounterpartyView counterparty = factory.manufacturePojo(CounterpartyView.class);
        String digitalId = randomAlphabetic(10);
        Partner partner = mapper.toPartner(counterparty, digitalId);
        checkPartner(partner, counterparty, digitalId);
    }

    @Test
    @AllureId("34047")
    void toAccountsTest() {
        List<CounterpartyView> counterparties = Collections.singletonList(factory.manufacturePojo(CounterpartyView.class));
        String digitalId = randomAlphabetic(10);
        List<Account> accounts = mapper.toAccounts(counterparties, digitalId, Mockito.mock(BudgetMaskService.class));
        assertNotNull(accounts);
        assertEquals(accounts.size(), counterparties.size());
        var account = accounts.get(0);
        var counterparty = counterparties.get(0);
        checkAccount(account, counterparty, digitalId, null);
    }

    @Test
    @AllureId("34049")
    void toAccountTest() {
        CounterpartyView counterparty = factory.manufacturePojo(CounterpartyView.class);
        String digitalId = randomAlphabetic(10);
        UUID accountUuid = UUID.randomUUID();
        Account account = mapper.toAccount(counterparty, digitalId, accountUuid, Mockito.mock(BudgetMaskService.class));
        checkAccount(account, counterparty, digitalId, accountUuid.toString());
    }

    @Test
    @AllureId("34056")
    void toAccountTest1() {
        Counterparty counterparty = factory.manufacturePojo(Counterparty.class);
        String digitalId = randomAlphabetic(10);
        UUID accountUuid = UUID.randomUUID();
        Account account = mapper.toAccount(counterparty, digitalId, accountUuid, Mockito.mock(BudgetMaskService.class));
        assertNull(account.getName());
        assertNull(account.getVersion());
        assertFalse(account.getBudget());
        assertEquals(account.getId(), accountUuid.toString());
        assertEquals(account.getDigitalId(), digitalId);
        assertEquals(account.getPartnerId(), counterparty.getPprbGuid());
        assertEquals(account.getAccount(), counterparty.getAccount());
        assertEquals(account.getState(), counterparty.getSigned() ? Account.StateEnum.SIGNED : Account.StateEnum.NOT_SIGNED);
        assertNotNull(account.getBanks());
        var bank = account.getBanks().get(0);
        assertEquals(bank.getName(), counterparty.getBankName());
        assertEquals(bank.getBic(), counterparty.getBankBic());
        assertNotNull(bank.getBankAccounts());
        var bankAccount = bank.getBankAccounts().get(0);
        assertEquals(bankAccount.getAccount(), counterparty.getCorrAccount());
    }

    private void checkAccount(Account account, CounterpartyView counterparty, String digitalId, String accountUuid) {
        assertNull(account.getName());
        assertNull(account.getVersion());
        assertFalse(account.getBudget());
        if (accountUuid == null) {
            assertNull(account.getId());
        } else {
            assertEquals(account.getId(), accountUuid);
        }
        assertEquals(account.getDigitalId(), digitalId);
        assertEquals(account.getPartnerId(), counterparty.getPprbGuid());
        assertEquals(account.getAccount(), counterparty.getAccount());
        assertEquals(account.getState(), counterparty.isSigned() ? Account.StateEnum.SIGNED : Account.StateEnum.NOT_SIGNED);
        assertNotNull(account.getBanks());
        var bank = account.getBanks().get(0);
        assertEquals(bank.getName(), counterparty.getBankName());
        assertEquals(bank.getBic(), counterparty.getBankBic());
        assertNotNull(bank.getBankAccounts());
        var bankAccount = bank.getBankAccounts().get(0);
        assertEquals(bankAccount.getAccount(), counterparty.getBankAccount());
    }

    private void checkPartner(Partner partner, CounterpartyView counterparty, String digitalId) {
        assertNull(partner.getCitizenship());
        assertNull(partner.getEmails());
        assertNull(partner.getFirstName());
        assertNull(partner.getLegalForm());
        assertNull(partner.getMiddleName());
        assertNull(partner.getOgrn());
        assertNull(partner.getOkpo());
        assertNull(partner.getPhones());
        assertNull(partner.getSecondName());
        assertNull(partner.getVersion());
        assertEquals(partner.getDigitalId(), digitalId);
        assertEquals(partner.getId(), counterparty.getPprbGuid());
        assertEquals(partner.getOrgName(), counterparty.getName());
        assertEquals(partner.getInn(), counterparty.getTaxNumber());
        assertEquals(partner.getKpp(), counterparty.getKpp());
        assertEquals(partner.getComment(), counterparty.getDescription());
    }
}

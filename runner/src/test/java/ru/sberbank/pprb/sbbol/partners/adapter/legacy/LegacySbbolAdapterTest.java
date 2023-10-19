package ru.sberbank.pprb.sbbol.partners.adapter.legacy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.adapter.legacy.config.LegasyAdapterConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyCheckRequisites;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyCheckRequisitesResult;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyFilter;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyView;
import ru.sberbank.pprb.sbbol.partners.legacy.model.ListResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ContextConfiguration(classes = LegasyAdapterConfiguration.class)
class LegacySbbolAdapterTest extends BaseUnitConfiguration {

    private static final String DIGITAL_ID = randomAlphabetic(20);
    private static final String DIGITAL_USER_ID = randomAlphabetic(20);
    private static final String PPRB_GUID = UUID.randomUUID().toString();

    @Autowired
    private LegacySbbolAdapter adapter;

    /**
     * {@link LegacySbbolAdapter#checkNotMigration(String)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка миграции организации")
    void checkMigrationTest() {
        assertFalse(adapter.checkNotMigration(DIGITAL_ID));
    }

    /**
     * {@link LegacySbbolAdapter#checkRequisites(CounterpartyCheckRequisites)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка реквизитов контрагента")
    void checkRequisitesTest() {
        var request = factory.manufacturePojo(CounterpartyCheckRequisites.class);
        CounterpartyCheckRequisitesResult result = adapter.checkRequisites(request);
        assertNotNull(result.getPprbGuid());
        assertNotNull(result.getSigned());
    }


    /**
     * {@link LegacySbbolAdapter#update(String, Counterparty, String)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка обновления контрагента")
    void updateTest() {
        var request = factory.manufacturePojo(Counterparty.class);
        Counterparty result = adapter.update(DIGITAL_ID, request, null);
        checkCounterparty(result, request);
    }

    /**
     * {@link LegacySbbolAdapter#create(String, Counterparty, String)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка создания контрагента")
    void createTest() {
        var request = factory.manufacturePojo(Counterparty.class);
        Counterparty result = adapter.create(DIGITAL_ID, request, null);
        checkCounterparty(result, request);
    }

    /**
     * {@link LegacySbbolAdapter#getByPprbGuid(String, String)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка получения контрагента по ППРБ гуиду")
    void getByPprbGuidTest() {
        Counterparty result = adapter.getByPprbGuid(DIGITAL_ID, PPRB_GUID);
        assertEquals(PPRB_GUID, result.getPprbGuid());
    }

    /**
     * {@link LegacySbbolAdapter#delete(String, String, String)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка удаления по ППРБ гуиду")
    void deleteTest() {
        assertDoesNotThrow(() -> adapter.delete(DIGITAL_ID, PPRB_GUID, null));
    }


    /**
     * {@link LegacySbbolAdapter#list(String)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка получения списка по ППРБ гуиду")
    void listTest() {
        List<CounterpartyView> result = adapter.list(DIGITAL_ID);
        assertNotNull(result);
    }

    /**
     * {@link LegacySbbolAdapter#saveSign(String, CounterpartySignData, String)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка сохранения подписи")
    void saveSignTest() {
        var request = factory.manufacturePojo(CounterpartySignData.class);
        assertDoesNotThrow(() -> adapter.saveSign(DIGITAL_USER_ID, request, null));
    }

    /**
     * {@link LegacySbbolAdapter#removeSign(String, String, String)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка удаления подписи")
    void removeSignTest() {
        assertDoesNotThrow(() -> adapter.removeSign(DIGITAL_USER_ID, PPRB_GUID, null));
    }

    /**
     * {@link LegacySbbolAdapter#getHousingInn(String, Set)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка получения списка ИНН контрагентов")
    void getHousingInnTest() {
        Set<String> request = Set.of(randomNumeric(11), randomNumeric(11), randomNumeric(11), randomNumeric(11), randomNumeric(13));
        Set<String> result = adapter.getHousingInn(DIGITAL_ID, request);
        assertEquals(result, request);
    }

    /**
     * {@link LegacySbbolAdapter#viewRequest(String, CounterpartyFilter)}
     */
    @Test
    @DisplayName("Сббол Адаптер контрагенты. Проверка получения списка по ППРБ гуиду и фильтру")
    void viewTest() {
        var request = factory.manufacturePojo(CounterpartyFilter.class);
        ListResponse<CounterpartyView> result = adapter.viewRequest(DIGITAL_ID, request);
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertNotNull(result.getPagination());
    }

    private void checkCounterparty(Counterparty result, Counterparty expectedResult) {
        assertEquals(result.getOperationGuid(), expectedResult.getOperationGuid());
        assertEquals(result.getName(), expectedResult.getName());
        assertEquals(result.getTaxNumber(), expectedResult.getTaxNumber());
        assertEquals(result.getKpp(), expectedResult.getKpp());
        assertEquals(result.getAccount(), expectedResult.getAccount());
        assertEquals(result.getBankBic(), expectedResult.getBankBic());
        assertEquals(result.getCorrAccount(), expectedResult.getCorrAccount());
        assertEquals(result.getBankName(), expectedResult.getBankName());
        assertEquals(result.getBankCity(), expectedResult.getBankCity());
        assertEquals(result.getSettlementType(), expectedResult.getSettlementType());
        assertEquals(result.getPprbGuid(), expectedResult.getPprbGuid());
        assertEquals(result.getSigned(), expectedResult.getSigned());
        assertEquals(result.getCounterpartyPhone(), expectedResult.getCounterpartyPhone());
        assertEquals(result.getCounterpartyEmail(), expectedResult.getCounterpartyEmail());
        assertEquals(result.getDescription(), expectedResult.getDescription());
    }
}

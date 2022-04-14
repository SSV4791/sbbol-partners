package ru.sberbank.pprb.sbbol.partners.adapter.sbbol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.mockserver.model.Headers;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapterImpl;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.Counterparty;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyCheckRequisites;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyCheckRequisitesResult;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyFilter;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyView;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.ListResponse;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UnitTestLayer
@ExtendWith(SpringExtension.class)
@MockServerTest("sbbol.mock.url=http://localhost:${mockServerPort}/synapse-in-system-session/api/int-rest")
class LegacySbbolAdapterTest {

    private static final PodamFactory factory = new PodamFactoryImpl();
    private static final String DIGITAL_ID = randomAlphabetic(20);
    private static final String DIGITAL_USER_ID = randomAlphabetic(20);
    private static final String PPRB_GUID = UUID.randomUUID().toString();
    private static final String ROOT_URL = "/synapse-in-system-session/api/int-rest/counterparty";
    private static final String CHECK_REQUISITES = ROOT_URL + "/check-requisites";
    private static final String UPDATE = ROOT_URL + "/update/" + DIGITAL_ID;
    private static final String CREATE = ROOT_URL + "/create/" + DIGITAL_ID;
    private static final String BY_PPRB_GUID = ROOT_URL + "/" + DIGITAL_ID + "/" + PPRB_GUID;
    private static final String DELETE = ROOT_URL + "/delete/" + DIGITAL_ID + "/" + PPRB_GUID;
    private static final String COUNTERPARTY_BY_DIGITAL_ID = ROOT_URL + "/list/" + DIGITAL_ID;
    private static final String SIGN_SAVE = ROOT_URL + "/sign/save/" + DIGITAL_USER_ID;
    private static final String SIGN_REMOVE = ROOT_URL + "/sign/remove/" + DIGITAL_USER_ID + "/" + PPRB_GUID;
    private static final String VIEW = ROOT_URL + "/view/" + DIGITAL_ID;
    private static final String CHECK_MIGRATION = ROOT_URL + "/check-migration/" + DIGITAL_ID;
    private static final String GET_HOUSING_INN = ROOT_URL + "/housing/" + DIGITAL_ID;

    @Value("${sbbol.mock.url}")
    private String sbbolMockUrl;

    private LegacySbbolAdapter adapter;
    private MockServerClient mockServerClient;
    private ObjectMapper objectMapper;
    private Headers headers;

    @BeforeEach
    private void initTest() {
        RestTemplate restTemplate = new RestTemplate();
        objectMapper = JsonMapper.builder()
            .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));

        headers = new Headers(new Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE),
            new Header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));

        restTemplate.setMessageConverters(Collections.singletonList(converter));
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(sbbolMockUrl));
        adapter = new LegacySbbolAdapterImpl(restTemplate);

    }

    /**
     * {@link LegacySbbolAdapter#checkNotMigration(String)}
     */
    @Test
    @AllureId("34036")
    @DisplayName("Сббол Адаптер контрагенты. Проверка миграции организации")
    void checkMigrationTest() {
        mockServerClient.when(HttpRequest.request(CHECK_MIGRATION).withMethod(HttpMethod.GET.name()).withHeaders(headers))
            .respond(HttpResponse.response(getStringObject(true)).withHeaders(headers));
        assertFalse(adapter.checkNotMigration(DIGITAL_ID));
    }

    /**
     * {@link LegacySbbolAdapter#checkRequisites(CounterpartyCheckRequisites)}
     */
    @Test
    @AllureId("34041")
    @DisplayName("Сббол Адаптер контрагенты. Проверка реквизитов контрагента")
    void checkRequisitesTest() {
        var request = factory.manufacturePojo(CounterpartyCheckRequisites.class);
        var expectedResult = factory.manufacturePojo(CounterpartyCheckRequisitesResult.class);
        mockServerClient.when(HttpRequest.request(CHECK_REQUISITES).withMethod(HttpMethod.POST.name()).withHeaders(headers))
            .respond(HttpResponse.response(getStringObject(expectedResult)).withHeaders(headers));
        CounterpartyCheckRequisitesResult result = adapter.checkRequisites(request);
        assertEquals(result.getPprbGuid(), expectedResult.getPprbGuid());
        assertEquals(result.getSigned(), expectedResult.getSigned());
    }

    /**
     * {@link LegacySbbolAdapter#update(String, Counterparty)}
     */
    @Test
    @AllureId("34035")
    @DisplayName("Сббол Адаптер контрагенты. Проверка обновления контрагента")
    void updateTest() {
        var request = factory.manufacturePojo(Counterparty.class);
        mockServerClient.when(HttpRequest.request(UPDATE).withMethod(HttpMethod.PUT.name()).withHeaders(headers))
            .respond(HttpResponse.response(getStringObject(request)).withHeaders(headers));
        Counterparty result = adapter.update(DIGITAL_ID, request);
        checkCounterparty(result, request);
    }

    /**
     * {@link LegacySbbolAdapter#create(String, Counterparty)}
     */
    @Test
    @AllureId("34042")
    @DisplayName("Сббол Адаптер контрагенты. Проверка создания контрагента")
    void createTest() {
        var request = factory.manufacturePojo(Counterparty.class);
        mockServerClient.when(HttpRequest.request(CREATE).withMethod(HttpMethod.POST.name()).withHeaders(headers))
            .respond(HttpResponse.response(getStringObject(request)).withHeaders(headers));
        Counterparty result = adapter.create(DIGITAL_ID, request);
        checkCounterparty(result, request);
    }

    /**
     * {@link LegacySbbolAdapter#getByPprbGuid(String, String)}
     */
    @Test
    @AllureId("34040")
    @DisplayName("Сббол Адаптер контрагенты. Проверка получения контрагента по ППРБ гуиду")
    void getByPprbGuidTest() {
        var response = factory.manufacturePojo(Counterparty.class);
        mockServerClient.when(HttpRequest.request(BY_PPRB_GUID).withMethod(HttpMethod.GET.name()).withHeaders(headers))
            .respond(HttpResponse.response(getStringObject(response)).withHeaders(headers));
        Counterparty result = adapter.getByPprbGuid(DIGITAL_ID, PPRB_GUID);
        checkCounterparty(result, response);
    }

    /**
     * {@link LegacySbbolAdapter#delete(String, String)}
     */
    @Test
    @AllureId("34034")
    @DisplayName("Сббол Адаптер контрагенты. Проверка удаления по ППРБ гуиду")
    void deleteTest() {
        mockServerClient.when(HttpRequest.request(DELETE).withMethod(HttpMethod.DELETE.name()).withHeaders(headers))
            .respond(HttpResponse.response().withStatusCode(200).withHeaders(headers));
        adapter.delete(DIGITAL_ID, PPRB_GUID);
    }

    /**
     * {@link LegacySbbolAdapter#list(String)}
     */
    @Test
    @AllureId("34038")
    @DisplayName("Сббол Адаптер контрагенты. Проверка получения списка по ППРБ гуиду")
    @SuppressWarnings("unchecked")
    void listTest() {
        List<CounterpartyView> response = factory.manufacturePojo(ArrayList.class, CounterpartyView.class);
        mockServerClient.when(HttpRequest.request(COUNTERPARTY_BY_DIGITAL_ID).withMethod(HttpMethod.GET.name()).withHeaders(headers))
            .respond(HttpResponse.response(getStringObject(response)).withHeaders(headers));
        List<CounterpartyView> result = adapter.list(DIGITAL_ID);
        for (int i = 0; i < response.size(); i++) {
            checkCounterpartyView(response.get(i), result.get(i));
        }
    }

    /**
     * {@link LegacySbbolAdapter#saveSign(String, CounterpartySignData)}
     */
    @Test
    @AllureId("34043")
    @DisplayName("Сббол Адаптер контрагенты. Проверка сохранения подписи")
    void saveSignTest() {
        var request = factory.manufacturePojo(CounterpartySignData.class);
        mockServerClient.when(HttpRequest.request(SIGN_SAVE).withMethod(HttpMethod.POST.name()).withHeaders(headers))
            .respond(HttpResponse.response().withStatusCode(200).withHeaders(headers));
        adapter.saveSign(DIGITAL_USER_ID, request);
    }

    /**
     * {@link LegacySbbolAdapter#removeSign(String, String)}
     */
    @Test
    @AllureId("34039")
    @DisplayName("Сббол Адаптер контрагенты. Проверка удаления подписи")
    void removeSignTest() {
        mockServerClient.when(HttpRequest.request(SIGN_REMOVE).withMethod(HttpMethod.DELETE.name()).withHeaders(headers))
            .respond(HttpResponse.response().withStatusCode(200).withHeaders(headers));
        adapter.removeSign(DIGITAL_USER_ID, PPRB_GUID);
    }

    /**
     * {@link LegacySbbolAdapter#getHousingInn(String, Set)}
     */
    @Test
    @AllureId("34037")
    @DisplayName("Сббол Адаптер контрагенты. Проверка получения списка ИНН контрагентов")
    void getHousingInnTest() {
        Set<String> request = Set.of(randomNumeric(11), randomNumeric(11), randomNumeric(11), randomNumeric(11), randomNumeric(13));
        mockServerClient.when(HttpRequest.request(GET_HOUSING_INN).withMethod(HttpMethod.POST.name()).withHeaders(headers))
            .respond(HttpResponse.response(getStringObject(request)).withHeaders(headers));
        Set<String> result = adapter.getHousingInn(DIGITAL_ID, request);
        assertEquals(result, request);
    }

    /**
     * {@link LegacySbbolAdapter#viewRequest(String, CounterpartyFilter)}
     */
    @Test
    @AllureId("34033")
    @SuppressWarnings("unchecked")
    @DisplayName("Сббол Адаптер контрагенты. Проверка получения списка по ППРБ гуиду и фильтру")
    void viewTest() {
        var request = factory.manufacturePojo(CounterpartyFilter.class);
        var response = factory.manufacturePojo(ListResponse.class, CounterpartyView.class);
        mockServerClient.when(HttpRequest.request(VIEW).withMethod(HttpMethod.POST.name()).withHeaders(headers))
            .respond(HttpResponse.response(getStringObject(response)).withHeaders(headers));
        ListResponse<CounterpartyView> result = adapter.viewRequest(DIGITAL_ID, request);
        assertNotNull(result.getItems());
        assertNotNull(result.getPagination());
        assertEquals(result.getPagination().getCount(), response.getPagination().getCount());
        assertEquals(result.getPagination().getHasNextPage(), response.getPagination().getHasNextPage());
        assertEquals(result.getPagination().getOffset(), response.getPagination().getOffset());
        List<CounterpartyView> resultList = result.getItems();
        resultList.sort(Comparator.comparing(CounterpartyView::getName));
        List<CounterpartyView> responseList = response.getItems();
        responseList.sort(Comparator.comparing(CounterpartyView::getName));
        for (int i = 0; i < resultList.size(); i++) {
            checkCounterpartyView(resultList.get(i), responseList.get(i));
        }
    }

    /**
     * Получить String представление объекта
     *
     * @param object объект маппинга
     * @return String представление объекта
     */
    private String getStringObject(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("При преобразовании объекта в json произошла ошибка", e);
        }
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

    private void checkCounterpartyView(CounterpartyView result, CounterpartyView expectedResult) {
        assertEquals(result.getName(), expectedResult.getName());
        assertEquals(result.getTaxNumber(), expectedResult.getTaxNumber());
        assertEquals(result.getKpp(), expectedResult.getKpp());
        assertEquals(result.getAccount(), expectedResult.getAccount());
        assertEquals(result.getBankBic(), expectedResult.getBankBic());
        assertEquals(result.getBankAccount(), expectedResult.getBankAccount());
        assertEquals(result.getBankName(), expectedResult.getBankName());
        assertEquals(result.getBankCity(), expectedResult.getBankCity());
        assertEquals(result.getBankSettlementType(), expectedResult.getBankSettlementType());
        assertEquals(result.getPprbGuid(), expectedResult.getPprbGuid());
        assertEquals(result.isSigned(), expectedResult.isSigned());
        assertEquals(result.getHousingServicesProvider(), expectedResult.getHousingServicesProvider());
        assertEquals(result.getDescription(), expectedResult.getDescription());
    }
}

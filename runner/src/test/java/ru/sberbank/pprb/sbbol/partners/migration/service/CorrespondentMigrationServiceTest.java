package ru.sberbank.pprb.sbbol.partners.migration.service;

import io.qameta.allure.AllureId;
import io.restassured.common.mapper.TypeRef;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationLegalType;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigratedCorrespondentData;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcRequest;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcResponse;
import ru.sberbank.pprb.sbbol.partners.migration.model.MigrateCorrespondentRequest;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class CorrespondentMigrationServiceTest extends AbstractIntegrationTest {

    private static final String DIGITAL_ID = RandomStringUtils.randomAlphanumeric(20);
    private static final String JSON_RPC_REQUEST_ID = RandomStringUtils.randomAlphanumeric(10);
    private static final String JSON_RPC_VERSION = "2.0";
    private static final String REMOTE_METHOD_NAME = "migrate";
    private static final String URI_REMOTE_SERVICE = "/correspondents/migrate";
    private static final int GENERATE_COUNTERPARTIES_COUNT = 10;

    private final JsonRpcRequest<MigrateCorrespondentRequest> request = new JsonRpcRequest<>(
        JSON_RPC_REQUEST_ID,
        JSON_RPC_VERSION,
        REMOTE_METHOD_NAME,
        null
    );

    @Test
    @AllureId("34177")
    void migrateAndCreateFewCorrespondentsTest() {
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, generateCorrespondents(GENERATE_COUNTERPARTIES_COUNT)));
        JsonRpcResponse<List<MigratedCorrespondentData>> response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(JSON_RPC_REQUEST_ID);
        assertThat(response.getJsonrpc()).isEqualTo(JSON_RPC_VERSION);
        List<MigratedCorrespondentData> correspondentsData = response.getResult();
        assertThat(CollectionUtils.isEmpty(correspondentsData)).isFalse();
        assertThat(correspondentsData.size()).isEqualTo(GENERATE_COUNTERPARTIES_COUNT);
        correspondentsData.forEach(data -> {
            assertThat(data.getPprbGuid()).isNotNull();
            assertThat(data.getSbbolReplicationGuid()).isNotNull();
            assertThat(data.getVersion()).isNotNull();
        });
    }

    @Test
    @AllureId("34162")
    void migrateAndCreateCorrespondentTest() {
        MigrationCorrespondentCandidate generatedCorrespondent = generateCorrespondent();
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, List.of(generatedCorrespondent)));
        JsonRpcResponse<List<MigratedCorrespondentData>> response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        MigratedCorrespondentData correspondentData = response.getResult().get(0);
        assertThat(correspondentData.getVersion()).isEqualTo(generatedCorrespondent.getVersion());
        assertThat(correspondentData.getSbbolReplicationGuid()).isEqualTo(generatedCorrespondent.getReplicationGuid());
        generatedCorrespondent.setVersion(1);
        generatedCorrespondent.setReplicationGuid(RandomStringUtils.randomAlphanumeric(10));
        response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        correspondentData = response.getResult().get(0);
        assertThat(correspondentData.getVersion()).isEqualTo(generatedCorrespondent.getVersion());
        assertThat(correspondentData.getSbbolReplicationGuid()).isEqualTo(generatedCorrespondent.getReplicationGuid());
    }

    @Test
    @AllureId("35873")
    void migrateAndUpdateCorrespondentTest() {
        MigrationCorrespondentCandidate generatedCorrespondent = generateCorrespondent();
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, List.of(generatedCorrespondent)));
        JsonRpcResponse<List<MigratedCorrespondentData>> response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        MigratedCorrespondentData correspondentData = response.getResult().get(0);
        assertThat(correspondentData.getVersion()).isEqualTo(generatedCorrespondent.getVersion());
        assertThat(correspondentData.getSbbolReplicationGuid()).isEqualTo(generatedCorrespondent.getReplicationGuid());
        generatedCorrespondent.setVersion(1);
        generatedCorrespondent.setReplicationGuid(RandomStringUtils.randomAlphanumeric(10));
        generatedCorrespondent.setPprbGuid(correspondentData.getPprbGuid());
        response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(JSON_RPC_REQUEST_ID);
        assertThat(response.getJsonrpc()).isEqualTo(JSON_RPC_VERSION);
        List<MigratedCorrespondentData> correspondentsData = response.getResult();
        assertThat(CollectionUtils.isEmpty(correspondentsData)).isFalse();
        assertThat(correspondentsData.size()).isEqualTo(1);
        correspondentsData.forEach(data -> {
            assertThat(data.getPprbGuid()).isEqualTo(correspondentData.getPprbGuid());
            assertThat(data.getSbbolReplicationGuid()).isNotNull();
            assertThat(data.getVersion()).isNotNull();
        });

    }

    @Test
    @AllureId("34116")
    void migrateInvalidCorrespondentTest() {
        MigrationCorrespondentCandidate generatedCorrespondent = generateCorrespondentWithLongInn();
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, List.of(generatedCorrespondent)));
        postWithInternalServerErrorExpected(URI_REMOTE_SERVICE, request);
    }

    private MigrationCorrespondentCandidate generateCorrespondentWithLongInn() {
        MigrationCorrespondentCandidate correspondent = generateCorrespondent();
        correspondent.setInn(RandomStringUtils.randomAlphanumeric(20));
        return correspondent;
    }

    private MigrationCorrespondentCandidate generateCorrespondent() {
        return generateCorrespondents(1).get(0);
    }

    private List<MigrationCorrespondentCandidate> generateCorrespondents(int count) {
        List<MigrationCorrespondentCandidate> generatedCorrespondents = new ArrayList<>(count);
        String eightSymbolsRandomString = RandomStringUtils.randomAlphanumeric(8);
        for (int i = 0; i < count; i++) {
            String randomReplicationGuid = RandomStringUtils.randomAlphanumeric(8);
            MigrationCorrespondentCandidate correspondent = new MigrationCorrespondentCandidate();
            correspondent.setName(eightSymbolsRandomString);
            correspondent.setInn(eightSymbolsRandomString);
            correspondent.setKpp(eightSymbolsRandomString);
            correspondent.setAccount(eightSymbolsRandomString);
            correspondent.setBic(eightSymbolsRandomString);
            correspondent.setDescription(eightSymbolsRandomString);
            correspondent.setReplicationGuid(randomReplicationGuid);
            correspondent.setCorrPhoneNumber(eightSymbolsRandomString);
            correspondent.setCorrEmail(eightSymbolsRandomString);
            correspondent.setBankAccount(eightSymbolsRandomString);
            correspondent.setVersion(0);
            correspondent.setSigned(false);
            correspondent.setLegalType(MigrationLegalType.LEGAL_ENTITY);
            generatedCorrespondents.add(correspondent);
        }
        return generatedCorrespondents;
    }
}

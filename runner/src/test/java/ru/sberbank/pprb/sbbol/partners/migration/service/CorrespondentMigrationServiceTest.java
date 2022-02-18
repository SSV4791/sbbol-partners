package ru.sberbank.pprb.sbbol.partners.migration.service;

import io.restassured.common.mapper.TypeRef;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationLegalType;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcRequest;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcResponse;
import ru.sberbank.pprb.sbbol.partners.migration.model.MigrateCorrespondentRequest;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigratedCorrespondentData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// TODO DCBBRAIN-2268 Перенести тесты и модели для migration-service из модуля Runner в свой модуль
public class CorrespondentMigrationServiceTest extends AbstractIntegrationWithOutSbbolTest {

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
    void migrateFewCorrespondentsTest() {
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, generateCorrespondents(GENERATE_COUNTERPARTIES_COUNT)));
        JsonRpcResponse<List<MigratedCorrespondentData>> response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {});
        assertNotNull(response);
        assertEquals(response.getId(), JSON_RPC_REQUEST_ID);
        assertEquals(response.getJsonrpc(), JSON_RPC_VERSION);
        List<MigratedCorrespondentData> correspondentsData = response.getResult();
        assertFalse(CollectionUtils.isEmpty(correspondentsData));
        assertEquals(correspondentsData.size(), GENERATE_COUNTERPARTIES_COUNT);
        correspondentsData.forEach(data -> {
            assertNotNull(data.getPprbGuid());
            assertNotNull(data.getSbbolReplicationGuid());
            assertNotNull(data.getVersion());
        });
    }

    @Test
    void updateMigratedCorrespondentTest() {
        MigrationCorrespondentCandidate generatedCorrespondent = generateCorrespondent();
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, List.of(generatedCorrespondent)));
        JsonRpcResponse<List<MigratedCorrespondentData>> response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {});
        MigratedCorrespondentData correspondentData = response.getResult().get(0);
        assertEquals(correspondentData.getVersion(), generatedCorrespondent.getVersion());
        assertEquals(correspondentData.getSbbolReplicationGuid(), generatedCorrespondent.getReplicationGuid());
        generatedCorrespondent.setVersion(1);
        generatedCorrespondent.setReplicationGuid(RandomStringUtils.randomAlphanumeric(10));
        response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {});
        correspondentData = response.getResult().get(0);
        assertEquals(correspondentData.getVersion(), generatedCorrespondent.getVersion());
        assertEquals(correspondentData.getSbbolReplicationGuid(), generatedCorrespondent.getReplicationGuid());
    }

    @Test
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
            correspondent.setPprbGuid(eightSymbolsRandomString);
            correspondent.setBankAccount(eightSymbolsRandomString);
            correspondent.setVersion(0);
            correspondent.setSigned(false);
            correspondent.setLegalType(MigrationLegalType.LEGAL_ENTITY);
            generatedCorrespondents.add(correspondent);
        }
        return generatedCorrespondents;
    }
}

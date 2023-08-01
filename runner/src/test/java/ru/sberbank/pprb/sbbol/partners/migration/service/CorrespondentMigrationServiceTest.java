package ru.sberbank.pprb.sbbol.partners.migration.service;

import io.restassured.common.mapper.TypeRef;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigratedCorrespondentData;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentResponse;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationReplicationGuidCandidate;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcRequest;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcResponse;
import ru.sberbank.pprb.sbbol.partners.migration.model.MigrateCorrespondentRequest;
import ru.sberbank.pprb.sbbol.partners.migration.model.MigrateReplicationGuidRequest;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;
import ru.sberbank.pprb.sbbol.partners.rest.partner.BaseAccountControllerTest;
import ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
    void migrateAndCreateFewCorrespondentsTest() {
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, generateCorrespondents(GENERATE_COUNTERPARTIES_COUNT)));
        JsonRpcResponse<MigrationCorrespondentResponse> response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(JSON_RPC_REQUEST_ID);
        assertThat(response.getJsonrpc()).isEqualTo(JSON_RPC_VERSION);
        assertThat(response.getResult()).isNotNull();
        List<MigratedCorrespondentData> correspondentsData = response.getResult().getCorrespondents();
        assertThat(CollectionUtils.isEmpty(correspondentsData)).isFalse();
        assertThat(correspondentsData).hasSize(GENERATE_COUNTERPARTIES_COUNT);
        correspondentsData.forEach(data -> {
            assertThat(data.getPprbGuid()).isNotNull();
            assertThat(data.getSbbolReplicationGuid()).isNotNull();
            assertThat(data.getVersion()).isNotNull();
        });
    }

    @Test
    void migrateAndCreateFewDuplicationCorrespondentsTest() {
        String kpp = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        String inn = RandomStringUtils.randomAlphanumeric(8);
        var correspondents = generateCorrespondents(GENERATE_COUNTERPARTIES_COUNT).stream()
            .peek(value -> {
                value.setInn(inn);
                value.setName(name);
                value.setKpp(kpp);
                value.setAccount(RandomStringUtils.randomAlphanumeric(20));
                value.setBic(RandomStringUtils.randomAlphanumeric(9));
                value.setBankAccount(RandomStringUtils.randomAlphanumeric(20));
            })
            .collect(Collectors.toList());
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, correspondents));
        JsonRpcResponse<MigrationCorrespondentResponse> response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(JSON_RPC_REQUEST_ID);
        assertThat(response.getJsonrpc()).isEqualTo(JSON_RPC_VERSION);
        assertThat(response.getResult()).isNotNull();
        List<MigratedCorrespondentData> correspondentsData = response.getResult().getCorrespondents();
        assertThat(CollectionUtils.isEmpty(correspondentsData)).isFalse();
        assertThat(correspondentsData).hasSize(GENERATE_COUNTERPARTIES_COUNT);
        correspondentsData.forEach(data -> {
            assertThat(data.getPprbGuid()).isNotNull();
            assertThat(data.getSbbolReplicationGuid()).isNotNull();
            assertThat(data.getVersion()).isNotNull();
        });
    }

    @Test
    void migrateAndCreateCorrespondentTest() {
        MigrationCorrespondentCandidate generatedCorrespondent = generateCorrespondent();
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, List.of(generatedCorrespondent)));
        JsonRpcResponse<MigrationCorrespondentResponse> response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        assertThat(response.getResult()).isNotNull();
        var correspondents = response.getResult().getCorrespondents();
        assertThat(correspondents).isNotNull();
        MigratedCorrespondentData correspondentData = correspondents.get(0);
        assertThat(correspondentData.getSbbolReplicationGuid()).isEqualTo(generatedCorrespondent.getReplicationGuid());
        generatedCorrespondent.setVersion(1);
        generatedCorrespondent.setReplicationGuid(UUID.randomUUID().toString());
        response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        correspondentData = response.getResult().getCorrespondents().get(0);
        assertThat(correspondentData.getVersion()).isEqualTo(generatedCorrespondent.getVersion());
        assertThat(correspondentData.getSbbolReplicationGuid()).isEqualTo(generatedCorrespondent.getReplicationGuid());
    }

    @Test
    void migrateAndUpdateCorrespondentTest() {
        MigrationCorrespondentCandidate generatedCorrespondent = generateCorrespondent();
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, List.of(generatedCorrespondent)));
        JsonRpcResponse<MigrationCorrespondentResponse> response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        assertThat(response.getResult()).isNotNull();
        var correspondents = response.getResult().getCorrespondents();
        assertThat(correspondents).isNotNull();
        MigratedCorrespondentData correspondentData = correspondents.get(0);
        assertThat(correspondentData.getSbbolReplicationGuid()).isEqualTo(generatedCorrespondent.getReplicationGuid());
        generatedCorrespondent.setVersion(1);
        generatedCorrespondent.setReplicationGuid(UUID.randomUUID().toString());
        generatedCorrespondent.setPprbGuid(correspondentData.getPprbGuid());
        response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(JSON_RPC_REQUEST_ID);
        assertThat(response.getJsonrpc()).isEqualTo(JSON_RPC_VERSION);
        assertThat(CollectionUtils.isEmpty(correspondents)).isFalse();
        assertThat(correspondents).hasSize(1);
        correspondents.forEach(data -> {
            assertThat(data.getPprbGuid()).isEqualTo(correspondentData.getPprbGuid());
            assertThat(data.getSbbolReplicationGuid()).isNotNull();
            assertThat(data.getVersion()).isNotNull();
        });

    }

    @Test
    void migrateInvalidCorrespondentTest() {
        MigrationCorrespondentCandidate generatedCorrespondent = generateCorrespondentWithLongInn();
        request.setParams(new MigrateCorrespondentRequest(DIGITAL_ID, List.of(generatedCorrespondent)));
        assertDoesNotThrow(() -> postWithInternalServerErrorExpected(URI_REMOTE_SERVICE, request));
    }

    @Test
    void migrateReplicationGuidTest() {
        Partner partner = PartnerControllerTest.createValidPartner();
        var account = BaseAccountControllerTest.createValidAccount(partner.getId(), partner.getDigitalId());
        assertDoesNotThrow(() -> post(
            URI_REMOTE_SERVICE,
            new JsonRpcRequest<>(
                JSON_RPC_REQUEST_ID,
                JSON_RPC_VERSION,
                "migrateReplicationGuid",
                new MigrateReplicationGuidRequest(
                    account.getDigitalId(),
                    List.of(
                        new MigrationReplicationGuidCandidate(UUID.randomUUID().toString(), account.getId())
                    ))
            ),
            new TypeRef<>() {
            })
        );
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
        for (int i = 0; i < count; i++) {
            String randomReplicationGuid = UUID.randomUUID().toString();
            MigrationCorrespondentCandidate correspondent = new MigrationCorrespondentCandidate();
            correspondent.setReplicationGuid(randomReplicationGuid);
            correspondent.setName(RandomStringUtils.randomAlphanumeric(8) + " " + RandomStringUtils.randomAlphanumeric(8));
            correspondent.setInn(RandomStringUtils.randomAlphanumeric(8));
            correspondent.setKpp(RandomStringUtils.randomAlphanumeric(8));
            correspondent.setAccount(RandomStringUtils.randomAlphanumeric(8));
            correspondent.setBic(RandomStringUtils.randomAlphanumeric(8));
            correspondent.setDescription(RandomStringUtils.randomAlphanumeric(8));
            correspondent.setReplicationGuid(UUID.randomUUID().toString());
            correspondent.setCorrPhoneNumber(RandomStringUtils.randomAlphanumeric(8));
            correspondent.setCorrEmail(RandomStringUtils.randomAlphanumeric(8));
            correspondent.setBankAccount(RandomStringUtils.randomAlphanumeric(8));
            correspondent.setVersion(0);
            correspondent.setSigned(false);
            generatedCorrespondents.add(correspondent);
        }
        return generatedCorrespondents;
    }
}

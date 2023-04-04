package ru.sberbank.pprb.sbbol.partners.migration.service;

import io.restassured.common.mapper.TypeRef;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcRequest;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcResponse;
import ru.sberbank.pprb.sbbol.partners.migration.model.MigrateGkuMigrateRequest;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GkuInnDictionaryRepository;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Collection;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GkuMigrationServiceTest extends AbstractIntegrationTest {

    @Autowired
    private PodamFactory podamFactory;

    @Autowired
    private GkuInnDictionaryRepository gkuInnDictionaryRepository;

    private static final String JSON_RPC_REQUEST_ID = RandomStringUtils.randomAlphanumeric(10);
    private static final String JSON_RPC_VERSION = "2.0";
    private static final String REMOTE_MIGRATE_METHOD_NAME = "migrate";
    private static final String REMOTE_DELETE_METHOD_NAME = "delete";
    private static final String URI_REMOTE_SERVICE = "/gku/migrate";

    private final JsonRpcRequest<MigrateGkuMigrateRequest> requestMigrate = new JsonRpcRequest<>(
        JSON_RPC_REQUEST_ID,
        JSON_RPC_VERSION,
        REMOTE_MIGRATE_METHOD_NAME,
        null
    );

    private final JsonRpcRequest<Void> requestDelete = new JsonRpcRequest<>(
        JSON_RPC_REQUEST_ID,
        JSON_RPC_VERSION,
        REMOTE_DELETE_METHOD_NAME,
        null
    );

    @Test
    @DisplayName("POST /gku/migrate Миграция справочника ЖКУ")
    void migrationGKU() {
        step("Подготовка данных для запроса", () -> {
            var collection = podamFactory.manufacturePojo(Collection.class, MigrationGkuCandidate.class);
            requestMigrate.setParams(new MigrateGkuMigrateRequest(collection));
            return collection;
        });
        JsonRpcResponse<Void> response = step("Выполнение post-запроса /gku/migrate, код ответа 200", () ->
            post(URI_REMOTE_SERVICE, requestMigrate, new TypeRef<>() {
            })
        );
        step("Проверка корректности ответа", () -> {
            assertNotNull(response);
            assertEquals(response.getId(), JSON_RPC_REQUEST_ID);
            assertEquals(response.getJsonrpc(), JSON_RPC_VERSION);
        });
    }

    @Test
    @DisplayName("POST /gku/migrate Миграция справочника ЖКУ удаление")
    void deleteGKU() {
        GkuInnEntity saved = step("Подготовка данных для запроса", () -> {
            var entity = podamFactory.manufacturePojo(GkuInnEntity.class);
            var savedEntity = gkuInnDictionaryRepository.save(entity);
            var foundInnBeforeDelete = gkuInnDictionaryRepository.getByInn(savedEntity.getInn());
            assertNotNull(foundInnBeforeDelete);
            return savedEntity;
        });
        JsonRpcResponse<Void> response =
            step("Выполнение post-запроса /gku/migrate, код ответа 200", () ->
                post(URI_REMOTE_SERVICE, requestDelete, new TypeRef<>() {
                }));
        step("Проверка корректности ответа", () -> {
            assertNotNull(response);
            assertEquals(response.getId(), JSON_RPC_REQUEST_ID);
            assertEquals(response.getJsonrpc(), JSON_RPC_VERSION);

            var foundInnAfterDelete = gkuInnDictionaryRepository.getByInn(saved.getInn());
            assertNotNull(foundInnAfterDelete);
        });
    }

    @Test
    @DisplayName("POST /gku/migrate Миграция справочника ЖКУ удаление")
    void deleteGKU_batch() {
        step("Подготовка данных для запроса", () -> {
            for (int i = 0; i < 50; i++) {
                var entity = podamFactory.manufacturePojo(GkuInnEntity.class);
                gkuInnDictionaryRepository.save(entity);
            }
        });
        JsonRpcResponse<Void> response = step("Выполнение post-запроса /gku/migrate, код ответа 200", () ->
            post(URI_REMOTE_SERVICE, requestDelete, new TypeRef<>() {
            }));
        step("Проверка корректности ответа", () -> {
            assertNotNull(response);
            assertEquals(response.getId(), JSON_RPC_REQUEST_ID);
            assertEquals(response.getJsonrpc(), JSON_RPC_VERSION);
            Iterable<GkuInnEntity> allInn = gkuInnDictionaryRepository.findAll();
            assertThat(allInn).isNotEmpty();
        });
    }
}
